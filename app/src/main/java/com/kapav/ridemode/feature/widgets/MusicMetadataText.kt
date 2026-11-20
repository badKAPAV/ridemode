package com.kapav.ridemode.feature.widgets

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.kapav.ridemode.core.ui.theme.RideUiTokens

/** Typography follows the widget's line height; string length only determines scrolling. */
@Composable
internal fun MusicMetadataText(
    text: String,
    style: TextStyle,
    color: Color,
    maxSize: TextUnit,
    playing: Boolean,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier, contentAlignment = Alignment.CenterStart) {
        val density = LocalDensity.current
        val lineHeightRatio = style.lineHeight.value / style.fontSize.value
        val fontSize = minOf(maxSize.value,
            with(density) { maxHeight.toSp().value } / lineHeightRatio).sp
        val sizedStyle = style.copy(fontSize = fontSize,
            lineHeight = (fontSize.value * lineHeightRatio).sp)
        val measurer = rememberTextMeasurer()
        val overflows = measurer.measure(text, style = sizedStyle, softWrap = false,
            maxLines = 1).size.width > with(density) { maxWidth.roundToPx() }
        val fadeWidth = minOf(RideUiTokens.musicMarqueeFadeWidth, maxWidth / 4)

        // Replacing metadata restarts the hold even when old and new text have equal widths.
        key(text) {
            Text(text, style = sizedStyle, color = color, maxLines = 1, softWrap = false,
                modifier = if (!overflows) Modifier else Modifier
                    .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                    .drawWithContent {
                        drawContent()
                        val edge = fadeWidth.toPx()
                        drawRect(Brush.horizontalGradient(
                            0f to Color.Transparent,
                            (edge / size.width) to Color.Black,
                            (1f - edge / size.width) to Color.Black,
                            1f to Color.Transparent), blendMode = BlendMode.DstIn)
                    }
                    .basicMarquee(iterations = if (playing) Int.MAX_VALUE else 0,
                        initialDelayMillis = RideUiTokens.musicMarqueePauseMillis,
                        repeatDelayMillis = RideUiTokens.musicMarqueePauseMillis)
                    // Keep the first/last glyphs outside the fade while resting at the origin.
                    .padding(horizontal = fadeWidth))
        }
    }
}
