package com.kapav.ridemode.data

import android.content.Context
import android.database.ContentObserver
import android.media.AudioDeviceCallback
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.roundToInt

data class VolumeState(val minimum: Int = 0, val maximum: Int = 0, val current: Int = 0) {
    val available: Boolean get() = maximum > minimum
    val fraction: Float get() = if (available) (current - minimum).toFloat() / (maximum - minimum) else 0f
    val percent: Int get() = (fraction * 100).roundToInt()
    fun levelForFraction(value: Float): Int =
        (minimum + value.coerceIn(0f, 1f) * (maximum - minimum)).roundToInt().coerceIn(minimum, maximum)
}

/** App-process music-stream volume source. No notification access or background service is needed. */
@Singleton
class VolumeRepository @Inject constructor(@ApplicationContext context: Context) {
    private val audio = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    private val handler = Handler(Looper.getMainLooper())
    private val _state = MutableStateFlow(read())
    val state = _state.asStateFlow()

    private val settingsObserver = object : ContentObserver(handler) {
        override fun onChange(selfChange: Boolean) = refresh()
    }
    private val deviceCallback = object : AudioDeviceCallback() {
        override fun onAudioDevicesAdded(addedDevices: Array<out AudioDeviceInfo>) = refresh()
        override fun onAudioDevicesRemoved(removedDevices: Array<out AudioDeviceInfo>) = refresh()
    }

    init {
        context.contentResolver.registerContentObserver(Settings.System.CONTENT_URI, true, settingsObserver)
        audio?.registerAudioDeviceCallback(deviceCallback, handler)
    }

    fun refresh() {
        _state.value = read()
    }

    fun setFraction(fraction: Float): Boolean {
        val manager = audio ?: return false
        val current = read()
        if (!current.available) return false
        return try {
            manager.setStreamVolume(AudioManager.STREAM_MUSIC, current.levelForFraction(fraction), 0)
            refresh()
            true
        } catch (_: SecurityException) {
            refresh()
            false
        } catch (_: IllegalArgumentException) {
            refresh()
            false
        }
    }

    private fun read(): VolumeState {
        val manager = audio ?: return VolumeState()
        return try {
            val minimum = if (Build.VERSION.SDK_INT >= 28) manager.getStreamMinVolume(AudioManager.STREAM_MUSIC) else 0
            val maximum = manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            VolumeState(minimum, maximum, manager.getStreamVolume(AudioManager.STREAM_MUSIC).coerceIn(minimum, maximum))
        } catch (_: RuntimeException) {
            VolumeState()
        }
    }
}
