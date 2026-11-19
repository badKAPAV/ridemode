package com.kapav.ridemode.feature.ride

import android.app.PictureInPictureParams
import android.Manifest
import android.content.res.Configuration
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.util.Rational
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.kapav.ridemode.core.ui.theme.RideTheme
import com.kapav.ridemode.data.RidePreferences
import com.kapav.ridemode.feature.widgets.GoogleMapsController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RideActivity : ComponentActivity() {
    private val viewModel: RideViewModel by viewModels()
    private val inPip = mutableStateOf(false)
    private val introInterrupted = mutableStateOf(false)
    private val resumeCounter = mutableIntStateOf(0)
    private var keepAwake = true
    private val mapControllers = mutableMapOf<String, GoogleMapsController>()
    private val mapLocationPermission = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) {
        mapControllers.values.forEach { controller -> controller.refreshPermission() }
    }

    private fun mapControllerFor(id: String): GoogleMapsController =
        mapControllers.getOrPut(id) { GoogleMapsController(this) }

    private fun finishMapSessions() {
        mapControllers.values.forEach(GoogleMapsController::finishRide)
        mapControllers.clear()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        volumeControlStream = AudioManager.STREAM_MUSIC
        val freshStart = savedInstanceState == null &&
            !RideSession.active.value && !intent.hasExtra(RideSession.EXTRA_STARTED_AT)
        RideSession.start(this, intent.getLongExtra(RideSession.EXTRA_STARTED_AT, System.currentTimeMillis()))
        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (Build.VERSION.SDK_INT >= 26) {
            val params = PictureInPictureParams.Builder().setAspectRatio(Rational(16, 9))
            if (Build.VERSION.SDK_INT >= 31) params.setSeamlessResizeEnabled(false)
            setPictureInPictureParams(params.build())
        }
        setContent {
            RideTheme(forceDark = true) {
                val pages by viewModel.pages.collectAsState()
                val preferences by viewModel.preferences.collectAsState()
                val music by viewModel.music.collectAsState()
                val volume by viewModel.volume.collectAsState()
                val weather by viewModel.weather.collectAsState()
                val notifications by viewModel.notifications.collectAsState()
                val calls by viewModel.calls.collectAsState()
                val guidance by viewModel.guidance.collectAsState()
                val maxRows by viewModel.maxRows.collectAsState()
                val maxColumns by viewModel.maxColumns.collectAsState()
                val active = RideSession.active.value
                val startupPreferences by produceState<RidePreferences?>(initialValue = null) {
                    value = viewModel.readPreferences()
                }
                var introFinished by rememberSaveable { mutableStateOf(!freshStart) }
                LaunchedEffect(active) { if (!active) finish() }
                LaunchedEffect(inPip.value) {
                    if (inPip.value) introFinished = true
                }
                SideEffect {
                    keepAwake = preferences.keepAwake
                    if (keepAwake && !inPip.value) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    else window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
                Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
                    val startup = startupPreferences
                    if (startup != null) {
                        RideDashboard(
                            pages = pages,
                            preferences = preferences,
                            music = music,
                            onMusicAction = viewModel::controlMusic,
                            volume = volume,
                            onVolumeChange = { viewModel.setVolume(it) },
                            weather = weather,
                            notifications = notifications,
                            calls = calls,
                            guidance = guidance,
                            maxRows = maxRows,
                            maxColumns = maxColumns,
                            inPip = inPip.value,
                            resumeCounter = resumeCounter.intValue,
                            mapControllerFor = ::mapControllerFor,
                            onRequestMapLocation = { mapLocationPermission.launch(arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION)) },
                            onSaverChange = { enabled ->
                                window.attributes = window.attributes.apply {
                                    screenBrightness = if (enabled && preferences.reduceBrightness) 0.05f
                                        else WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
                                }
                            },
                            onEndRide = { finishMapSessions(); RideSession.end(this@RideActivity); finish() }
                        )
                        if (freshStart && !introFinished && !introInterrupted.value && !inPip.value &&
                            startup.rideStartAnimation && !startup.reducedMotion) {
                            RideStartupAnimation { introFinished = true }
                        }
                    }
                }
            }
        }
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (Build.VERSION.SDK_INT >= 26 && RideSession.active.value && !isInPictureInPictureMode) {
            enterPictureInPictureMode(PictureInPictureParams.Builder().setAspectRatio(Rational(16, 9)).build())
        }
    }

    override fun onPictureInPictureRequested(): Boolean {
        if (Build.VERSION.SDK_INT >= 26 && RideSession.active.value) {
            return enterPictureInPictureMode(PictureInPictureParams.Builder().setAspectRatio(Rational(16, 9)).build())
        }
        return false
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        inPip.value = isInPictureInPictureMode
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshMusic()
        viewModel.refreshNotifications()
        viewModel.refreshGuidance()
        viewModel.refreshVolume()
        mapControllers.values.forEach(GoogleMapsController::refreshPermission)
        viewModel.startWeather("ride")
        viewModel.startCalls("ride")
        resumeCounter.intValue++
        if (keepAwake && !inPip.value) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        WindowCompat.getInsetsController(window, window.decorView).hide(WindowInsetsCompat.Type.systemBars())
    }

    override fun onPause() {
        mapControllers.values.forEach(GoogleMapsController::pauseIdleLocationLookup)
        viewModel.stopWeather("ride")
        viewModel.stopCalls("ride")
        introInterrupted.value = true
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.attributes = window.attributes.apply {
            screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
        }
        super.onPause()
    }

    override fun onDestroy() {
        finishMapSessions()
        super.onDestroy()
    }
}
