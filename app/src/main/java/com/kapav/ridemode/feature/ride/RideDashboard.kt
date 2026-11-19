package com.kapav.ridemode.feature.ride

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.core.tween
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kapav.ridemode.core.database.PageWithWidgets
import com.kapav.ridemode.core.ui.components.RideActionButton
import com.kapav.ridemode.core.ui.theme.RideUiTokens
import com.kapav.ridemode.core.ui.theme.RideThinTitleFontFamily
import com.kapav.ridemode.data.RidePreferences
import com.kapav.ridemode.data.MusicAction
import com.kapav.ridemode.data.MusicSessionState
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Fill
import com.adamglin.phosphoricons.fill.MapPin
import com.adamglin.phosphoricons.fill.Microphone
import com.adamglin.phosphoricons.fill.GridFour
import com.kapav.ridemode.feature.widgets.GoogleMapsController
import com.adamglin.phosphoricons.Regular
import com.adamglin.phosphoricons.regular.X
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RideDashboard(
    pages: List<PageWithWidgets>,
    preferences: RidePreferences,
    music: MusicSessionState,
    onMusicAction: (MusicAction) -> Unit,
    volume: com.kapav.ridemode.data.VolumeState,
    onVolumeChange: (Float) -> Boolean,
    weather: com.kapav.ridemode.data.WeatherState,
    notifications: com.kapav.ridemode.data.NotificationFeedState,
    calls: com.kapav.ridemode.data.CallHistoryState,
    guidance: com.kapav.ridemode.data.TurnGuidanceState,
    maxRows: Int,
    maxColumns: Int,
    inPip: Boolean = false,
    resumeCounter: Int,
    mapControllerFor: (String) -> GoogleMapsController,
    onRequestMapLocation: () -> Unit,
    onSaverChange: (Boolean) -> Unit,
    onEndRide: () -> Unit
) {
    val enabledPages = remember(pages) { pages.filter { it.page.enabled } }
    val pager = rememberPagerState(pageCount = { enabledPages.size.coerceAtLeast(1) })
    val pagerScope = rememberCoroutineScope()
    val currentMapWidget = enabledPages.getOrNull(pager.currentPage)?.widgets
        ?.firstOrNull { it.visible && it.typeId == "google_maps" }
    val currentMapController = remember(currentMapWidget?.id) {
        currentMapWidget?.id?.let(mapControllerFor)
    }
    val currentMapState = currentMapController?.state?.collectAsState()?.value
    val mapExpanded = currentMapState?.expanded == true
    BackHandler(mapExpanded) { currentMapController?.collapse() }
    var saver by remember { mutableStateOf(false) }
    var interactionCount by remember { mutableIntStateOf(0) }
    var assistantOpen by remember { mutableStateOf(false) }
    var assistantStep by remember { mutableStateOf(0) }
    LaunchedEffect(resumeCounter) { saver = false; interactionCount++ }
    LaunchedEffect(preferences.saverEnabled, preferences.saverDelaySeconds, interactionCount, inPip, resumeCounter) {
        if (preferences.saverEnabled && !inPip) {
            delay(preferences.saverDelaySeconds * 1000L)
            saver = true
        } else saver = false
    }
    LaunchedEffect(saver) { onSaverChange(saver) }
    Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
        if (inPip) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Ride in progress", style = MaterialTheme.typography.titleLarge)
            }
            return@Surface
        }
        Box(Modifier.fillMaxSize().pointerInput(Unit) {
            awaitPointerEventScope {
                while (true) {
                    val event = awaitPointerEvent(PointerEventPass.Initial)
                    if (event.changes.any { it.pressed && !it.previousPressed } && !saver) {
                        interactionCount++
                    }
                }
            }
        }) {
            Column(
                modifier = Modifier.fillMaxSize().padding(
                    horizontal = RideUiTokens.rideSidePadding,
                    vertical = RideUiTokens.rideScreenPadding),
                verticalArrangement = Arrangement.spacedBy(RideUiTokens.rideRailGap)
            ) {
                HorizontalPager(state = pager, userScrollEnabled = !mapExpanded,
                    modifier = Modifier.fillMaxWidth().weight(1f)) { index ->
                    val page = enabledPages.getOrNull(index)
                    if (page != null) {
                        FixedRideCanvas(page.widgets, maxRows, maxColumns, music, onMusicAction, volume, onVolumeChange,
                            weather, notifications, preferences.privacyMode, calls, guidance,
                            mapsController = if (index == pager.currentPage) currentMapController else null,
                            onRequestMapLocation = onRequestMapLocation)
                    } else {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No enabled page")
                        }
                    }
                }
                Row(Modifier.fillMaxWidth().height(RideUiTokens.rideRailHeight)
                    .background(MaterialTheme.colorScheme.surfaceContainerLow,
                        RoundedCornerShape(RideUiTokens.panelCorner)),
                    horizontalArrangement = Arrangement.spacedBy(RideUiTokens.rideRailGap),
                    verticalAlignment = Alignment.CenterVertically) {
                    RideEndButton(onClick = onEndRide)
                    if (mapExpanded) RideIconRailButton(PhosphorIcons.Fill.GridFour, null,
                        contentDescription = "Restore widget grid",
                        onClick = { currentMapController?.collapse() })
                    AnimatedContent(
                        targetState = pager.currentPage,
                        modifier = Modifier.weight(1f),
                        transitionSpec = {
                            val duration = if (preferences.reducedMotion) 0 else RideUiTokens.pageIndicatorTransitionMillis
                            val forward = targetState > initialState
                            slideInHorizontally(tween(duration)) { if (forward) it / 3 else -it / 3 } togetherWith
                                slideOutHorizontally(tween(duration)) { if (forward) -it / 3 else it / 3 }
                        },
                        label = "Ride page indicator"
                    ) { index ->
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            PageIndicatorLabel(enabledPages.getOrNull(index - 1)?.page?.name, false,
                                enabled = !mapExpanded,
                                modifier = Modifier.weight(1f)) { pagerScope.launch { pager.animateScrollToPage(index - 1) } }
                            PageIndicatorLabel(enabledPages.getOrNull(index)?.page?.name, true,
                                enabled = !mapExpanded,
                                modifier = Modifier.weight(1.4f)) { pagerScope.launch { pager.animateScrollToPage(index) } }
                            PageIndicatorLabel(enabledPages.getOrNull(index + 1)?.page?.name, false,
                                enabled = !mapExpanded,
                                modifier = Modifier.weight(1f)) { pagerScope.launch { pager.animateScrollToPage(index + 1) } }
                        }
                    }
                    RideIconRailButton(PhosphorIcons.Fill.MapPin, "Open", onClick = {}, enabled = false)
                    RideIconRailButton(PhosphorIcons.Fill.Microphone, null,
                        contentDescription = "Assistant preview",
                        secondary = true,
                        onClick = { assistantOpen = true; assistantStep = 0 })
                }
            }
            AnimatedVisibility(visible = saver,
                enter = fadeIn(tween(if (preferences.reducedMotion) 0 else RideUiTokens.oledTransitionMillis)),
                exit = fadeOut(tween(if (preferences.reducedMotion) 0 else RideUiTokens.oledTransitionMillis))) {
                Box(Modifier.fillMaxSize().background(Color.Black).pointerInput(Unit) {
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        down.consume()
                        saver = false
                        interactionCount++
                        do {
                            val event = awaitPointerEvent()
                            event.changes.forEach { it.consume() }
                        } while (event.changes.any { it.pressed })
                    }
                }) {
                    OledSaverContent(preferences, music, weather)
                }
            }
        }
    }
    if (assistantOpen && !saver) {
        val states = listOf("Idle", "Pressed (mock)", "Listening (mock)", "Transcript: show my pages (mock)",
            "Result: no action executed (mock)", "Error: voice control is not available yet")
        AlertDialog(
            onDismissRequest = { assistantOpen = false },
            title = { Text("Assistant preview") },
            text = { Text(states[assistantStep]) },
            confirmButton = {
                RideActionButton(
                    label = if (assistantStep < states.lastIndex) "Next state" else "Start over",
                    onClick = {
                        assistantStep = (assistantStep + 1) % states.size
                    }
                )
            },
            dismissButton = {
                RideActionButton(
                    label = "Close",
                    onClick = { assistantOpen = false }
                )
            }
        )
    }
}

