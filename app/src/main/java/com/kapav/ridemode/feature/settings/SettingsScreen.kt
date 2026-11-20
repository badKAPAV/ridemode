package com.kapav.ridemode.feature.settings

import android.content.Intent
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Fill
import com.adamglin.phosphoricons.fill.Bell
import com.adamglin.phosphoricons.fill.Clock
import com.adamglin.phosphoricons.fill.MoonStars
import com.adamglin.phosphoricons.fill.Motorcycle
import com.adamglin.phosphoricons.fill.MusicNotes
import com.adamglin.phosphoricons.fill.ShieldCheck
import com.adamglin.phosphoricons.fill.SlidersHorizontal
import com.adamglin.phosphoricons.fill.Sun
import com.adamglin.phosphoricons.fill.SunDim
import com.adamglin.phosphoricons.fill.CloudLightning
import com.adamglin.phosphoricons.fill.MapPin
import com.adamglin.phosphoricons.fill.Phone
import com.kapav.ridemode.R
import com.kapav.ridemode.core.ui.components.RideInfoCard
import com.kapav.ridemode.core.ui.components.RideScreen
import com.kapav.ridemode.core.ui.theme.RideUiTokens
import com.kapav.ridemode.core.model.SpeedUnit
import com.kapav.ridemode.data.RidePreferences
import com.kapav.ridemode.data.OledSaverStyle
import com.kapav.ridemode.data.MusicAvailability
import com.kapav.ridemode.data.NotificationAccess
import com.kapav.ridemode.feature.ride.RideViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsScreen(viewModel: RideViewModel, onBack: () -> Unit, onPermissions: () -> Unit) {
    val preferences by viewModel.preferences.collectAsState(initial = RidePreferences())
    val music by viewModel.music.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var weatherAllowed by remember { mutableStateOf(
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED) }
    var weatherRequested by remember { mutableStateOf(false) }
    var callLogAllowed by remember { mutableStateOf(
        ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) ==
            PackageManager.PERMISSION_GRANTED) }
    var callLogRequested by remember { mutableStateOf(false) }
    val weatherPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        weatherRequested = true
        weatherAllowed = it
        viewModel.refreshWeather()
    }
    val callLogPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        callLogRequested = true
        callLogAllowed = it
        viewModel.refreshCalls()
    }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                weatherAllowed = ContextCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                callLogAllowed = ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    val scope = rememberCoroutineScope()
    RideScreen(title = stringResource(R.string.settings_title), onBack = onBack) {
        SettingsSection("Ride display", "Set preferences for your ride dashboard") {
            SettingToggle("Keep screen awake", "Screen stays on while Ridemode is active",
                PhosphorIcons.Fill.Sun, preferences.keepAwake) {
                scope.launch { viewModel.setKeepAwake(it) }
            }
            SettingToggle(stringResource(R.string.ride_start_animation_title),
                stringResource(R.string.ride_start_animation_body),
                PhosphorIcons.Fill.Motorcycle, preferences.rideStartAnimation) {
                scope.launch { viewModel.setRideStartAnimation(it) }
            }
            SettingToggle("Show Maps shortcut", "Show Open Maps in the Ride toolbar",
                PhosphorIcons.Fill.MapPin, preferences.showMapsShortcut) {
                scope.launch { viewModel.setShowMapsShortcut(it) }
            }
            SettingToggle("Idle Mode", "A battery saving always on display for your dashboard",
                PhosphorIcons.Fill.MoonStars, preferences.saverEnabled) {
                scope.launch { viewModel.setSaverEnabled(it) }
            }
            if (preferences.saverEnabled) {
                Text("Idle style", style = MaterialTheme.typography.titleSmall)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
                    verticalArrangement = Arrangement.spacedBy(RideUiTokens.widgetPadding)) {
                    OledSaverStyle.entries.forEachIndexed { index, style ->
                        ToggleButton(checked = preferences.saverStyle == style,
                            onCheckedChange = { scope.launch { viewModel.setSaverStyle(style) } },
                            modifier = Modifier.semantics { role = Role.RadioButton },
                            shapes = if (index == 0) ButtonGroupDefaults.connectedLeadingButtonShapes()
                                else ButtonGroupDefaults.connectedTrailingButtonShapes()) {
                            Text(if (style == OledSaverStyle.CLASSIC) "Classic" else "Modern")
                        }
                    }
                }
                Text("Idle show delay", style = MaterialTheme.typography.titleSmall)
                val delays = listOf(15, 30, 60, 120, 300)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
                    verticalArrangement = Arrangement.spacedBy(RideUiTokens.widgetPadding)) {
                    delays.forEachIndexed { index, seconds ->
                        ToggleButton(checked = preferences.saverDelaySeconds == seconds,
                            onCheckedChange = { scope.launch { viewModel.setSaverDelay(seconds) } },
                            modifier = Modifier.semantics { role = Role.RadioButton },
                            shapes = when (index) {
                                0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                delays.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                                else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                            }) { Text("$seconds s") }
                    }
                }
                SettingToggle("Outline clock", "Use outlined numerals in idle mode clock",
                    PhosphorIcons.Fill.Clock, preferences.outlineText) {
                    scope.launch { viewModel.setOutlineText(it) }
                }
                SettingToggle("Dim screen", "Make the already dim OLED screen significantly darker",
                    PhosphorIcons.Fill.SunDim, preferences.reduceBrightness) {
                    scope.launch { viewModel.setReduceBrightness(it) }
                }
            }
        }
        SettingsSection("Motion", "Control transitions across Ride and the editor.") {
            SettingToggle("Reduce motion", "Skips Ride intro and shortens page and editor transitions.",
                PhosphorIcons.Fill.SlidersHorizontal, preferences.reducedMotion) {
                scope.launch { viewModel.setReducedMotion(it) }
            }
        }
        SettingsSection("Speedometer", "Precise GPS while Ride is visible; each tile can override these units") {
            Row(horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)) {
                SpeedUnit.entries.forEachIndexed { index, unit ->
                    ToggleButton(checked = preferences.speedUnit == unit,
                        onCheckedChange = { scope.launch { viewModel.setSpeedUnit(unit) } },
                        shapes = if (index == 0) ButtonGroupDefaults.connectedLeadingButtonShapes()
                            else ButtonGroupDefaults.connectedTrailingButtonShapes()) { Text(unit.label) }
                }
            }
            Text("Add a Speedometer tile, then tap it to allow precise location. GPS stops when Ride leaves the foreground.",
                style = MaterialTheme.typography.bodySmall)
        }
        Text("Access and privacy", style = MaterialTheme.typography.titleLarge)
        RideInfoCard("Weather location",
            if (weatherAllowed)
                "Approximate location allowed. Weather updates while Ride or the editor is visible. Data: Open-Meteo."
            else "Allow approximate location for live weather. If unavailable, the Clock shows Something's not right. Data: Open-Meteo.",
            PhosphorIcons.Fill.CloudLightning,
            actionLabel = if (weatherAllowed) null else "Allow location",
            onAction = {
                val activity = context as? Activity
                if (weatherRequested && activity != null &&
                    !ActivityCompat.shouldShowRequestPermissionRationale(activity,
                        Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:${context.packageName}")))
                } else weatherPermission.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
            })
        RideInfoCard("Call history access",
            if (callLogAllowed) "System Recents are available in the Calls widget. Ridemode keeps no separate call record."
            else "The Calls widget needs Android call-log access for the last five system calls. If this restricted permission cannot be granted, call history cannot be shown.",
            PhosphorIcons.Fill.Phone,
            actionLabel = if (callLogAllowed) null else "Allow call history",
            onAction = {
                val activity = context as? Activity
                if (callLogRequested && activity != null &&
                    !ActivityCompat.shouldShowRequestPermissionRationale(activity,
                        Manifest.permission.READ_CALL_LOG)) {
                    context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:${context.packageName}")))
                } else callLogPermission.launch(Manifest.permission.READ_CALL_LOG)
            })
        RideInfoCard("Music and notification access",
            when (music.availability) {
                MusicAvailability.ACCESS_REQUIRED -> "Allow Android notification access to show active music sessions and a local feed of messages and high-priority alerts."
                MusicAvailability.NO_SESSION -> "Access allowed. Open a supported music app to see its active session."
                MusicAvailability.READY -> "Connected to ${music.packageName ?: "an active music app"}."
                MusicAvailability.ERROR -> "Music sessions are unavailable. Check Android access settings."
            }, PhosphorIcons.Fill.MusicNotes,
            actionLabel = if (music.availability == MusicAvailability.ACCESS_REQUIRED ||
                music.availability == MusicAvailability.ERROR) "Open access settings" else "Refresh music",
            onAction = {
                if (music.availability == MusicAvailability.ACCESS_REQUIRED ||
                    music.availability == MusicAvailability.ERROR) {
                    runCatching { context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)) }
                        .onFailure { context.startActivity(Intent(Settings.ACTION_SETTINGS)) }
                } else viewModel.refreshMusic()
            })
        RideInfoCard("Notification feed",
            when (notifications.access) {
                NotificationAccess.REQUIRED -> "Allow notification access to show messages and high-priority alerts. Content is kept in memory only while the notification is active."
                NotificationAccess.CONNECTING -> "Connecting to Android notifications."
                NotificationAccess.READY -> "${notifications.entries.size} current messages and high-priority alerts; messages appear first."
            }, PhosphorIcons.Fill.Bell,
            actionLabel = if (notifications.access == NotificationAccess.REQUIRED) "Open access settings" else "Refresh",
            onAction = {
                if (notifications.access == NotificationAccess.REQUIRED) {
                    runCatching { context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)) }
                        .onFailure { context.startActivity(Intent(Settings.ACTION_SETTINGS)) }
                } else viewModel.refreshNotifications()
            })
        SettingToggle("Privacy mode", "Hide names and notification text on Ride and editor pages",
            PhosphorIcons.Fill.ShieldCheck, preferences.privacyMode) {
            scope.launch { viewModel.setPrivacyMode(it) }
        }
        RideInfoCard(stringResource(R.string.permissions_title),
            stringResource(R.string.settings_permissions_body), PhosphorIcons.Fill.ShieldCheck,
            actionLabel = stringResource(R.string.open_permissions), onAction = onPermissions)
    }
}

@Composable
private fun SettingsSection(title: String, description: String,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit) {
    Surface(color = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = RoundedCornerShape(RideUiTokens.panelCorner),
        modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(RideUiTokens.nonRideCardPadding),
            verticalArrangement = Arrangement.spacedBy(RideUiTokens.nonRideSectionGap)) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            Text(description, style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            content()
        }
    }
}

@Composable
private fun SettingToggle(title: String, description: String, icon: ImageVector,
    checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Surface(onClick = { onCheckedChange(!checked) },
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = RoundedCornerShape(RideUiTokens.widgetCardCorner),
        modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().padding(RideUiTokens.nonRideTogglePadding),
            horizontalArrangement = Arrangement.spacedBy(RideUiTokens.nonRideTogglePadding),
            verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(RideUiTokens.nonRideIconContainer), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(RideUiTokens.iconSize))
            }
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(description, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}
