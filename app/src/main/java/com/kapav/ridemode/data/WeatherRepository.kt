package com.kapav.ridemode.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Looper
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import kotlin.math.roundToInt

data class WeatherReading(
    val latitude: Double, val longitude: Double, val temperatureC: Int,
    val code: Int, val fetchedAtMs: Long
)

data class WeatherState(val reading: WeatherReading? = null, val loading: Boolean = false) {
    fun displayReading(nowMs: Long): WeatherReading? = reading?.takeIf {
        nowMs >= it.fetchedAtMs && nowMs - it.fetchedAtMs <= MAX_DISPLAY_AGE_MS
    }
}

internal const val WEATHER_REFRESH_MS = 30 * 60_000L
internal const val MAX_DISPLAY_AGE_MS = 2 * 60 * 60_000L
internal const val LOCATION_MAX_AGE_MS = 24 * 60 * 60_000L
internal const val LOCATION_CHANGE_METERS = 10_000f
internal fun validWeatherCode(code: Int): Boolean = code in 0..3 || code == 45 || code == 48 ||
    code in 51..67 || code in 71..77 || code in 80..82 || code == 85 || code == 86 || code in 95..99
internal fun weatherRetryDelay(failures: Int): Long = when (failures) {
    1 -> 60_000L
    2 -> 5 * 60_000L
    3 -> 15 * 60_000L
    else -> 30 * 60_000L
}

