package com.kapav.ridemode.feature.widgets

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import com.kapav.ridemode.core.ui.theme.RideThinTitleFontFamily
import com.kapav.ridemode.core.ui.theme.RideUiTokens
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Fill
import com.adamglin.phosphoricons.fill.Lightning

private fun batteryLevel(context: Context): Int? =
    (context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager)
        ?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        ?.takeIf { it in 0..100 }

private fun batteryLevel(intent: Intent): Int? {
    val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
    val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
    return if (level >= 0 && scale > 0) (level * 100 / scale).coerceIn(0, 100) else null
}

internal fun batteryTextOnFill(filledWidth: Float, emptyWidth: Float, requiredWidth: Float): Boolean =
    filledWidth >= requiredWidth ||
        (emptyWidth < requiredWidth && filledWidth >= emptyWidth)

/** The local battery is live when available; the fallback is explicitly marked mock. */
@Composable
fun BatteryWidget(vertical: Boolean, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var realLevel by remember(context) { mutableStateOf(batteryLevel(context)) }
    var charging by remember(context) { mutableStateOf(false) }
    DisposableEffect(context) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let { batteryLevel(it) }?.let { realLevel = it }
                charging = intent?.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) != 0
            }
        }
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            @Suppress("DEPRECATION")
            context.registerReceiver(receiver, filter)
        }
        onDispose { context.unregisterReceiver(receiver) }
    }

    val level = realLevel ?: RideUiTokens.batteryMockFallbackPercent
    val animatedLevel by animateFloatAsState(level / 100f,
        animationSpec = tween(RideUiTokens.batteryTransitionMillis),
        label = "Battery split")
    val fillColor = MaterialTheme.colorScheme.primary
    val emptyColor = fillColor.copy(alpha = RideUiTokens.batteryEmptyOpacity)

    BoxWithConstraints(modifier) {
        val gap = RideUiTokens.batterySplitGap
        val inset = RideUiTokens.batteryTextInset
        if (vertical) {
            val filledHeight = (maxHeight * animatedLevel - gap / 2).coerceAtLeast(0.dp)
            val emptyHeight = (maxHeight * (1f - animatedLevel) - gap / 2).coerceAtLeast(0.dp)
            val onFill = batteryTextOnFill(filledHeight.value, emptyHeight.value,
                RideUiTokens.batteryVerticalTextMinHeight.value)
            Canvas(Modifier.fillMaxSize()) {
                val boundaryPx = size.height * (1f - animatedLevel)
                val gapPx = gap.toPx()
                val topEnd = (boundaryPx - gapPx / 2f).coerceAtLeast(0f)
                val bottomStart = (boundaryPx + gapPx / 2f).coerceAtMost(size.height)
                val radius = minOf(size.width * RideUiTokens.batteryCornerFraction,
                    size.width / 2f)
                if (topEnd > 0f) drawRoundRect(emptyColor, size = Size(size.width, topEnd),
                    cornerRadius = CornerRadius(radius))
                if (bottomStart < size.height) drawRoundRect(fillColor,
                    topLeft = Offset(0f, bottomStart),
                    size = Size(size.width, size.height - bottomStart),
                    cornerRadius = CornerRadius(radius))
            }
            val regionHeight = if (onFill) filledHeight else emptyHeight
            val targetY = if (onFill) maxHeight - regionHeight else 0.dp
            val textY by animateDpAsState(targetY,
                animationSpec = tween(RideUiTokens.batteryTransitionMillis),
                label = "Battery vertical text side")
            Box(Modifier.fillMaxWidth().offset(y = textY).height(regionHeight),
                contentAlignment = Alignment.Center) {
                BatteryText(level, realLevel == null, charging, onFill, Modifier.fillMaxSize()
                    .padding(RideUiTokens.batteryVerticalTextInset), compact = true)
            }
            return@BoxWithConstraints
        }
        val boundary = maxWidth * animatedLevel
        val filledWidth = (boundary - gap / 2).coerceAtLeast(0.dp)
        val emptyWidth = (maxWidth - boundary - gap / 2).coerceAtLeast(0.dp)
        val idealPanelWidth = maxWidth * RideUiTokens.batteryTextWidthFraction
        val onFill = batteryTextOnFill(filledWidth.value, emptyWidth.value,
            (idealPanelWidth + inset).value)
        val sideWidth = if (onFill) filledWidth else emptyWidth
        val panelWidth = minOf(idealPanelWidth, (sideWidth - inset).coerceAtLeast(0.dp))
        val targetX = if (onFill) inset else boundary + gap / 2 + inset
        val textX by animateDpAsState(targetX,
            animationSpec = tween(RideUiTokens.batteryTransitionMillis),
            label = "Battery text side")

        Canvas(Modifier.fillMaxSize()) {
            val boundaryPx = size.width * animatedLevel
            val gapPx = gap.toPx()
            val hasBoth = animatedLevel > 0f && animatedLevel < 1f
            val leftRight = if (hasBoth) (boundaryPx - gapPx / 2).coerceAtLeast(0f)
                else boundaryPx
            val rightLeft = if (hasBoth) (boundaryPx + gapPx / 2).coerceAtMost(size.width)
                else boundaryPx
            drawBatterySegment(fillColor, 0f, leftRight, size.height, outerOnLeft = true,
                roundBoth = !hasBoth)
            drawBatterySegment(emptyColor, rightLeft, size.width - rightLeft, size.height,
                outerOnLeft = false, roundBoth = !hasBoth)
        }
        BatteryText(level, realLevel == null, charging, onFill,
            Modifier.align(Alignment.CenterStart).offset(x = textX).width(panelWidth)
                .height(maxHeight - RideUiTokens.batteryHorizontalVerticalInset * 2), compact = false)
    }
}

