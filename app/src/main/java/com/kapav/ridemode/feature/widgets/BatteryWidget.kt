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
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
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

internal data class BatteryContentPlacement(
    val percentStart: Float, val percentLength: Float, val percentOnFill: Boolean,
    val iconStart: Float, val iconLength: Float, val iconOnFill: Boolean
)

/** Logical axis starts at the filled end: left in landscape, bottom in portrait. */
internal fun batteryContentPlacement(length: Float, fraction: Float, gap: Float, inset: Float,
    percentLength: Float, iconLength: Float): BatteryContentPlacement {
    val fill = fraction.coerceIn(0f, 1f)
    val splitGap = if (fill > 0f && fill < 1f) gap else 0f
    val boundary = length * fill
    val filledEnd = (boundary - splitGap / 2f).coerceAtLeast(0f)
    val emptyStart = (boundary + splitGap / 2f).coerceAtMost(length)
    val filledLength = filledEnd
    val emptyLength = length - emptyStart
    val iconOnFill = emptyLength < iconLength + inset * 2 && filledLength >= emptyLength
    val iconRegionStart = if (iconOnFill) 0f else emptyStart
    val iconRegionEnd = if (iconOnFill) filledEnd else length
    val actualIconLength = minOf(iconLength, (iconRegionEnd - iconRegionStart - inset * 2).coerceAtLeast(0f))
    val iconStart = (iconRegionEnd - inset - actualIconLength).coerceAtLeast(iconRegionStart)
    val filledRoom = (filledLength - inset * 2 -
        if (iconLength > 0f && iconOnFill) actualIconLength + inset else 0f).coerceAtLeast(0f)
    val emptyRoom = (emptyLength - inset * 2 -
        if (iconLength > 0f && !iconOnFill) actualIconLength + inset else 0f).coerceAtLeast(0f)
    val percentOnFill = batteryTextOnFill(filledRoom, emptyRoom, percentLength)
    val percentRegionStart = if (percentOnFill) 0f else emptyStart
    val percentRegionEnd = if (percentOnFill) filledEnd else length
    val percentStart = (percentRegionStart + inset).coerceAtMost(percentRegionEnd)
    val percentEnd = if (iconLength > 0f && percentOnFill == iconOnFill) iconStart - inset
        else percentRegionEnd - inset
    return BatteryContentPlacement(percentStart,
        minOf(percentLength, (percentEnd - percentStart).coerceAtLeast(0f)), percentOnFill,
        iconStart, actualIconLength, iconOnFill)
}

/** Local battery with independently fitted percentage and power status at opposite bar ends. */
@Composable
fun BatteryWidget(vertical: Boolean, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var realLevel by remember(context) { mutableStateOf(batteryLevel(context)) }
    var charging by remember(context) { mutableStateOf(false) }
    DisposableEffect(context) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let { batteryLevel(it) }?.let { realLevel = it }
                charging = (intent?.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) ?: 0) != 0
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
        animationSpec = tween(RideUiTokens.batteryTransitionMillis), label = "Battery split")
    val fillColor = MaterialTheme.colorScheme.primary
    val emptyColor = fillColor.copy(alpha = RideUiTokens.batteryEmptyOpacity)
    BoxWithConstraints(modifier) {
        val length = if (vertical) maxHeight else maxWidth
        val percentExtent = if (vertical) RideUiTokens.batteryVerticalTextMinHeight
            else maxWidth * RideUiTokens.batteryTextWidthFraction
        val placement = batteryContentPlacement(length.value, animatedLevel,
            RideUiTokens.batterySplitGap.value, RideUiTokens.batteryTextInset.value,
            percentExtent.value, if (charging) RideUiTokens.iconSize.value else 0f)
        val percentPosition = if (vertical) length.value - placement.percentStart - placement.percentLength
            else placement.percentStart
        val iconPosition = if (vertical) length.value - placement.iconStart - placement.iconLength
            else placement.iconStart
        val percentOffset by animateDpAsState(percentPosition.dp,
            tween(RideUiTokens.batteryTransitionMillis), label = "Battery percentage end")
        val iconOffset by animateDpAsState(iconPosition.dp,
            tween(RideUiTokens.batteryTransitionMillis), label = "Battery status end")
        Canvas(Modifier.fillMaxSize()) {
            val axis = if (vertical) size.height else size.width
            val cross = if (vertical) size.width else size.height
            if (vertical) withTransform({
                translate(0f, size.height)
                rotate(-90f, Offset.Zero)
            }) { drawBatteryBar(fillColor, emptyColor, animatedLevel,
                RideUiTokens.batterySplitGap.toPx(), axis, cross) }
            else drawBatteryBar(fillColor, emptyColor, animatedLevel,
                RideUiTokens.batterySplitGap.toPx(), axis, cross)
        }
        if (placement.percentLength > 0f) {
            val percentModifier = Modifier.offset(x = if (vertical) 0.dp else percentOffset,
                y = if (vertical) percentOffset else 0.dp).then(
                    if (vertical) Modifier.fillMaxWidth().height(placement.percentLength.dp)
                        .padding(horizontal = RideUiTokens.batteryTextInset)
                    else Modifier.width(placement.percentLength.dp).fillMaxHeight()
                        .padding(vertical = RideUiTokens.batteryHorizontalVerticalInset))
            AdaptiveWidgetText(if (realLevel == null) "$level% · Mock" else "$level%",
                MaterialTheme.typography.headlineLarge,
                if (placement.percentOnFill) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                percentModifier, minSize = RideUiTokens.batteryPercentMinSp.sp,
                maxSize = RideUiTokens.batteryPercentMaxSp.sp, maxLines = if (realLevel == null) 2 else 1,
                textAlign = if (vertical) TextAlign.Center else TextAlign.Start, verticalCentered = true)
        }
        if (charging && placement.iconLength > 0f) {
            Box(Modifier.offset(x = if (vertical) 0.dp else iconOffset,
                y = if (vertical) iconOffset else 0.dp).then(
                    if (vertical) Modifier.fillMaxWidth().height(placement.iconLength.dp)
                    else Modifier.width(placement.iconLength.dp).fillMaxHeight()),
                contentAlignment = Alignment.Center) {
                Icon(PhosphorIcons.Fill.Lightning, contentDescription = "Connected to power",
                    modifier = Modifier.size(placement.iconLength.dp),
                    tint = if (placement.iconOnFill) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

private fun DrawScope.drawBatteryBar(fillColor: Color, emptyColor: Color, fraction: Float,
    gap: Float, length: Float, cross: Float) {
    val boundary = length * fraction
    val hasBoth = fraction > 0f && fraction < 1f
    val filledEnd = if (hasBoth) (boundary - gap / 2).coerceAtLeast(0f) else boundary
    val emptyStart = if (hasBoth) (boundary + gap / 2).coerceAtMost(length) else boundary
    drawBatterySegment(fillColor, 0f, filledEnd, cross, outerOnLeft = true, roundBoth = !hasBoth)
    drawBatterySegment(emptyColor, emptyStart, length - emptyStart, cross, outerOnLeft = false, roundBoth = !hasBoth)
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
