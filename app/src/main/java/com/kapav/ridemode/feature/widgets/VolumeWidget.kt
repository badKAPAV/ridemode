package com.kapav.ridemode.feature.widgets

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kapav.ridemode.core.ui.theme.RideUiTokens
import com.kapav.ridemode.data.VolumeState

/** Music-stream volume, with read-only editor presentation. */
@Composable
fun VolumeWidget(editor: Boolean, vertical: Boolean, state: VolumeState,
    onChange: (Float) -> Boolean, modifier: Modifier = Modifier) {
    val level = state.fraction
    val currentOnChange by rememberUpdatedState(onChange)
    val animatedLevel by animateFloatAsState(level,
        animationSpec = tween(RideUiTokens.volumeFillTransitionMillis),
        label = "System volume fill")
    val fillColor = MaterialTheme.colorScheme.primary
    val emptyColor = fillColor.copy(alpha = RideUiTokens.volumeEmptyOpacity)
    BoxWithConstraints(modifier.semantics {
        contentDescription = if (state.available) "Music volume, ${state.percent} percent" else "Music volume unavailable"
        if (state.available) {
            progressBarRangeInfo = ProgressBarRangeInfo(level, 0f..1f)
            if (!editor) setProgress { onChange(it.coerceIn(0f, 1f)) }
        }
    }) {
        val padding = RideUiTokens.volumeCounterInset
        val counterWidth = minOf(RideUiTokens.volumeCounterWidth,
            maxWidth * (if (vertical) RideUiTokens.volumeBarCrossAxisFraction else 1f))
        val boundary = if (vertical) maxHeight * (1f - animatedLevel)
            else maxWidth * animatedLevel
        val counterInFill = if (vertical)
            boundary < RideUiTokens.volumeCounterHeight + padding * 2
        else boundary >= counterWidth + padding * 2
        val counterX by animateDpAsState(
            targetValue = if (vertical || counterInFill) padding
                else (boundary + RideUiTokens.volumeSplitGap / 2 + padding)
                    .coerceAtMost((maxWidth - counterWidth - padding).coerceAtLeast(0.dp)),
            animationSpec = tween(RideUiTokens.volumeCounterTransitionMillis),
            label = "Volume counter horizontal position")
        val counterY by animateDpAsState(
            targetValue = if (!vertical || !counterInFill) padding
                else (boundary + RideUiTokens.volumeSplitGap / 2 + padding)
                    .coerceAtMost((maxHeight - RideUiTokens.volumeCounterHeight - padding)
                        .coerceAtLeast(0.dp)),
            animationSpec = tween(RideUiTokens.volumeCounterTransitionMillis),
            label = "Volume counter vertical position")

        Canvas(Modifier.fillMaxSize()
            .then(if (editor || !state.available) Modifier else Modifier.pointerInput(vertical) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    fun update(position: Offset) {
                        currentOnChange((if (vertical) 1f - position.y / size.height
                            else position.x / size.width).coerceIn(0f, 1f))
                    }
                    update(down.position)
                    down.consume()
                    do {
                        val event = awaitPointerEvent()
                        event.changes.forEach { change ->
                            if (change.pressed) update(change.position)
                            change.consume()
                        }
                    } while (event.changes.any { it.pressed })
                }
            })) {
            val gap = RideUiTokens.volumeSplitGap.toPx()
            val lineThickness = RideUiTokens.volumeDividerThickness.toPx()
            if (vertical) {
                val barWidth = size.width * RideUiTokens.volumeBarCrossAxisFraction
                val barLeft = (size.width - barWidth) / 2f
                val boundaryPx = size.height * (1f - animatedLevel)
                val hasBoth = animatedLevel > 0f && animatedLevel < 1f
                val emptyBottom = if (hasBoth) (boundaryPx - gap / 2f).coerceAtLeast(0f)
                    else boundaryPx
                val fillTop = if (hasBoth) (boundaryPx + gap / 2f).coerceAtMost(size.height)
                    else boundaryPx
                drawVolumeSegment(emptyColor, barLeft, 0f, barWidth, emptyBottom,
                    outerAtStart = true, vertical = true, roundBoth = !hasBoth)
                drawVolumeSegment(fillColor, barLeft, fillTop, barWidth, size.height - fillTop,
                    outerAtStart = false, vertical = true, roundBoth = !hasBoth)
                if (hasBoth) drawLine(fillColor,
                    Offset(0f, boundaryPx), Offset(size.width, boundaryPx),
                    strokeWidth = lineThickness, cap = StrokeCap.Round)
            } else {
                val barHeight = size.height * RideUiTokens.volumeBarCrossAxisFraction
                val barTop = (size.height - barHeight) / 2f
                val boundaryPx = size.width * animatedLevel
                val hasBoth = animatedLevel > 0f && animatedLevel < 1f
                val fillRight = if (hasBoth) (boundaryPx - gap / 2f).coerceAtLeast(0f)
                    else boundaryPx
                val emptyLeft = if (hasBoth) (boundaryPx + gap / 2f).coerceAtMost(size.width)
                    else boundaryPx
                drawVolumeSegment(fillColor, 0f, barTop, fillRight, barHeight,
                    outerAtStart = true, vertical = false, roundBoth = !hasBoth)
                drawVolumeSegment(emptyColor, emptyLeft, barTop, size.width - emptyLeft, barHeight,
                    outerAtStart = false, vertical = false, roundBoth = !hasBoth)
                if (hasBoth) drawLine(fillColor,
                    Offset(boundaryPx, 0f), Offset(boundaryPx, size.height),
                    strokeWidth = lineThickness, cap = StrokeCap.Round)
            }
        }
        AdaptiveWidgetText(if (state.available) "${state.percent}" else "—",
            modifier = if (vertical) Modifier.align(Alignment.TopCenter).offset(y = counterY)
                .width(counterWidth).height(minOf(maxHeight, RideUiTokens.volumeCounterHeight))
            else Modifier.align(Alignment.CenterStart).offset(x = counterX).width(counterWidth)
                .height(minOf(maxHeight, RideUiTokens.volumeCounterHeight)),
            style = MaterialTheme.typography.titleMedium,
            color = if (counterInFill) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            maxLines = 1)
    }
}

