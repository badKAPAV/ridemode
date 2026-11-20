package com.kapav.ridemode.feature.permissions

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ComponentName
import com.kapav.ridemode.service.RideMediaListenerService
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Fill
import com.adamglin.phosphoricons.fill.Bell
import com.adamglin.phosphoricons.fill.MapPin
import com.adamglin.phosphoricons.fill.MusicNotes
import com.adamglin.phosphoricons.fill.Phone
import com.adamglin.phosphoricons.fill.ShieldCheck
import com.kapav.ridemode.R
import com.kapav.ridemode.core.ui.components.RideInfoCard
import com.kapav.ridemode.core.ui.components.RideCardTone
import com.kapav.ridemode.core.ui.components.RideScreen

@Composable
fun PermissionInfoScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val appSettings = { openAccessSettings(context) }
    RideScreen(title = stringResource(R.string.permissions_title), onBack = onBack,
        subtitle = "Choose what Ridemode can access. Tap a card to manage it.") {
        RideInfoCard("Location", "Precise location for navigation and speed; approximate location for weather.",
            PhosphorIcons.Fill.MapPin, actionLabel = "App settings → Permissions", onAction = appSettings)
        RideInfoCard("Music and messages", "Allow notification access to control music and show messages.",
            PhosphorIcons.Fill.MusicNotes, tone = RideCardTone.SECONDARY,
            actionLabel = "Manage notification access", onAction = {
                val listeners = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                if (Build.VERSION.SDK_INT >= 30) openAccessSettings(context,
                    Intent(Settings.ACTION_NOTIFICATION_LISTENER_DETAIL_SETTINGS).putExtra(
                        Settings.EXTRA_NOTIFICATION_LISTENER_COMPONENT_NAME,
                        ComponentName(context, RideMediaListenerService::class.java).flattenToString()), listeners)
                else openAccessSettings(context, listeners)
            })
        RideInfoCard("Calls", "Allow call history to see recent calls. Calling needs separate permission.",
            PhosphorIcons.Fill.Phone, tone = RideCardTone.TERTIARY,
            actionLabel = "App settings → Permissions", onAction = appSettings)
        RideInfoCard("Ride notifications", "Keep ride controls available when you leave the app.",
            PhosphorIcons.Fill.Bell, actionLabel = "Manage notifications", onAction = {
                val intent = if (Build.VERSION.SDK_INT >= 26) Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName) else null
                if (intent != null) openAccessSettings(context, intent) else openAccessSettings(context)
            })
        RideInfoCard("Your controls", "You can change or revoke access at any time.",
            PhosphorIcons.Fill.ShieldCheck, tone = RideCardTone.SECONDARY,
            actionLabel = "Open app settings", onAction = appSettings)
        Text("Messages stay on your device. Maps and weather use online services. Privacy mode in Settings hides message text.",
            style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

private fun openAccessSettings(context: Context, vararg preferred: Intent) {
    val appDetails = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.parse("package:${context.packageName}"))
    for (intent in preferred.toList() + appDetails) {
        try { context.startActivity(intent); return }
        catch (_: ActivityNotFoundException) { /* Try the next supported Settings screen. */ }
        catch (_: SecurityException) { /* Some system builds restrict individual Settings actions. */ }
    }
    Toast.makeText(context, "Open Ridemode permissions in Android Settings", Toast.LENGTH_SHORT).show()
}
