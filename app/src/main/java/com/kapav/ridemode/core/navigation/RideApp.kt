package com.kapav.ridemode.core.navigation

import android.content.Intent
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kapav.ridemode.R
import com.kapav.ridemode.feature.home.HomeScreen
import com.kapav.ridemode.feature.pageeditor.PageEditorActivity
import com.kapav.ridemode.feature.onboarding.OnboardingScreen
import com.kapav.ridemode.feature.permissions.PermissionInfoScreen
import com.kapav.ridemode.feature.ride.RideActivity
import com.kapav.ridemode.feature.ride.RideViewModel
import com.kapav.ridemode.feature.settings.SettingsScreen

private object Destinations {
    const val HOME = "home"
    const val SETTINGS = "settings"
    const val ONBOARDING = "onboarding"
    const val PERMISSIONS = "permissions"
}

@Composable
fun RideApp(viewModel: RideViewModel) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val startRide = { context.startActivity(Intent(context, RideActivity::class.java)) }
    val notificationPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) Toast.makeText(context, R.string.ride_notification_denied, Toast.LENGTH_LONG).show()
        startRide()
    }
    NavHost(navController = navController, startDestination = Destinations.HOME) {
        composable(Destinations.HOME) {
            HomeScreen(
                onEditPages = { context.startActivity(Intent(context, PageEditorActivity::class.java)) },
                onStartRide = {
                    if (Build.VERSION.SDK_INT >= 33 &&
                        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
                    ) notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                    else startRide()
                },
                onSettings = { navController.navigate(Destinations.SETTINGS) },
                onOnboarding = { navController.navigate(Destinations.ONBOARDING) },
                onPermissions = { navController.navigate(Destinations.PERMISSIONS) }
            )
        }
        composable(Destinations.SETTINGS) {
            SettingsScreen(
                viewModel = viewModel,
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
