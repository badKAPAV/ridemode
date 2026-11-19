package com.kapav.ridemode.feature.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Fill
import com.adamglin.phosphoricons.fill.Crosshair
import com.adamglin.phosphoricons.fill.MagnifyingGlass
import com.adamglin.phosphoricons.Regular
import com.adamglin.phosphoricons.regular.X
import com.google.android.gms.maps.GoogleMap
import com.google.android.libraries.navigation.NavigationView
import com.kapav.ridemode.core.ui.theme.RideUiTokens
import java.util.Date
import java.util.Locale
import android.text.format.DateFormat

@Composable
fun GoogleMapsWidget(
    controller: GoogleMapsController,
    onRequestPreciseLocation: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by controller.state.collectAsState()
    val context = LocalContext.current
    val density = LocalDensity.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val navigationView = remember(controller) { NavigationView(context) }
    DisposableEffect(navigationView, lifecycle, controller) {
        var started = false
        var resumed = false
        var released = false
        var sdkMap: GoogleMap? = null
        navigationView.setEtaCardEnabled(false)
        navigationView.onCreate(null)
        navigationView.getMapAsync { map ->
            if (!released) { sdkMap = map; controller.attachMap(map) }
        }
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> if (!started) { navigationView.onStart(); started = true }
                Lifecycle.Event.ON_RESUME -> if (!resumed) { navigationView.onResume(); resumed = true }
                Lifecycle.Event.ON_PAUSE -> if (resumed) { navigationView.onPause(); resumed = false }
                Lifecycle.Event.ON_STOP -> if (started) { navigationView.onStop(); started = false }
                else -> Unit
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            released = true
            lifecycle.removeObserver(observer)
            sdkMap?.let(controller::detachMap)
            if (resumed) navigationView.onPause()
            if (started) navigationView.onStop()
            navigationView.onDestroy()
        }
    }
    LaunchedEffect(navigationView, state.expanded, state.stage) {
        navigationView.setHeaderEnabled(state.expanded)
        navigationView.setRecenterButtonEnabled(state.expanded)
        if (state.stage == MapStage.NAVIGATING) {
            withFrameNanos { }
            withFrameNanos { }
            controller.followNavigation()
        }
    }
    Surface(modifier = modifier, shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainer) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val resultsHeight = minOf(RideUiTokens.mapResultsMaxHeight,
                (maxHeight - RideUiTokens.mapSearchChromeHeight).coerceAtLeast(RideUiTokens.minimumTouchTarget))
            AndroidView(factory = { navigationView }, modifier = Modifier.fillMaxSize())
            if (!state.hasPreciseLocation) {
                Surface(Modifier.align(Alignment.Center).padding(RideUiTokens.widgetPadding),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    shape = MaterialTheme.shapes.large) {
                    Column(Modifier.padding(RideUiTokens.panelContentPadding),
                        verticalArrangement = Arrangement.spacedBy(RideUiTokens.contentGap)) {
                        Text("Your location", style = MaterialTheme.typography.titleMedium)
                        Text("Allow precise location to show your position and navigate.",
                            style = MaterialTheme.typography.bodyMedium)
                        Button(onClick = onRequestPreciseLocation) { Text("Allow location") }
                    }
                }
            } else when (state.stage) {
                MapStage.IDLE -> {
                    Box(Modifier.align(Alignment.TopStart).padding(RideUiTokens.widgetPadding)) {
                        val label = state.message ?: "YOUR LOCATION"
                        val textStyle = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        Text(label, modifier = Modifier.clearAndSetSemantics {},
                            style = textStyle.copy(drawStyle = Stroke(
                                width = with(density) { RideUiTokens.mapStatusOutlineWidth.toPx() })),
                            color = Color.White)
                        Text(label, style = textStyle, color = Color.Black)
                    }
                    Row(Modifier.align(Alignment.BottomEnd).padding(RideUiTokens.widgetPadding),
                        horizontalArrangement = Arrangement.spacedBy(RideUiTokens.widgetPadding),
                        verticalAlignment = Alignment.CenterVertically) {
                        MapIconButton("Recenter map", PhosphorIcons.Fill.Crosshair, controller::recenter)
                        MapIconButton("Search for a destination", PhosphorIcons.Fill.MagnifyingGlass,
                            controller::openSearch)
                    }
                }
                MapStage.SEARCH, MapStage.RESOLVING -> {
                    Surface(Modifier.fillMaxWidth().align(Alignment.TopCenter)
                        .padding(RideUiTokens.widgetPadding),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        shape = MaterialTheme.shapes.large) {
                        Column(Modifier.fillMaxWidth().padding(RideUiTokens.contentGap),
                            verticalArrangement = Arrangement.spacedBy(RideUiTokens.widgetPadding)) {
                            OutlinedTextField(value = state.query, onValueChange = controller::editQuery,
                                enabled = state.stage == MapStage.SEARCH,
                                label = { Text("Where to?") }, singleLine = true,
                                modifier = Modifier.fillMaxWidth())
                            if (state.searching) CircularProgressIndicator(Modifier.size(RideUiTokens.iconSize))
                            state.message?.let { Text(it, style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error) }
                            if (state.message != null && state.stage == MapStage.SEARCH &&
                                state.query.trim().length >= 3) {
                                OutlinedButton(onClick = controller::retrySearch) { Text("Retry search") }
                            }
                            Column(Modifier.heightIn(max = resultsHeight)
                                .verticalScroll(rememberScrollState())) {
                                (if (state.stage == MapStage.SEARCH) state.predictions else emptyList())
                                    .forEach { prediction ->
                                    Surface(onClick = { controller.select(prediction) },
                                        modifier = Modifier.fillMaxWidth(),
                                        color = MaterialTheme.colorScheme.surfaceContainerHigh) {
                                        Column(Modifier.padding(RideUiTokens.contentGap)) {
                                            Text(prediction.title, style = MaterialTheme.typography.titleSmall,
                                                maxLines = 1, overflow = TextOverflow.Ellipsis)
                                            if (prediction.subtitle.isNotBlank()) Text(prediction.subtitle,
                                                style = MaterialTheme.typography.bodySmall,
                                                maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        }
                                    }
                                }
                            }
                            Text("Google Maps", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
                MapStage.SELECTED -> {
                    Surface(Modifier.align(Alignment.BottomCenter).fillMaxWidth()
                        .padding(RideUiTokens.widgetPadding),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        shape = MaterialTheme.shapes.large) {
                        Column(Modifier.padding(RideUiTokens.contentGap),
                            verticalArrangement = Arrangement.spacedBy(RideUiTokens.widgetPadding)) {
                            Text("Your location", style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text("↓", style = MaterialTheme.typography.titleSmall)
                            Text(state.destination?.title.orEmpty(), style = MaterialTheme.typography.titleMedium,
                                maxLines = 1, overflow = TextOverflow.Ellipsis)
                            state.message?.let { Text(it, color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall) }
                            Button(onClick = controller::startNavigation,
                                modifier = Modifier.fillMaxWidth()) { Text("Start navigation") }
                        }
                    }
                }
                MapStage.STARTING -> {
                    Surface(Modifier.align(Alignment.BottomCenter).padding(RideUiTokens.widgetPadding),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        shape = MaterialTheme.shapes.large) {
                        Row(Modifier.padding(RideUiTokens.contentGap),
                            horizontalArrangement = Arrangement.spacedBy(RideUiTokens.contentGap),
                            verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(Modifier.size(RideUiTokens.iconSize))
                            Text("Starting navigation", style = MaterialTheme.typography.titleSmall)
                        }
                    }
                }
                MapStage.NAVIGATING -> {
                    NavigationProgressCard(state, compact = !state.expanded,
                        modifier = Modifier.align(if (state.expanded) Alignment.BottomCenter else Alignment.TopStart)
                            .fillMaxWidth(if (state.expanded) 0.46f else 0.78f)
                            .padding(RideUiTokens.widgetPadding))
                    if (state.expanded) {
                        Button(onClick = controller::stopNavigation,
                            modifier = Modifier.align(Alignment.BottomEnd).padding(RideUiTokens.widgetPadding),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                contentColor = MaterialTheme.colorScheme.onSurface)) {
                            Icon(PhosphorIcons.Regular.X, contentDescription = null)
                            Text("End navigation")
                        }
                    } else {
                        Surface(onClick = controller::stopNavigation,
                            modifier = Modifier.align(Alignment.BottomEnd).padding(RideUiTokens.widgetPadding),
                            shape = CircleShape, color = MaterialTheme.colorScheme.surfaceContainerHigh) {
                            Box(Modifier.size(RideUiTokens.minimumTouchTarget), contentAlignment = Alignment.Center) {
                                Icon(PhosphorIcons.Regular.X, contentDescription = "End navigation",
                                    tint = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NavigationProgressCard(state: GoogleMapsState, compact: Boolean, modifier: Modifier) {
    val context = LocalContext.current
    val progress = state.routeProgress
    val distance = progress?.let {
        if (it.remainingMeters < 1_000) "${it.remainingMeters} m"
        else String.format(Locale.getDefault(), "%.1f km", it.remainingMeters / 1_000.0)
    }
    val duration = progress?.let {
        val minutes = (it.remainingSeconds + 59) / 60
        when {
            it.remainingSeconds == 0 -> "Arriving"
            minutes < 60 -> "$minutes min"
            else -> "${minutes / 60} h ${minutes % 60} min"
        }
    }
    val eta = progress?.let { DateFormat.getTimeFormat(context).format(Date(it.arrivalEpochMillis)) }
    Surface(modifier, shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerHigh) {
        Column(Modifier.padding(RideUiTokens.mapProgressPadding),
            verticalArrangement = Arrangement.spacedBy(RideUiTokens.widgetPadding)) {
            if (!compact) Text("TO ${state.destination?.title.orEmpty()}",
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (progress == null) {
                Text("Calculating arrival…", style = MaterialTheme.typography.titleSmall)
            } else {
                if (compact) {
                    Text(duration.orEmpty(), style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(distance.orEmpty(), style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1, overflow = TextOverflow.Ellipsis)
                } else {
                    Text("$duration  ·  $distance", style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Text("ETA $eta", style = MaterialTheme.typography.bodySmall,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
private fun MapIconButton(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit) {
    Surface(color = MaterialTheme.colorScheme.primary,
        shape = MaterialTheme.shapes.medium) {
        IconButton(onClick = onClick, modifier = Modifier.size(RideUiTokens.minimumTouchTarget)) {
            Icon(icon, contentDescription = label,
                tint = MaterialTheme.colorScheme.onPrimary)
        }
    }
}
