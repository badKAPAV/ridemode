package com.kapav.ridemode.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Bold
import com.adamglin.phosphoricons.bold.Bell
import com.adamglin.phosphoricons.bold.Gear
import com.adamglin.phosphoricons.bold.ShieldCheck
import com.kapav.ridemode.R
import com.kapav.ridemode.core.ui.components.RideInfoCard
import com.kapav.ridemode.core.ui.components.RideScreen

@Composable
fun SettingsScreen(onBack: () -> Unit, onPermissions: () -> Unit) {
    RideScreen(title = stringResource(R.string.settings_title), onBack = onBack) {
        RideInfoCard(
            title = stringResource(R.string.settings_display_title),
            body = stringResource(R.string.settings_display_body),
            icon = PhosphorIcons.Bold.Gear
        )
        RideInfoCard(
            title = stringResource(R.string.settings_notifications_title),
            body = stringResource(R.string.settings_notifications_body),
            icon = PhosphorIcons.Bold.Bell
        )
        RideInfoCard(
            title = stringResource(R.string.permissions_title),
            body = stringResource(R.string.settings_permissions_body),
            icon = PhosphorIcons.Bold.ShieldCheck,
            actionLabel = stringResource(R.string.open_permissions),
            onAction = onPermissions
        )
    }
}
