package com.kapav.ridemode.feature.ride

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Fill
import com.adamglin.phosphoricons.fill.BatteryCharging
import com.adamglin.phosphoricons.fill.BatteryFull
import com.adamglin.phosphoricons.fill.BatteryHigh
import com.adamglin.phosphoricons.fill.BatteryLow
import com.adamglin.phosphoricons.fill.BatteryMedium
import com.adamglin.phosphoricons.fill.CloudSun
import com.adamglin.phosphoricons.fill.MusicNotes
import com.kapav.ridemode.core.ui.theme.RideUiTokens
import com.kapav.ridemode.core.ui.theme.RideHeavyTitleFontFamily
import com.kapav.ridemode.data.OledSaverStyle
import com.kapav.ridemode.data.RidePreferences
import com.kapav.ridemode.data.WeatherState
import com.kapav.ridemode.feature.widgets.weatherPresentation
import com.kapav.ridemode.data.MusicAvailability
import com.kapav.ridemode.data.MusicSessionState
import java.text.SimpleDateFormat
import java.util.Date
import kotlinx.coroutines.delay

@Composable
internal fun OledSaverContent(preferences: RidePreferences, music: MusicSessionState, weather: WeatherState) {
    val context = LocalContext.current
    var battery by remember(context) { mutableStateOf(
        (context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager)
            ?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
            ?.takeIf { it in 0..100 }
    ) }
    var charging by remember(context) { mutableStateOf(false) }
    DisposableEffect(context) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
                val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
                battery = if (level >= 0 && scale > 0) (level * 100 / scale).coerceIn(0, 100)
                    else null
                charging = (intent?.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) ?: 0) != 0
            }
        }
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            context.registerReceiver(receiver, filter)
        }
        onDispose { context.unregisterReceiver(receiver) }
    }
    var now by remember { mutableStateOf(Date()) }
    LaunchedEffect(Unit) {
        while (true) { now = Date(); delay(15_000) }
    }
    Box(Modifier.fillMaxSize().background(Color.Black)) {
        if (preferences.saverStyle == OledSaverStyle.ROW) {
            OledRowStyle(now, battery, charging, preferences.outlineText, music, weather)
        } else {
            OledClassicStyle(now, battery, preferences.outlineText, music, weather)
        }
        Text("Tap once to wake", color = if (preferences.saverStyle == OledSaverStyle.ROW)
            Color.White else Color.White.copy(alpha = RideUiTokens.oledWakeHintOpacity),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.align(Alignment.BottomCenter)
                .padding(bottom = RideUiTokens.rideScreenPadding))
    }
}