@Composable
private fun BatteryText(level: Int, mock: Boolean, charging: Boolean, onFill: Boolean,
    modifier: Modifier, compact: Boolean) {
    val labelColor = if (onFill) MaterialTheme.colorScheme.onPrimary.copy(
        alpha = RideUiTokens.batteryLabelOpacity) else MaterialTheme.colorScheme.onSurfaceVariant
    val percentColor = if (onFill) MaterialTheme.colorScheme.onPrimary
        else MaterialTheme.colorScheme.onSurface
    Column(modifier, verticalArrangement = Arrangement.Center) {
        Row(Modifier.fillMaxWidth().weight(1f), verticalAlignment = Alignment.CenterVertically) {
            AdaptiveWidgetText(if (mock) "Mock phone" else "This phone",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = RideThinTitleFontFamily, fontWeight = FontWeight.Light),
                color = labelColor, modifier = Modifier.weight(1f).fillMaxSize(),
                maxSize = (if (compact) RideUiTokens.batteryLabelMaxSp * 0.8f
                    else RideUiTokens.batteryLabelMaxSp).sp,
                minSize = 13.sp,
                textAlign = if (compact) TextAlign.Center else TextAlign.Start,
                verticalCentered = true)
            if (charging) Icon(PhosphorIcons.Fill.Lightning, contentDescription = "Charging",
                tint = percentColor, modifier = Modifier.size(RideUiTokens.iconSize))
        }
        AdaptiveWidgetText("$level%", style = MaterialTheme.typography.headlineLarge,
            color = percentColor, modifier = Modifier.fillMaxWidth().weight(2f),
            maxSize = RideUiTokens.batteryPercentMaxSp.sp,
            minSize = if (compact) 22.sp else 30.sp,
            textAlign = if (compact) TextAlign.Center else TextAlign.Start)
    }
}

/** Two independently rounded outer edges leave a clean gap at the level boundary. */
private fun DrawScope.drawBatterySegment(
    color: Color, left: Float, width: Float, height: Float,
    outerOnLeft: Boolean, roundBoth: Boolean
) {
    if (width <= 0f || height <= 0f) return
    val right = left + width
    val radius = minOf(height * RideUiTokens.batteryCornerFraction, width / 2f)
    if (roundBoth) {
        drawRoundRect(color, Offset(left, 0f), Size(width, height), CornerRadius(radius))
        return
    }
    val path = Path()
    if (outerOnLeft) {
        path.moveTo(right, 0f)
        path.lineTo(left + radius, 0f)
        path.quadraticTo(left, 0f, left, radius)
        path.lineTo(left, height - radius)
        path.quadraticTo(left, height, left + radius, height)
        path.lineTo(right, height)
    } else {
        path.moveTo(left, 0f)
        path.lineTo(right - radius, 0f)
        path.quadraticTo(right, 0f, right, radius)
        path.lineTo(right, height - radius)
        path.quadraticTo(right, height, right - radius, height)
        path.lineTo(left, height)
    }
    path.close()
    drawPath(path, color)
}
