package com.kapav.ridemode.feature.permissions

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Fill
import com.adamglin.phosphoricons.fill.AddressBook
import com.adamglin.phosphoricons.fill.Bell
import com.adamglin.phosphoricons.fill.Microphone
import com.adamglin.phosphoricons.fill.MusicNotes
import com.adamglin.phosphoricons.fill.CloudLightning
import com.adamglin.phosphoricons.fill.Phone
import com.adamglin.phosphoricons.fill.ShieldCheck
import com.kapav.ridemode.R
import com.kapav.ridemode.core.ui.components.RideInfoCard
import com.kapav.ridemode.core.ui.components.RideCardTone
import com.kapav.ridemode.core.ui.components.RideScreen

@Composable
fun PermissionInfoScreen(onBack: () -> Unit) {
    RideScreen(title = stringResource(R.string.permissions_title), onBack = onBack,
        subtitle = "You choose what the app can access.") {
        RideInfoCard(
            title = stringResource(R.string.permissions_now_title),
            body = stringResource(R.string.permissions_now_body),
            icon = PhosphorIcons.Fill.ShieldCheck,
            tone = RideCardTone.PRIMARY
        )
        RideInfoCard(
            title = "Music sessions",
            body = "Live music and the notification feed need an explicit Android notification-access grant from Settings. The listener reads active media sessions plus messages and high-priority notifications; content stays in memory while active. Revoke access in Android Settings at any time.",
            icon = PhosphorIcons.Fill.MusicNotes,
            tone = RideCardTone.SECONDARY
        )
        RideInfoCard(
            title = "Weather location",
            body = "Live weather uses approximate location only while Ride or the editor is visible. Coarse coordinates go to Open-Meteo over HTTPS; the app caches one rounded location and result. Grant location from Settings → Weather location. Without usable data, the Clock shows Something's not right.",
            icon = PhosphorIcons.Fill.CloudLightning,
            tone = RideCardTone.TERTIARY
        )
        RideInfoCard(
            title = "System call history",
            body = "The last five system calls require Android's restricted call-log grant. If the installer or Android cannot grant it, Calls shows an unavailable state. Ridemode does not keep its own call record. Direct calling asks for a separate permission and confirmation; emergency calls open the system dialer.",
            icon = PhosphorIcons.Fill.Phone,
            tone = RideCardTone.PRIMARY
        )
        RideInfoCard(
            title = stringResource(R.string.permissions_notifications_title),
            body = stringResource(R.string.permissions_notifications_body),
            icon = PhosphorIcons.Fill.Bell,
            tone = RideCardTone.TERTIARY
        )
        RideInfoCard(
            title = stringResource(R.string.permissions_contacts_title),
            body = stringResource(R.string.permissions_contacts_body),
            icon = PhosphorIcons.Fill.AddressBook,
            tone = RideCardTone.SECONDARY
        )
        RideInfoCard(
            title = stringResource(R.string.permissions_microphone_title),
            body = stringResource(R.string.permissions_microphone_body),
            icon = PhosphorIcons.Fill.Microphone,
            tone = RideCardTone.PRIMARY
        )
    }
}
