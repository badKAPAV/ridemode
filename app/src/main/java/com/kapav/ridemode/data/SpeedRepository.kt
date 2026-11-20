package com.kapav.ridemode.data

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Build
import android.os.Looper
import android.os.SystemClock
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

internal const val SPEED_MAX_AGE_MS = 5_000L
internal const val SPEED_MAX_ERROR_MPS = 3f
internal fun validSpeedSample(speed: Float?, error: Float?, ageMs: Long): Boolean =
    speed != null && speed.isFinite() && speed >= 0 && ageMs in 0..SPEED_MAX_AGE_MS &&
        (error == null || (error.isFinite() && error in 0f..SPEED_MAX_ERROR_MPS))

enum class SpeedStatus(val label: String) {
    PAUSED("Paused"), PERMISSION("Allow precise location"), DISABLED("Turn on GPS"),
    SEARCHING("Waiting for GPS"), LIVE("GPS"), UNAVAILABLE("GPS unavailable"), WEAK("Weak GPS signal")
}
data class SpeedState(val status: SpeedStatus = SpeedStatus.PAUSED, val metersPerSecond: Float? = null,
    val accuracyMps: Float? = null, val simulated: Boolean = false)

/** One foreground-only GPS subscription, independent of map/routing and collected only by speed tiles. */
@Singleton
class SpeedRepository @Inject constructor(@ApplicationContext private val context: Context) {
    private val manager = context.getSystemService(LocationManager::class.java)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val _state = MutableStateFlow(SpeedState())
    val state = _state.asStateFlow()
    private var active = false
    private var generation = 0
    private var listener: LocationListener? = null
    private var expiry: Job? = null
    private val providers = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) { refresh() }
    }
    fun start() {
        if (active) return
        active = true
        val filter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        if (Build.VERSION.SDK_INT >= 33) context.registerReceiver(providers, filter, Context.RECEIVER_NOT_EXPORTED)
        else { @Suppress("DEPRECATION") context.registerReceiver(providers, filter) }
        refresh()
    }
    fun stop() {
        if (!active) return
        active = false
        context.unregisterReceiver(providers)
        clearRequest()
        _state.value = SpeedState()
    }
    private fun clearRequest() {
        generation++
        expiry?.cancel(); expiry = null
        listener?.let { runCatching { manager.removeUpdates(it) } }
        listener = null
    }
    fun refresh() {
        if (!active) return
        clearRequest()
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            _state.value = SpeedState(SpeedStatus.PERMISSION); return
        }
        if (LocationManager.GPS_PROVIDER !in manager.allProviders) {
            _state.value = SpeedState(SpeedStatus.UNAVAILABLE); return
        }
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            _state.value = SpeedState(SpeedStatus.DISABLED); return
        }
        _state.value = SpeedState(SpeedStatus.SEARCHING)
        val request = generation
        val callback = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                if (!active || request != generation) return
                val speed = location.speed.takeIf { location.hasSpeed() }
                val error = if (Build.VERSION.SDK_INT >= 26 && location.hasSpeedAccuracy()) location.speedAccuracyMetersPerSecond else null
                val age = SystemClock.elapsedRealtime() - location.elapsedRealtimeNanos / 1_000_000
                expiry?.cancel()
                if (!validSpeedSample(speed, error, age)) {
                    _state.value = SpeedState(SpeedStatus.WEAK); return
                }
                val simulated = if (Build.VERSION.SDK_INT >= 31) location.isMock else {
                    @Suppress("DEPRECATION") location.isFromMockProvider
                }
                _state.value = SpeedState(SpeedStatus.LIVE, speed, error, simulated)
                expiry = scope.launch {
                    delay(SPEED_MAX_AGE_MS - age + 1)
                    if (active && request == generation) _state.value = SpeedState(SpeedStatus.UNAVAILABLE)
                }
            }
            override fun onProviderDisabled(provider: String) { if (active && request == generation) refresh() }
            override fun onProviderEnabled(provider: String) { if (active && request == generation) refresh() }
            // Explicit implementations keep LocationListener compatible with API 24–29.
            @Deprecated("Legacy platform callback")
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) = Unit
        }
        listener = callback
        try { manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1_000L, 0f, callback, Looper.getMainLooper()) }
        catch (_: SecurityException) { clearRequest(); _state.value = SpeedState(SpeedStatus.PERMISSION) }
        catch (_: IllegalArgumentException) { clearRequest(); _state.value = SpeedState(SpeedStatus.UNAVAILABLE) }
    }
}