/** Foreground-only weather source. One request is shared by all Clock and OLED renderers. */
@Singleton
class WeatherRepository @Inject constructor(@ApplicationContext private val context: Context) {
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val prefs = context.getSharedPreferences("weather_cache", Context.MODE_PRIVATE)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val owners = mutableSetOf<String>()
    private val _state = MutableStateFlow(WeatherState())
    val state = _state.asStateFlow()
    private var location: Location? = null
    private var fetchJob: Job? = null
    private var locationTimeout: Job? = null
    private var lastAttemptMs = 0L
    private var networkRegistered = false
    private var locationRegistered = false

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(newLocation: Location) = acceptLocation(newLocation)
        override fun onProviderEnabled(provider: String) = resumeLocation()
        override fun onProviderDisabled(provider: String) {
            if (provider == LocationManager.NETWORK_PROVIDER) {
                location = null
                fetchJob?.cancel()
                _state.value = WeatherState()
            }
        }
        @Deprecated("Older Android callback")
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) = Unit
    }
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            scope.launch {
                val now = System.currentTimeMillis()
                val readingAge = _state.value.reading?.let { now - it.fetchedAtMs }
                if (owners.isNotEmpty() && now - lastAttemptMs >= 60_000L &&
                    (readingAge == null || readingAge >= WEATHER_REFRESH_MS)) {
                    scheduleFetch(force = true)
                }
            }
        }
    }

    fun start(owner: String) {
        if (!owners.add(owner)) return
        if (owners.size == 1) {
            if (!networkRegistered) {
                runCatching { connectivity.registerDefaultNetworkCallback(networkCallback) }
                    .onSuccess { networkRegistered = true }
            }
            resumeLocation()
        }
    }

    fun stop(owner: String) {
        owners.remove(owner)
        if (owners.isNotEmpty()) return
        fetchJob?.cancel(); fetchJob = null
        locationTimeout?.cancel(); locationTimeout = null
        if (locationRegistered) {
            runCatching { locationManager.removeUpdates(locationListener) }
            locationRegistered = false
        }
        if (networkRegistered) {
            runCatching { connectivity.unregisterNetworkCallback(networkCallback) }
            networkRegistered = false
        }
    }

    fun refresh() {
        if (owners.isNotEmpty()) resumeLocation()
    }

    private fun resumeLocation() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
            location = null
            fetchJob?.cancel()
            _state.value = WeatherState()
            return
        }
        try {
            if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                location = null
                fetchJob?.cancel()
                _state.value = WeatherState()
                return
            }
            val cached = listOf(LocationManager.NETWORK_PROVIDER, LocationManager.PASSIVE_PROVIDER)
                .mapNotNull { runCatching { locationManager.getLastKnownLocation(it) }.getOrNull() }
                .filter { System.currentTimeMillis() - it.time in 0..LOCATION_MAX_AGE_MS }
                .maxByOrNull { it.time }
            if (cached != null) acceptLocation(cached)
            if (!locationRegistered) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    15 * 60_000L, LOCATION_CHANGE_METERS, locationListener, Looper.getMainLooper())
                locationRegistered = true
            }
            if (location == null) {
                _state.value = WeatherState(loading = true)
                locationTimeout?.cancel()
                locationTimeout = scope.launch {
                    delay(20_000)
                    if (location == null) _state.value = WeatherState()
                }
            }
        } catch (_: SecurityException) {
            location = null
            fetchJob?.cancel()
            _state.value = WeatherState()
        } catch (_: IllegalArgumentException) {
            location = null
            fetchJob?.cancel()
            _state.value = WeatherState()
        }
    }

    private fun acceptLocation(value: Location) {
        if (value.latitude !in -90.0..90.0 || value.longitude !in -180.0..180.0) return
        val changed = location?.distanceTo(value)?.let { it >= LOCATION_CHANGE_METERS } ?: true
        if (changed) location = value
        locationTimeout?.cancel()
        if (changed) {
            fetchJob?.cancel()
            fetchJob = null
            val saved = readCache()?.takeIf { distanceMeters(it, value) < LOCATION_CHANGE_METERS }
            _state.value = WeatherState(saved)
            scheduleFetch(force = false)
        } else if (fetchJob?.isActive != true) scheduleFetch(force = false)
    }

    private fun scheduleFetch(force: Boolean) {
        val point = location ?: return
        if (force) fetchJob?.cancel()
        else if (fetchJob?.isActive == true) return
        fetchJob = scope.launch {
            var failures = 0
            while (isActive && owners.isNotEmpty() && location === point) {
                val current = _state.value.reading?.takeIf { distanceMeters(it, point) < LOCATION_CHANGE_METERS }
                val age = current?.let { System.currentTimeMillis() - it.fetchedAtMs }
                if (!force && age != null && age in 0 until WEATHER_REFRESH_MS) {
                    delay(WEATHER_REFRESH_MS - age)
                    continue
                }
                _state.value = WeatherState(current, loading = current == null)
                lastAttemptMs = System.currentTimeMillis()
                val result = if (online()) runCatching { fetch(point) } else Result.failure(Exception("offline"))
                if (result.isSuccess) {
                    val reading = result.getOrThrow()
                    saveCache(reading)
                    _state.value = WeatherState(reading)
                    failures = 0
                    delay(WEATHER_REFRESH_MS)
                } else {
                    if (result.exceptionOrNull() is CancellationException) throw result.exceptionOrNull()!!
                    _state.value = WeatherState(current)
                    failures++
                    delay(weatherRetryDelay(failures))
                }
            }
        }
    }

    private fun online(): Boolean = runCatching {
        val network = connectivity.activeNetwork ?: return@runCatching false
        connectivity.getNetworkCapabilities(network)?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }.getOrDefault(false)

    private suspend fun fetch(point: Location): WeatherReading = withContext(Dispatchers.IO) {
        // Send only ~0.1-degree coordinates; exact device position is not retained in the cache.
        val latitude = (point.latitude * 10).roundToInt() / 10.0
        val longitude = (point.longitude * 10).roundToInt() / 10.0
        val address = "https://api.open-meteo.com/v1/forecast?latitude=" +
            String.format(Locale.US, "%.1f", latitude) + "&longitude=" +
            String.format(Locale.US, "%.1f", longitude) +
            "&current=temperature_2m,weather_code&forecast_days=1"
        val connection = URL(address).openConnection() as HttpURLConnection
        connection.connectTimeout = 8_000
        connection.readTimeout = 8_000
        try {
            if (connection.responseCode != HttpURLConnection.HTTP_OK) error("Weather HTTP ${connection.responseCode}")
            val body = connection.inputStream.bufferedReader().use { it.readText() }
            val current = JSONObject(body).getJSONObject("current")
            val temperature = current.getDouble("temperature_2m")
            val code = current.getInt("weather_code")
            require(temperature.isFinite() && temperature in -100.0..70.0 && validWeatherCode(code))
            WeatherReading(latitude, longitude, temperature.roundToInt(), code, System.currentTimeMillis())
        } finally {
            connection.disconnect()
        }
    }

    private fun distanceMeters(reading: WeatherReading, point: Location): Float {
        val result = FloatArray(1)
        Location.distanceBetween(reading.latitude, reading.longitude,
            point.latitude, point.longitude, result)
        return result[0]
    }

    private fun readCache(): WeatherReading? = runCatching {
        if (!prefs.contains("fetched_at")) return@runCatching null
        WeatherReading(
            java.lang.Double.longBitsToDouble(prefs.getLong("lat", 0)),
            java.lang.Double.longBitsToDouble(prefs.getLong("lon", 0)),
            prefs.getInt("temperature", 0), prefs.getInt("code", -1),
            prefs.getLong("fetched_at", 0)
        ).takeIf { validWeatherCode(it.code) && it.temperatureC in -100..70 &&
            WeatherState(it).displayReading(System.currentTimeMillis()) != null }
    }.getOrNull()

    private fun saveCache(reading: WeatherReading) {
        prefs.edit().putLong("lat", java.lang.Double.doubleToRawLongBits(reading.latitude))
            .putLong("lon", java.lang.Double.doubleToRawLongBits(reading.longitude))
            .putInt("temperature", reading.temperatureC).putInt("code", reading.code)
            .putLong("fetched_at", reading.fetchedAtMs).apply()
    }
}