@Composable
private fun OledClassicStyle(now: Date, battery: Int?, outline: Boolean,
    music: MusicSessionState, weather: WeatherState) {
    val locale = LocalLocale.current.platformLocale
    Column(Modifier.fillMaxSize().padding(RideUiTokens.rideScreenPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(RideUiTokens.widgetPadding)) {
        Text(SimpleDateFormat("HH:mm", locale).format(now),
            color = Color.White,
            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 96.sp,
                drawStyle = if (outline) Stroke(width = 2f)
                    else androidx.compose.ui.graphics.drawscope.Fill,
                textAlign = TextAlign.Center))
        Spacer(Modifier.weight(0.35f))
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(RideUiTokens.widgetPadding)) {
            Icon(weatherPresentation(weather).icon, contentDescription = null,
                tint = Color.White, modifier = Modifier.size(RideUiTokens.iconSize))
            Text(weatherPresentation(weather).label, color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(RideUiTokens.widgetPadding)) {
            Icon(PhosphorIcons.Fill.MusicNotes, contentDescription = null,
                tint = Color.White, modifier = Modifier.size(RideUiTokens.iconSize))
            Column {
                Text(music.saverTitle(), color = Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(music.saverArtist(),
                    color = Color.White.copy(alpha = RideUiTokens.pageIndicatorCurrentOpacity),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        Spacer(Modifier.weight(0.65f))
        if (battery != null) {
            Text("Battery $battery%", color = Color.White,
                style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(Modifier.size(RideUiTokens.iconSize))
    }
}

@Composable
private fun OledRowStyle(now: Date, battery: Int?, charging: Boolean, outline: Boolean,
    music: MusicSessionState, weather: WeatherState) {
    val locale = LocalLocale.current.platformLocale
    val fontScale = LocalDensity.current.fontScale
    BoxWithConstraints(Modifier.fillMaxSize().padding(RideUiTokens.rideScreenPadding)) {
        val clockSize = (maxHeight.value * RideUiTokens.oledRowClockHeightFraction / fontScale)
            .coerceIn(RideUiTokens.oledRowClockMinSp, RideUiTokens.oledRowClockMaxSp).sp
        Row(Modifier.fillMaxSize().padding(bottom = RideUiTokens.oledRowWakeClearance),
            horizontalArrangement = Arrangement.spacedBy(RideUiTokens.contentGap),
            verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(0.9f), horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {
                val clockStyle = MaterialTheme.typography.displayLarge.copy(
                    fontFamily = RideHeavyTitleFontFamily,
                    fontSize = clockSize, lineHeight = clockSize, fontWeight = FontWeight(1000),
                    drawStyle = if (outline) Stroke(width = 2f)
                        else androidx.compose.ui.graphics.drawscope.Fill)
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(-RideUiTokens.oledRowClockLineOverlap)) {
                    Text(SimpleDateFormat("HH", locale).format(now),
                        style = clockStyle, color = Color.White, maxLines = 1)
                    Text(SimpleDateFormat("mm", locale).format(now),
                        style = clockStyle, color = Color.White, maxLines = 1)
                }
                Text(SimpleDateFormat("EEE, d MMM", locale).format(now),
                    style = MaterialTheme.typography.bodyMedium, color = Color.White,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Column(Modifier.weight(1.1f).fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(RideUiTokens.oledRowPanelGap)) {
                OledRowPanel(Modifier.weight(1f)) {
                    Icon(PhosphorIcons.Fill.MusicNotes, contentDescription = null,
                        tint = Color.White, modifier = Modifier.size(RideUiTokens.iconSize))
                    Text(music.saverTitle(), style = MaterialTheme.typography.titleMedium,
                        color = Color.White, textAlign = TextAlign.Center,
                        maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(music.saverArtist(), style = MaterialTheme.typography.bodySmall,
                        color = Color.White, textAlign = TextAlign.Center,
                        maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                OledRowPanel(Modifier.weight(1f)) {
                    Icon(weatherPresentation(weather).icon, contentDescription = null,
                        tint = Color.White, modifier = Modifier.size(RideUiTokens.iconSize))
                    Text(weatherPresentation(weather).label, style = MaterialTheme.typography.titleMedium,
                        color = Color.White, textAlign = TextAlign.Center,
                        maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                val icon = when {
                    charging -> PhosphorIcons.Fill.BatteryCharging
                    battery == null || battery >= 80 -> PhosphorIcons.Fill.BatteryFull
                    battery >= 55 -> PhosphorIcons.Fill.BatteryHigh
                    battery >= 25 -> PhosphorIcons.Fill.BatteryMedium
                    else -> PhosphorIcons.Fill.BatteryLow
                }
                OledRowPanel(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(RideUiTokens.widgetPadding)) {
                        Icon(icon, contentDescription = if (charging) "Charging" else "Battery",
                            tint = Color.White, modifier = Modifier.size(RideUiTokens.iconSize))
                        Text(battery?.let { "$it%" } ?: "Unavailable",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White, textAlign = TextAlign.Center,
                            maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
    }
}

private fun MusicSessionState.saverTitle(): String = when (availability) {
    MusicAvailability.READY -> title ?: "Unknown track"
    MusicAvailability.ACCESS_REQUIRED -> "Music access needed"
    MusicAvailability.NO_SESSION -> "No active music"
    MusicAvailability.ERROR -> "Music unavailable"
}

private fun MusicSessionState.saverArtist(): String = when (availability) {
    MusicAvailability.READY -> artist ?: packageName.orEmpty()
    MusicAvailability.ACCESS_REQUIRED -> "Enable in Settings"
    MusicAvailability.NO_SESSION -> "Open a music app"
    MusicAvailability.ERROR -> "Check access in Settings"
}

@Composable
private fun OledRowPanel(modifier: Modifier,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit) {
    Column(modifier.fillMaxWidth().border(RideUiTokens.oledRowStrokeWidth, Color.White,
        RoundedCornerShape(RideUiTokens.widgetCardCorner))
        .background(Color.Black, RoundedCornerShape(RideUiTokens.widgetCardCorner))
        .padding(horizontal = RideUiTokens.contentGap,
            vertical = RideUiTokens.oledRowPanelVerticalPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        content = content)
}