/** Segments have round outer ends and flat ends facing the detached divider. */
private fun DrawScope.drawVolumeSegment(
    color: Color, left: Float, top: Float, width: Float, height: Float,
    outerAtStart: Boolean, vertical: Boolean, roundBoth: Boolean
) {
    if (width <= 0f || height <= 0f) return
    val length = if (vertical) height else width
    val cross = if (vertical) width else height
    val radius = minOf(cross * RideUiTokens.volumeCornerFraction, length / 2f)
    if (roundBoth) {
        drawRoundRect(color, Offset(left, top), Size(width, height), CornerRadius(radius))
        return
    }
    val right = left + width
    val bottom = top + height
    val path = Path()
    if (vertical) {
        if (outerAtStart) {
            path.moveTo(left, bottom)
            path.lineTo(left, top + radius)
            path.quadraticTo(left, top, left + radius, top)
            path.lineTo(right - radius, top)
            path.quadraticTo(right, top, right, top + radius)
            path.lineTo(right, bottom)
        } else {
            path.moveTo(left, top)
            path.lineTo(right, top)
            path.lineTo(right, bottom - radius)
            path.quadraticTo(right, bottom, right - radius, bottom)
            path.lineTo(left + radius, bottom)
            path.quadraticTo(left, bottom, left, bottom - radius)
        }
    } else {
        if (outerAtStart) {
            path.moveTo(right, top)
            path.lineTo(left + radius, top)
            path.quadraticTo(left, top, left, top + radius)
            path.lineTo(left, bottom - radius)
            path.quadraticTo(left, bottom, left + radius, bottom)
            path.lineTo(right, bottom)
        } else {
            path.moveTo(left, top)
            path.lineTo(right - radius, top)
            path.quadraticTo(right, top, right, top + radius)
            path.lineTo(right, bottom - radius)
            path.quadraticTo(right, bottom, right - radius, bottom)
            path.lineTo(left, bottom)
        }
    }
    path.close()
    drawPath(path, color)
}
