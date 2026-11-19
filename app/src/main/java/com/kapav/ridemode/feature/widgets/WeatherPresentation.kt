package com.kapav.ridemode.feature.widgets

import androidx.compose.ui.graphics.vector.ImageVector
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Fill
import com.adamglin.phosphoricons.fill.Cloud
import com.adamglin.phosphoricons.fill.CloudFog
import com.adamglin.phosphoricons.fill.CloudLightning
import com.adamglin.phosphoricons.fill.CloudRain
import com.adamglin.phosphoricons.fill.CloudSnow
import com.adamglin.phosphoricons.fill.CloudSun
import com.adamglin.phosphoricons.fill.Sun
import com.kapav.ridemode.data.WeatherState

data class WeatherPresentation(val label: String, val icon: ImageVector)

fun weatherPresentation(state: WeatherState, nowMs: Long = System.currentTimeMillis()): WeatherPresentation {
    val reading = state.displayReading(nowMs)
        ?: return if (state.loading) WeatherPresentation("Checking weather", PhosphorIcons.Fill.CloudSun)
            else WeatherPresentation("Something's not right", PhosphorIcons.Fill.CloudLightning)
    val condition = when (reading.code) {
        0 -> "Clear" to PhosphorIcons.Fill.Sun
        1, 2 -> "Partly cloudy" to PhosphorIcons.Fill.CloudSun
        3 -> "Overcast" to PhosphorIcons.Fill.Cloud
        45, 48 -> "Fog" to PhosphorIcons.Fill.CloudFog
        in 51..67, in 80..82 -> "Rain" to PhosphorIcons.Fill.CloudRain
        in 71..77, 85, 86 -> "Snow" to PhosphorIcons.Fill.CloudSnow
        in 95..99 -> "Thunderstorm" to PhosphorIcons.Fill.CloudLightning
        else -> return WeatherPresentation("Something's not right", PhosphorIcons.Fill.CloudLightning)
    }
    val ageMinutes = ((nowMs - reading.fetchedAtMs) / 60_000L).coerceAtLeast(0)
    val freshness = when {
        ageMinutes >= 60 -> " · ${ageMinutes / 60}h old"
        ageMinutes >= 45 -> " · ${ageMinutes}m old"
        else -> ""
    }
    return WeatherPresentation("${reading.temperatureC}°C · ${condition.first}$freshness", condition.second)
}
