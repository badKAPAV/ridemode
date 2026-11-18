package com.kapav.ridemode.feature.permissions

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Bold
import com.adamglin.phosphoricons.bold.AddressBook
import com.adamglin.phosphoricons.bold.Bell
import com.adamglin.phosphoricons.bold.Microphone
import com.adamglin.phosphoricons.bold.ShieldCheck
import com.kapav.ridemode.R
import com.kapav.ridemode.core.ui.components.RideInfoCard
import com.kapav.ridemode.core.ui.components.RideScreen

@Composable
fun PermissionInfoScreen(onBack: () -> Unit) {
    RideScreen(title = stringResource(R.string.permissions_title), onBack = onBack) {
        RideInfoCard(
            title = stringResource(R.string.permissions_now_title),
            body = stringResource(R.string.permissions_now_body),
            icon = PhosphorIcons.Bold.ShieldCheck
        )
        RideInfoCard(
            title = stringResource(R.string.permissions_notifications_title),
            body = stringResource(R.string.permissions_notifications_body),
            icon = PhosphorIcons.Bold.Bell
        )
        RideInfoCard(
            title = stringResource(R.string.permissions_contacts_title),
            body = stringResource(R.string.permissions_contacts_body),
            icon = PhosphorIcons.Bold.AddressBook
        )
        RideInfoCard(
            title = stringResource(R.string.permissions_microphone_title),
            body = stringResource(R.string.permissions_microphone_body),
            icon = PhosphorIcons.Bold.Microphone
        )
    }
}
