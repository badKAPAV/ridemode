package com.kapav.ridemode.data

import android.app.PendingIntent
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.provider.Settings
import android.view.KeyEvent
import com.kapav.ridemode.service.RideMediaListenerService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class MusicAvailability { ACCESS_REQUIRED, NO_SESSION, READY, ERROR }
enum class MusicAction { PREVIOUS, PLAY_PAUSE, NEXT }

data class MusicSessionState(
    val availability: MusicAvailability = MusicAvailability.ACCESS_REQUIRED,
    val packageName: String? = null,
    val title: String? = null,
    val artist: String? = null,
    val artwork: Bitmap? = null,
    val artworkUri: String? = null,
    val durationMs: Long? = null,
    val positionMs: Long = 0,
    val positionUpdatedAtMs: Long = 0,
    val speed: Float = 0f,
    val playing: Boolean = false,
    val actions: Long = 0,
    val sessionActivity: PendingIntent? = null
) {
    /** The source is captured when the confirmation opens, independently of later session changes. */
    fun sourcePackageForLaunch(): String? = packageName?.takeIf {
        availability == MusicAvailability.READY && it.isNotBlank()
    }

    fun supports(action: MusicAction): Boolean = availability == MusicAvailability.READY && when (action) {
        MusicAction.PREVIOUS -> actions and PlaybackState.ACTION_SKIP_TO_PREVIOUS != 0L
        MusicAction.NEXT -> actions and PlaybackState.ACTION_SKIP_TO_NEXT != 0L
        MusicAction.PLAY_PAUSE -> actions and PlaybackState.ACTION_PLAY_PAUSE != 0L ||
            actions and (if (playing) PlaybackState.ACTION_PAUSE else PlaybackState.ACTION_PLAY) != 0L
    }

    fun positionAt(elapsedRealtimeMs: Long): Long {
        val estimate = if (playing && positionUpdatedAtMs > 0)
            positionMs + ((elapsedRealtimeMs - positionUpdatedAtMs).coerceAtLeast(0) * speed).toLong()
        else positionMs
        return estimate.coerceAtLeast(0).let { position ->
            durationMs?.let { position.coerceAtMost(it) } ?: position
        }
    }
}

/** Keeps only the current active media session in memory; notification contents are never read. */
@Singleton
class MediaSessionRepository @Inject constructor(@ApplicationContext private val context: Context) {
    private val manager = context.getSystemService(Context.MEDIA_SESSION_SERVICE) as? MediaSessionManager
    private val handler = Handler(Looper.getMainLooper())
    private val component = ComponentName(context, RideMediaListenerService::class.java)
    private val _state = MutableStateFlow(MusicSessionState())
    val state = _state.asStateFlow()
    private var controller: MediaController? = null
    private var listening = false

    private val sessionListener = MediaSessionManager.OnActiveSessionsChangedListener { sessions ->
        select(sessions.orEmpty())
    }
    private val controllerCallback = object : MediaController.Callback() {
        override fun onMetadataChanged(metadata: MediaMetadata?) = publish()
        override fun onPlaybackStateChanged(state: PlaybackState?) = publish()
        override fun onSessionDestroyed() {
            clearController()
            _state.value = MusicSessionState(MusicAvailability.NO_SESSION)
            handler.postDelayed({ refresh() }, SESSION_RECHECK_DELAY_MS)
        }
    }

