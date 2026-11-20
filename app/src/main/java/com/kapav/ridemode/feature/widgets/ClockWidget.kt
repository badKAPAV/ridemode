package com.kapav.ridemode.feature.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Fill
import com.adamglin.phosphoricons.fill.CloudRain
import com.adamglin.phosphoricons.fill.CloudSun
import com.adamglin.phosphoricons.fill.Sun
import com.kapav.ridemode.core.ui.theme.RideUiTokens
import com.kapav.ridemode.core.ui.theme.RideThinTitleFontFamily
import java.text.SimpleDateFormat
import java.util.Date
import kotlinx.coroutines.delay

/** Local time plus live, cached or unavailable weather. */
@Composable
fun ClockWidget(weatherState: com.kapav.ridemode.data.WeatherState, modifier: Modifier = Modifier, gridRows: Int = 3) {
    var now by remember { mutableStateOf(Date()) }
    LaunchedEffect(Unit) {
        while (true) {
            now = Date()
            delay(30_000)
        }
    }
    val locale = LocalLocale.current.platformLocale
    val timeFormatter = remember(locale) { SimpleDateFormat("HH:mm", locale) }
    val shortDateFormatter = remember(locale) { SimpleDateFormat("EEE, d MMM", locale) }
    val longDateFormatter = remember(locale) { SimpleDateFormat("EEEE, d MMMM", locale) }

    Surface(modifier, color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = RoundedCornerShape(RideUiTokens.selectionCorner)) {
    BoxWithConstraints(Modifier.fillMaxSize().padding(RideUiTokens.widgetPadding)) {
        val showWeather = gridRows >= 3
        val compact = maxWidth < RideUiTokens.clockCompactWidth
        if (gridRows == 1 && maxWidth >= RideUiTokens.clockShortRowMinWidth) {
            Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(RideUiTokens.widgetPadding)) {
                AdaptiveWidgetText(timeFormatter.format(now), MaterialTheme.typography.headlineLarge,
                    MaterialTheme.colorScheme.onSurface, Modifier.weight(1f).fillMaxSize(),
                    maxSize = RideUiTokens.clockTimeMaxSp.sp, maxLines = 1, verticalCentered = true,
                    textAlign = TextAlign.Center)
                AdaptiveWidgetText(shortDateFormatter.format(now), MaterialTheme.typography.titleMedium.copy(
                    fontFamily = RideThinTitleFontFamily, fontWeight = FontWeight.Light),
                    MaterialTheme.colorScheme.onSurfaceVariant, Modifier.weight(1f).fillMaxSize(),
                    maxSize = RideUiTokens.clockShortDateMaxSp.sp, maxLines = 2, verticalCentered = true,
                    textAlign = TextAlign.Center)
            }
            return@BoxWithConstraints
        }
        val timeSize = minOf(maxHeight.value * if (showWeather) RideUiTokens.clockTimeHeightFraction else RideUiTokens.clockShortTimeHeightFraction,
            maxWidth.value * if (showWeather) RideUiTokens.clockTimeWidthFraction else RideUiTokens.clockShortTimeWidthFraction,
            RideUiTokens.clockTimeMaxSp).coerceAtLeast(RideUiTokens.clockTimeMinSp)
        val supportingSize = (timeSize * if (showWeather) RideUiTokens.clockSupportingFraction else RideUiTokens.clockShortSupportingFraction)
            .coerceIn(RideUiTokens.clockSupportingMinSp,
                if (showWeather) RideUiTokens.clockSupportingMaxSp else RideUiTokens.clockShortDateMaxSp)
        val weather = weatherPresentation(weatherState)
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                if (compact) RideUiTokens.clockCompactLineGap else RideUiTokens.clockLineGap,
                Alignment.CenterVertically)) {
            AdaptiveWidgetText(timeFormatter.format(now),
                modifier = Modifier.fillMaxWidth().weight(2f),
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = timeSize.sp),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 1)
            AdaptiveWidgetText((if (compact || !showWeather) shortDateFormatter else longDateFormatter).format(now),
                modifier = Modifier.fillMaxWidth().weight(1f),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = RideThinTitleFontFamily,
                    fontSize = supportingSize.sp, fontWeight = FontWeight.Light),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center)
            if (showWeather) Row(Modifier.fillMaxWidth().weight(1f), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(RideUiTokens.clockWeatherIconGap,
                    Alignment.CenterHorizontally)) {
                Icon(weather.icon, contentDescription = null,
                    modifier = Modifier.size(maxOf(supportingSize.dp,
                        RideUiTokens.clockWeatherIconMin)),
                    tint = MaterialTheme.colorScheme.primary)
                AdaptiveWidgetText(weather.label,
                    modifier = Modifier.weight(1f).fillMaxSize(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = RideThinTitleFontFamily,
                        fontSize = supportingSize.sp, fontWeight = FontWeight.Light),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center, maxLines = 2, verticalCentered = true)
            }
        }
    }
    }
}
