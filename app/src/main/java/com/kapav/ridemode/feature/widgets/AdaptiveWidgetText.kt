package com.kapav.ridemode.feature.widgets

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

/** Fits complete, wrapping widget text inside its measured bounds, including font scaling. */
@Composable
internal fun AdaptiveWidgetText(
    text: String,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
    minSize: TextUnit = 14.sp,
    maxSize: TextUnit = style.fontSize,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    verticalCentered: Boolean = false,
    allowEmergencyShrink: Boolean = true,
    overflow: TextOverflow = TextOverflow.Clip
) {
    BoxWithConstraints(modifier, contentAlignment = if (verticalCentered) Alignment.CenterStart else Alignment.TopStart) {
        val density = LocalDensity.current
        val measurer = rememberTextMeasurer()
        val bounds = remember(maxWidth, maxHeight, density) {
            with(density) { Constraints(maxWidth = maxWidth.roundToPx().coerceAtLeast(1),
                maxHeight = maxHeight.roundToPx().coerceAtLeast(1)) }
        }
        val lower = minSize.value.coerceAtMost(maxSize.value)
        val upper = maxSize.value.coerceAtLeast(lower)
        fun fittedStyle(size: Float): TextStyle = style.copy(
            fontSize = size.sp,
            lineHeight = if (style.lineHeight.isSp && style.fontSize.isSp && style.fontSize.value > 0f)
                (style.lineHeight.value * size / style.fontSize.value).sp else style.lineHeight)
        val fitted = remember(text, style, bounds, lower, upper, maxLines, measurer, allowEmergencyShrink) {
            fun fits(size: Float): Boolean = !measurer.measure(text, style = fittedStyle(size),
                constraints = bounds, softWrap = true, maxLines = maxLines).hasVisualOverflow
            val smallest = if (fits(lower) || !allowEmergencyShrink) lower else {
                var emergencyLow = 9f.coerceAtMost(lower)
                var emergencyHigh = lower
                repeat(7) {
                    val candidate = (emergencyLow + emergencyHigh) / 2f
                    if (fits(candidate)) emergencyLow = candidate else emergencyHigh = candidate
                }
                emergencyLow
            }
            var low = smallest
            var high = upper
            repeat(8) {
                val candidate = (low + high) / 2f
                if (fits(candidate)) low = candidate else high = candidate
            }
            low.sp
        }
        Text(text, modifier = if (verticalCentered) Modifier.fillMaxWidth() else Modifier.fillMaxSize(), style = fittedStyle(fitted.value),
            color = color, textAlign = textAlign, softWrap = true, maxLines = maxLines,
            overflow = overflow)
    }
}