@Composable
private fun PageIndicatorLabel(name: String?, current: Boolean, enabled: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Text(name.orEmpty(), modifier = modifier.clickable(enabled = name != null && enabled, onClick = onClick)
        .semantics { contentDescription = if (current) "Current page ${name.orEmpty()}" else "Go to page ${name.orEmpty()}" },
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = if (current)
            RideUiTokens.pageIndicatorCurrentOpacity else RideUiTokens.pageIndicatorAdjacentOpacity),
        style = if (current) MaterialTheme.typography.titleMedium else
            MaterialTheme.typography.titleMedium.copy(fontFamily = RideThinTitleFontFamily,
                fontWeight = FontWeight.Light),
        textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis)
}

@Composable
private fun RideIconRailButton(
    icon: ImageVector, label: String?, onClick: () -> Unit,
    enabled: Boolean = true, secondary: Boolean = false, contentDescription: String = label ?: ""
) {
    Button(onClick = onClick, enabled = enabled,
        modifier = Modifier.height(RideUiTokens.rideRailHeight)
            .then(if (label == null) Modifier.size(RideUiTokens.rideRailHeight) else Modifier)
            .semantics { this.contentDescription = contentDescription },
        shape = RoundedCornerShape(RideUiTokens.rideButtonCorner),
        colors = if (secondary) ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary
        ) else ButtonDefaults.buttonColors(),
        contentPadding = PaddingValues(horizontal = RideUiTokens.contentGap, vertical = 0.dp)) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(RideUiTokens.iconSize))
        if (label != null) {
            Spacer(Modifier.size(RideUiTokens.widgetPadding))
            Text(label, style = MaterialTheme.typography.titleMedium, maxLines = 1)
        }
    }
}

@Composable
private fun RideEndButton(onClick: () -> Unit) {
    OutlinedButton(onClick = onClick,
        modifier = Modifier.height(RideUiTokens.rideRailHeight)
            .semantics { contentDescription = "End ride" },
        shape = RoundedCornerShape(RideUiTokens.rideButtonCorner),
        contentPadding = PaddingValues(horizontal = RideUiTokens.widgetPadding, vertical = 0.dp)) {
        Icon(PhosphorIcons.Regular.X, contentDescription = null,
            modifier = Modifier.size(RideUiTokens.iconSize))
        Spacer(Modifier.size(RideUiTokens.widgetPadding))
        Text("End\nride", style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Center, maxLines = 2, lineHeight = 18.sp)
    }
}

@Composable
fun RideRailButton(label: String, onClick: () -> Unit, enabled: Boolean = true) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.height(RideUiTokens.rideRailHeight),
        shape = RoundedCornerShape(RideUiTokens.rideButtonCorner),
        contentPadding = PaddingValues(horizontal = RideUiTokens.rideScreenPadding, vertical = 0.dp)
    ) {
        Text(label, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            maxLines = 1)
    }
}
