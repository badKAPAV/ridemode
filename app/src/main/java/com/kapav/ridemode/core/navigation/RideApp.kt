package com.kapav.ridemode.core.navigation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kapav.ridemode.feature.home.HomeScreen
import com.kapav.ridemode.feature.onboarding.OnboardingScreen
import com.kapav.ridemode.feature.permissions.PermissionInfoScreen
import com.kapav.ridemode.feature.ride.RideActivity
import com.kapav.ridemode.feature.settings.SettingsScreen

private object Destinations {
    const val HOME = "home"
    const val SETTINGS = "settings"
    const val ONBOARDING = "onboarding"
    const val PERMISSIONS = "permissions"
}

@Composable
fun RideApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    NavHost(navController = navController, startDestination = Destinations.HOME) {
        composable(Destinations.HOME) {
            HomeScreen(
                onStartRide = { context.startActivity(Intent(context, RideActivity::class.java)) },
                onSettings = { navController.navigate(Destinations.SETTINGS) },
                onOnboarding = { navController.navigate(Destinations.ONBOARDING) },
                onPermissions = { navController.navigate(Destinations.PERMISSIONS) }
            )
        }
        composable(Destinations.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onPermissions = { navController.navigate(Destinations.PERMISSIONS) }
            )
        }
        composable(Destinations.ONBOARDING) {
            OnboardingScreen(onBack = { navController.popBackStack() })
        }
        composable(Destinations.PERMISSIONS) {
            PermissionInfoScreen(onBack = { navController.popBackStack() })
        }
    }
}
