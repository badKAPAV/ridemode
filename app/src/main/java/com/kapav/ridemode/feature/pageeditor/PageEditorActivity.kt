package com.kapav.ridemode.feature.pageeditor

import android.os.Bundle
import android.media.AudioManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.kapav.ridemode.feature.widgets.LocalSpeedUnit
import com.kapav.ridemode.core.ui.theme.RideTheme
import com.kapav.ridemode.feature.ride.RideViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PageEditorActivity : ComponentActivity() {
    private val viewModel: RideViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        volumeControlStream = AudioManager.STREAM_MUSIC
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            RideTheme(forceDark = true) {
                val preferences by viewModel.preferences.collectAsState()
                CompositionLocalProvider(LocalSpeedUnit provides preferences.speedUnit) {
                    LandscapePageEditorScreen(viewModel = viewModel, onBack = { finish() })
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshMusic()
        viewModel.refreshNotifications()
        viewModel.refreshVolume()
        viewModel.startWeather("editor")
        viewModel.startCalls("editor")
        WindowCompat.getInsetsController(window, window.decorView).hide(WindowInsetsCompat.Type.systemBars())
    }

    override fun onPause() {
        viewModel.stopWeather("editor")
        viewModel.stopCalls("editor")
        super.onPause()
    }
}
