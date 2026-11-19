package com.kapav.ridemode.feature.ride

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.sp
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Fill
import com.adamglin.phosphoricons.fill.Motorcycle
import com.kapav.ridemode.R
import com.kapav.ridemode.core.ui.theme.RideUiTokens
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

/** Short, one-shot overlay. The Ride dashboard is already present beneath it. */
@Composable
internal fun RideStartupAnimation(onFinished: () -> Unit) {
    val announcement = stringResource(R.string.ride_intro_accessibility)
    var phase by remember { mutableIntStateOf(0) }
    val travel = remember { Animatable(-RideUiTokens.rideIntroBikeTravelFactor) }
    LaunchedEffect(Unit) {
        delay(RideUiTokens.rideIntroHardHoldMillis.toLong())
        phase = 1
        delay(RideUiTokens.rideIntroTextTransitionMillis.toLong() +
            RideUiTokens.rideIntroSafeHoldMillis)
        phase = 2
        travel.animateTo(
            RideUiTokens.rideIntroBikeTravelFactor,
            animationSpec = tween(
                durationMillis = RideUiTokens.rideIntroBikeSweepMillis,
                easing = RideUiTokens.rideIntroBikeEasing
            )
        )
        onFinished()
    }
    BoxWithConstraints(
        Modifier.fillMaxSize()
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    down.consume()
                    do {
                        val event = awaitPointerEvent()
                        event.changes.forEach { it.consume() }
                    } while (event.changes.any { it.pressed })
                }
            }
            .semantics {
                liveRegion = LiveRegionMode.Polite
                contentDescription = announcement
            }
    ) {
        val viewportWidth = maxWidth
        val revealEdge = RideUiTokens.rideIntroRevealEdgeFactor
        val reveal = if (phase == 2)
            ((travel.value + revealEdge) / (revealEdge * 2f)).coerceIn(0f, 1f)
        else 0f
        Box(Modifier.fillMaxSize().background(
            MaterialTheme.colorScheme.background.copy(alpha = 1f - reveal)))
        AnimatedVisibility(
            visible = phase != 2,
            modifier = Modifier.align(Alignment.Center),
            exit = fadeOut(tween(RideUiTokens.rideIntroTextTransitionMillis))
        ) {
            Column(
                modifier = Modifier.padding(horizontal = RideUiTokens.rideSidePadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(RideUiTokens.contentGap)
            ) {
                AnimatedContent(
                    targetState = phase.coerceAtMost(1),
                    transitionSpec = {
                        (slideInVertically(tween(RideUiTokens.rideIntroTextTransitionMillis)) { it / 3 } +
                            fadeIn(tween(RideUiTokens.rideIntroTextTransitionMillis))) togetherWith
                            (slideOutVertically(tween(RideUiTokens.rideIntroTextTransitionMillis)) { -it / 3 } +
                                fadeOut(tween(RideUiTokens.rideIntroTextTransitionMillis)))
                    },
                    label = "Ride hard to ride safe"
                ) { current ->
                    Text(
                        if (current == 0) stringResource(R.string.ride_intro_hard)
                            else stringResource(R.string.ride_intro_safe),
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = (viewportWidth.value * RideUiTokens.rideIntroTitleWidthFraction)
                                .coerceIn(RideUiTokens.rideIntroTitleMinSp,
                                    RideUiTokens.rideIntroTitleMaxSp).sp
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        maxLines = 2
                    )
                }
                Text(
                    stringResource(R.string.ride_intro_helmet),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
            }
        }
        if (phase == 2) {
            Icon(
                PhosphorIcons.Fill.Motorcycle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.Center)
                    .offset {
                        IntOffset((travel.value * constraints.maxWidth).roundToInt(), 0)
                    }
                    .requiredSize(viewportWidth * RideUiTokens.rideIntroBikeSizeFactor)
            )
        }
    }
}
