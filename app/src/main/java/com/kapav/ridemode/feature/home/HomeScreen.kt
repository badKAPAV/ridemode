package com.kapav.ridemode.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Bold
import com.adamglin.phosphoricons.bold.Gear
import com.adamglin.phosphoricons.bold.Info
import com.adamglin.phosphoricons.bold.Motorcycle
import com.adamglin.phosphoricons.bold.ShieldCheck
import com.kapav.ridemode.R
import com.kapav.ridemode.core.ui.components.RideInfoCard
import com.kapav.ridemode.core.ui.components.RideScreen

@Composable
fun HomeScreen(
    onStartRide: () -> Unit,
    onSettings: () -> Unit,
    onOnboarding: () -> Unit,
    onPermissions: () -> Unit
) {
    RideScreen(title = stringResource(R.string.app_name)) {
        RideInfoCard(
            title = stringResource(R.string.home_hero_title),
            body = stringResource(R.string.home_hero_body),
            icon = PhosphorIcons.Bold.Motorcycle,
            actionLabel = stringResource(R.string.start_ride),
            onAction = onStartRide
        )
        RideInfoCard(
            title = stringResource(R.string.settings_title),
            body = stringResource(R.string.home_settings_body),
            icon = PhosphorIcons.Bold.Gear,
            actionLabel = stringResource(R.string.open_settings),
            onAction = onSettings
        )
        RideInfoCard(
            title = stringResource(R.string.onboarding_title),
            body = stringResource(R.string.home_onboarding_body),
            icon = PhosphorIcons.Bold.Info,
            actionLabel = stringResource(R.string.open_guide),
            onAction = onOnboarding
        )
        RideInfoCard(
            title = stringResource(R.string.permissions_title),
            body = stringResource(R.string.home_permissions_body),
            icon = PhosphorIcons.Bold.ShieldCheck,
            actionLabel = stringResource(R.string.open_permissions),
            onAction = onPermissions
        )
    }
}
