package com.kapav.ridemode

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.SideEffect
import androidx.core.view.WindowCompat
import com.kapav.ridemode.core.navigation.RideApp
import com.kapav.ridemode.core.ui.theme.RideTheme
import com.kapav.ridemode.feature.ride.RideViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val rideViewModel: RideViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val dark = isSystemInDarkTheme()
            SideEffect {
                WindowCompat.getInsetsController(window, window.decorView).apply {
                    isAppearanceLightStatusBars = !dark
                    isAppearanceLightNavigationBars = !dark
                }
            }
            RideTheme { RideApp(rideViewModel) }
        }
    }

    override fun onResume() {
        super.onResume()
        rideViewModel.refreshMusic()
    }
}