    fun hasAccess(): Boolean {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            return notificationManager.isNotificationListenerAccessGranted(component)
        }
        val enabled = Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")
        return enabled?.split(':')?.any { ComponentName.unflattenFromString(it) == component } == true
    }

    fun refresh() {
        if (!hasAccess()) {
            clear(MusicAvailability.ACCESS_REQUIRED)
            return
        }
        val sessionManager = manager ?: run {
            clear(MusicAvailability.ERROR)
            return
        }
        try {
            if (!listening) {
                sessionManager.addOnActiveSessionsChangedListener(sessionListener, component, handler)
                listening = true
            }
            select(sessionManager.getActiveSessions(component))
        } catch (_: SecurityException) {
            clear(MusicAvailability.ACCESS_REQUIRED)
        } catch (_: RuntimeException) {
            clear(MusicAvailability.ERROR)
        }
    }

    fun disconnect() = clear(if (hasAccess()) MusicAvailability.ERROR else MusicAvailability.ACCESS_REQUIRED)

    private fun select(sessions: List<MediaController>) {
        if (!hasAccess()) {
            clear(MusicAvailability.ACCESS_REQUIRED)
            return
        }
        val chosen = sessions.firstOrNull { it.playbackState?.state == PlaybackState.STATE_PLAYING }
            ?: sessions.firstOrNull { it.metadata != null }
            ?: sessions.firstOrNull()
        if (chosen == null) {
            clearController()
            _state.value = MusicSessionState(MusicAvailability.NO_SESSION)
            return
        }
        if (controller?.sessionToken != chosen.sessionToken) {
            clearController()
            controller = chosen
            chosen.registerCallback(controllerCallback, handler)
        }
        publish()
    }

    private fun publish() {
        val selected = controller ?: return
        val metadata = selected.metadata
        val playback = selected.playbackState
        val duration = metadata?.getLong(MediaMetadata.METADATA_KEY_DURATION)?.takeIf { it > 0 }
        _state.value = MusicSessionState(
            availability = MusicAvailability.READY,
            packageName = selected.packageName,
            title = metadata?.getString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE)
                ?: metadata?.getString(MediaMetadata.METADATA_KEY_TITLE),
            artist = metadata?.getString(MediaMetadata.METADATA_KEY_ARTIST)
                ?: metadata?.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST)
                ?: metadata?.getString(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE),
            artwork = metadata?.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART)
                ?: metadata?.getBitmap(MediaMetadata.METADATA_KEY_ART)
                ?: metadata?.getBitmap(MediaMetadata.METADATA_KEY_DISPLAY_ICON),
            artworkUri = metadata?.getString(MediaMetadata.METADATA_KEY_ALBUM_ART_URI)
                ?: metadata?.getString(MediaMetadata.METADATA_KEY_ART_URI)
                ?: metadata?.getString(MediaMetadata.METADATA_KEY_DISPLAY_ICON_URI),
            durationMs = duration,
            positionMs = playback?.position?.coerceAtLeast(0) ?: 0,
            positionUpdatedAtMs = playback?.lastPositionUpdateTime ?: SystemClock.elapsedRealtime(),
            speed = playback?.playbackSpeed ?: 0f,
            playing = playback?.state == PlaybackState.STATE_PLAYING,
            sessionActivity = selected.sessionActivity,
            actions = playback?.actions ?: 0
        )
    }

    fun perform(action: MusicAction) {
        val selected = controller ?: return
        val current = _state.value
        if (current.availability != MusicAvailability.READY || !current.supports(action)) return
        try {
            when (action) {
                MusicAction.PREVIOUS -> selected.transportControls.skipToPrevious()
                MusicAction.NEXT -> selected.transportControls.skipToNext()
                MusicAction.PLAY_PAUSE -> {
                    val direct = if (current.playing) PlaybackState.ACTION_PAUSE else PlaybackState.ACTION_PLAY
                    if (current.actions and direct != 0L) {
                        if (current.playing) selected.transportControls.pause()
                        else selected.transportControls.play()
                    } else {
                        selected.dispatchMediaButtonEvent(KeyEvent(KeyEvent.ACTION_DOWN,
                            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE))
                        selected.dispatchMediaButtonEvent(KeyEvent(KeyEvent.ACTION_UP,
                            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE))
                    }
                }
            }
        } catch (_: RuntimeException) {
            refresh()
        }
    }

    private fun clearController() {
        controller?.unregisterCallback(controllerCallback)
        controller = null
    }

    private fun clear(availability: MusicAvailability) {
        clearController()
        if (listening) {
            manager?.removeOnActiveSessionsChangedListener(sessionListener)
            listening = false
        }
        _state.value = MusicSessionState(availability)
    }

    private companion object { const val SESSION_RECHECK_DELAY_MS = 250L }
}
