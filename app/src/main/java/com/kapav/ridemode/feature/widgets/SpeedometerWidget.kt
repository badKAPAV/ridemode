package com.kapav.ridemode.feature.widgets

import android.Manifest
import android.app.Activity
import android.net.Uri
import androidx.core.app.ActivityCompat
import androidx.compose.runtime.saveable.rememberSaveable
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.kapav.ridemode.core.model.SpeedUnit
import com.kapav.ridemode.core.model.WidgetConfigs
import com.kapav.ridemode.core.ui.theme.RideUiTokens
import com.kapav.ridemode.data.SpeedState
import com.kapav.ridemode.data.SpeedStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.roundToInt

private val emptySpeed = MutableStateFlow(SpeedState())
val LocalSpeedState = staticCompositionLocalOf<StateFlow<SpeedState>> { emptySpeed }
val LocalSpeedRefresh = staticCompositionLocalOf<() -> Unit> { {} }
val LocalSpeedUnit = staticCompositionLocalOf { SpeedUnit.KMH }

@Composable
fun SpeedometerWidget(widgetId: String, configJson: String, editor: Boolean, modifier: Modifier = Modifier) {
    val config = remember(configJson) { WidgetConfigs.speed(configJson) }
    val unit = config.resolvedUnit(LocalSpeedUnit.current)
    val live by LocalSpeedState.current.collectAsState()
    val state = if (editor) SpeedState(SpeedStatus.LIVE, 0f) else live
    val refresh = LocalSpeedRefresh.current
    var asked by rememberSaveable(widgetId) { mutableStateOf(false) }
    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { refresh() }
    val context = LocalContext.current
    val value = state.metersPerSecond?.let { (it * unit.factor).roundToInt().toString() } ?: "—"
    val status = if (editor) "Preview" else if (state.simulated) "Simulated GPS" else state.status.label
    Surface(modifier = modifier.semantics { contentDescription = "Speed $value ${unit.label}; $status" },
        onClick = {
            if (state.status == SpeedStatus.PERMISSION) {
                val activity = context as? Activity
                if (asked && activity != null && !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    runCatching { context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${context.packageName}"))) }
                } else {
                    asked = true
                    permission.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
                }
            }
            else if (state.status == SpeedStatus.DISABLED) runCatching { context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
            else refresh()
        }, enabled = !editor, shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerHigh) {
        BoxWithConstraints(Modifier.fillMaxSize().padding(RideUiTokens.speedInset)) {
            val wide = maxWidth > maxHeight * RideUiTokens.speedWideRatio
            val wideStatusRoom = maxHeight >= RideUiTokens.minimumTouchTarget
            val tallStatusRoom = maxHeight >= RideUiTokens.widgetCompactHeight
            val textColor = if (state.metersPerSecond != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
            if (wide) Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(RideUiTokens.widgetPadding)) {
                AdaptiveWidgetText(value, MaterialTheme.typography.headlineLarge, textColor,
                    Modifier.weight(1f).fillMaxHeight(), maxSize = RideUiTokens.speedMaxSp.sp,
                    maxLines = 1, textAlign = TextAlign.Center, verticalCentered = true)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(unit.label, style = MaterialTheme.typography.labelSmall)
                    if (config.showStatus && wideStatusRoom)
                        Text(status, style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            } else Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                AdaptiveWidgetText(value, MaterialTheme.typography.headlineLarge, textColor,
                    Modifier.fillMaxWidth().weight(1f), maxSize = RideUiTokens.speedMaxSp.sp,
                    maxLines = 1, textAlign = TextAlign.Center, verticalCentered = true)
                Text(unit.label, style = MaterialTheme.typography.labelSmall, maxLines = 1)
                if (config.showStatus && tallStatusRoom)
                    Text(status, style = MaterialTheme.typography.labelSmall, maxLines = 2,
                        textAlign = TextAlign.Center, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}
