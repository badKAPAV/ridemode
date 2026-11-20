package com.kapav.ridemode.feature.ride

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Fill
import com.adamglin.phosphoricons.fill.Warning
import com.kapav.ridemode.R
import com.kapav.ridemode.core.ui.theme.RideUiTokens
import kotlinx.coroutines.delay

/** Fixed RIDE, fading HARD → SAFE, then the opaque intro lifts off the live dashboard. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun RideStartupAnimation(onFinished: () -> Unit) {
    val finished by rememberUpdatedState(onFinished)
    val wordAlpha = remember { Animatable(1f) }
    val reveal = remember { Animatable(0f) }
    var safe by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(RideUiTokens.rideIntroHardHoldMillis.toLong())
        wordAlpha.animateTo(0f, tween(RideUiTokens.rideIntroFadeOutMillis))
        safe = true
        wordAlpha.animateTo(1f, tween(RideUiTokens.rideIntroFadeInMillis))
        delay(RideUiTokens.rideIntroSafeHoldMillis.toLong())
        reveal.animateTo(1f, tween(RideUiTokens.rideIntroRevealMillis, easing = RideUiTokens.rideIntroRevealEasing))
        finished()
    }
    // Reserve system-bar space even while immersive mode hides the bars. Live safeDrawing
    // padding can move the bottom reminder when bar visibility changes during startup.
    val introInsets = WindowInsets.systemBarsIgnoringVisibility.union(WindowInsets.displayCutout)
    Box(Modifier.fillMaxSize().pointerInput(Unit) {
        // Keep the dashboard protected from accidental touches until the reveal completes.
        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false)
            down.consume()
            do {
                val event = awaitPointerEvent()
                event.changes.forEach { it.consume() }
            } while (event.changes.any { it.pressed })
        }
    }) {
        BoxWithConstraints(Modifier.fillMaxSize().graphicsLayer { translationY = -size.height * reveal.value }
            .background(MaterialTheme.colorScheme.background).windowInsetsPadding(introInsets)
            .padding(horizontal = RideUiTokens.screenPadding)) {
            val ride = stringResource(R.string.ride_intro_ride)
            val hard = stringResource(R.string.ride_intro_hard)
            val safeWord = stringResource(R.string.ride_intro_safe)
            val helmet = stringResource(R.string.ride_intro_helmet)
            val titleStyle = MaterialTheme.typography.titleLarge.copy(fontSize = 70.sp)
            val adviceStyle = MaterialTheme.typography.bodyMedium
            val measurer = rememberTextMeasurer()
            val density = LocalDensity.current
            val wordWidth = with(density) {
                maxOf(measurer.measure(hard, titleStyle).size.width,
                    measurer.measure(safeWord, titleStyle).size.width).toDp()
            }
            Row(Modifier.align(Alignment.Center).clearAndSetSemantics {
                liveRegion = LiveRegionMode.Polite
                contentDescription = "$ride ${if (safe) safeWord else hard}"
            }, verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(RideUiTokens.widgetPadding)) {
                Text(ride, style = titleStyle, color = MaterialTheme.colorScheme.primary)
                // Reserve the larger word's width so RIDE never shifts during the fade.
                Box(Modifier.width(wordWidth), contentAlignment = Alignment.CenterStart) {
                    Text(if (safe) safeWord else hard, style = titleStyle,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.graphicsLayer { alpha = wordAlpha.value })
                }
            }
            val adviceWidthPx = remember(helmet, adviceStyle, density, measurer) {
                measurer.measure(helmet, adviceStyle, maxLines = 1, softWrap = false).size.width
            }
            val availableAdvicePx = with(density) {
                (maxWidth - RideUiTokens.iconSize - RideUiTokens.widgetPadding).toPx()
            }.coerceAtLeast(1f)
            val adviceScale = minOf(1f, availableAdvicePx / adviceWidthPx.coerceAtLeast(1))
            val adviceTextWidth = with(density) {
                minOf(adviceWidthPx.toFloat(), availableAdvicePx).toDp()
            }
            Row(Modifier.align(Alignment.BottomCenter).padding(bottom = RideUiTokens.sectionGap),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(RideUiTokens.widgetPadding)) {
                Icon(PhosphorIcons.Fill.Warning, contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(RideUiTokens.iconSize))
                Text(helmet, modifier = Modifier.width(adviceTextWidth),
                    style = adviceStyle.copy(fontSize = adviceStyle.fontSize * adviceScale),
                    color = MaterialTheme.colorScheme.onBackground, maxLines = 1, softWrap = false)
            }
        }
    }
}
