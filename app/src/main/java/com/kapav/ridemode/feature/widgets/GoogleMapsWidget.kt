package com.kapav.ridemode.feature.widgets

import android.R
import com.kapav.ridemode.core.ui.components.RideConfirmation
import com.kapav.ridemode.core.ui.components.FadeRideConfirmation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.adamglin.phosphoricons.bold.SpeakerHigh
import com.adamglin.phosphoricons.bold.SpeakerSlash
import com.adamglin.phosphoricons.bold.Bell
import com.adamglin.phosphoricons.Bold
import com.adamglin.phosphoricons.bold.Crosshair
import com.adamglin.phosphoricons.bold.MagnifyingGlass
import com.adamglin.phosphoricons.bold.MapTrifold
import com.adamglin.phosphoricons.bold.MapPin
import com.adamglin.phosphoricons.bold.NavigationArrow
import com.adamglin.phosphoricons.bold.ArrowRight
import com.adamglin.phosphoricons.bold.ArrowLeft
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.key
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Regular
import com.adamglin.phosphoricons.regular.X
import com.google.android.gms.maps.GoogleMap
import com.google.android.libraries.navigation.NavigationView
import com.kapav.ridemode.core.ui.theme.RideUiTokens
import java.util.Date
import android.text.format.DateFormat
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape

@OptIn(androidx.compose.material3.ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun GoogleMapsWidget(
    controller: GoogleMapsController,
    onRequestPreciseLocation: () -> Unit,
    modifier: Modifier = Modifier,
    reducedMotion: Boolean = false,
    renderMap: Boolean = true
) {
    val state by controller.state.collectAsState()
    key(controller, state.mapRevision) {
        GoogleMapsWidgetContent(controller, onRequestPreciseLocation, modifier, reducedMotion, renderMap)
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun GoogleMapsWidgetContent(controller: GoogleMapsController, onRequestPreciseLocation: () -> Unit,
    modifier: Modifier, reducedMotion: Boolean, renderMap: Boolean) {
    val state by controller.state.collectAsState()
    val context = LocalContext.current
    val density = LocalDensity.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val navigationView = remember(controller) { NavigationView(context) }
    var mapReady by remember(navigationView) { mutableStateOf(false) }
    DisposableEffect(navigationView, lifecycle, controller) {
        var started = false
        var resumed = false
        var released = false
        var sdkMap: GoogleMap? = null
        navigationView.onCreate(controller.takeViewState())
        navigationView.setEtaCardEnabled(false)
        controller.attachNavigationView(navigationView)
        val navigationUiListener = com.google.android.libraries.navigation.OnNavigationUiChangedListener { enabled ->
            controller.onNavigationUiChanged(navigationView, enabled)
        }
        navigationView.addOnNavigationUiChangedListener(navigationUiListener)
        navigationView.getMapAsync { map ->
            if (!released && controller.isCurrentView(navigationView)) {
                sdkMap = map; controller.attachMap(navigationView, map); mapReady = true
            }
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
            if (controller.isCurrentView(navigationView)) controller.saveViewState()
            runCatching { navigationView.removeOnNavigationUiChangedListener(navigationUiListener) }
            sdkMap?.let(controller::detachMap)
            controller.detachNavigationView(navigationView)
            if (resumed) runCatching { navigationView.onPause() }
            if (started) runCatching { navigationView.onStop() }
            runCatching { navigationView.onDestroy() }.onFailure {
                android.util.Log.w("RidemodeMaps", "Map view disposal failed", it)
            }
        }
    }
    LaunchedEffect(navigationView, state.expanded, state.stage, renderMap) {
        if (!renderMap) return@LaunchedEffect
        controller.setMapBottomInset(if (state.stage == MapStage.NAVIGATING)
            with(density) { RideUiTokens.mapProgressHeight.roundToPx() } else 0)
        val navigating = state.stage == MapStage.NAVIGATING
        controller.updatePresentation(navigationView, state.expanded, navigating)
        if (state.stage == MapStage.NAVIGATING) {
            withFrameNanos { }
            withFrameNanos { }
            controller.followNavigation()
        }
    }
    val searching = state.stage == MapStage.SEARCH || state.stage == MapStage.RESOLVING
    val stopConfirmationSeconds = state.stopConfirmationSeconds
    val hideMap = !renderMap || searching || state.stage == MapStage.STOPPING || state.mapUnavailable
    val coveringMap = hideMap || stopConfirmationSeconds != null
    val mapAlpha by androidx.compose.animation.core.animateFloatAsState(
        if (hideMap) 0f else 1f,
        androidx.compose.animation.core.tween(if (reducedMotion) 0 else RideUiTokens.mapContentMillis),
        label = "Map search visibility")
    Surface(modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainer) {
        Box(Modifier.fillMaxSize()) {
            AndroidView(factory = { navigationView }, modifier = Modifier.fillMaxSize().graphicsLayer { alpha = mapAlpha },
                update = {
                    it.visibility = if (renderMap) android.view.View.VISIBLE else android.view.View.INVISIBLE
                    it.importantForAccessibility = if (coveringMap) android.view.View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
                    else android.view.View.IMPORTANT_FOR_ACCESSIBILITY_AUTO })
            if (!mapReady && !coveringMap) Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                MapLoadingIndicator()
            }
            if (!renderMap) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { MapLoadingIndicator() }
            } else if (state.stage == MapStage.STOPPING) {
                Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surfaceContainer) {
                    Column(Modifier.padding(RideUiTokens.contentGap),
                        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(state.message ?: "Ending navigation…", style = MaterialTheme.typography.titleSmall)
                        if (state.message != null) Button(onClick = controller::stopNavigation) { Text("Retry end") }
                        else CircularProgressIndicator(Modifier.size(RideUiTokens.iconSize))
                    }
                }
            } else if (searching) {
                // Search uses window bounds, not the square-cell canvas that contracts for the IME.
                // Keep the NavigationView mounted underneath so browsing/navigation state survives.
                Dialog(onDismissRequest = controller::back,
                    properties = DialogProperties(usePlatformDefaultWidth = false,
                        decorFitsSystemWindows = false, dismissOnClickOutside = false)) {
                    MapSearchView(state, controller, reducedMotion)
                }
            } else if (state.mapUnavailable) {
                Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surfaceContainer) {
                    Column(Modifier.padding(RideUiTokens.contentGap),
                        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Map display unavailable", style = MaterialTheme.typography.titleMedium)
                        Button(onClick = controller::retryMap) { Text("Reload map") }
                        if (state.stage == MapStage.NAVIGATING) androidx.compose.material3.TextButton(onClick = controller::requestStopNavigation) {
                            Text("End navigation")
                        }
                    }
                }
            } else if (!state.hasPreciseLocation) {
                Surface(Modifier.align(Alignment.Center).padding(RideUiTokens.widgetPadding),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh, shape = MaterialTheme.shapes.large) {
                    Column(Modifier.padding(RideUiTokens.contentGap)) {
                        Text("Your location", style = MaterialTheme.typography.titleMedium)
                        Text("Allow precise location to show your position and navigate.", style = MaterialTheme.typography.bodyMedium)
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
                                width = with(density) { RideUiTokens.mapStatusOutlineWidth.toPx() })), color = Color.White)
                        Text(label, style = textStyle, color = Color.Black)
                    }
                    Row(Modifier.align(Alignment.BottomEnd).padding(RideUiTokens.widgetPadding),
                        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)) {
                        ToggleButton(checked = false, onCheckedChange = { controller.recenter() },
                            shapes = ButtonGroupDefaults.connectedLeadingButtonShapes(),
                            modifier = Modifier.size(RideUiTokens.minimumTouchTarget),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)) {
                            Icon(PhosphorIcons.Bold.Crosshair, "Recenter map")
                        }
                        ToggleButton(checked = false, onCheckedChange = { controller.openSearch() },
                            shapes = ButtonGroupDefaults.connectedTrailingButtonShapes(),
                            modifier = Modifier.size(RideUiTokens.minimumTouchTarget),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)) {
                            Icon(PhosphorIcons.Bold.MagnifyingGlass, "Search for a destination")
                        }
                    }
                }
                MapStage.PICKING -> {
                    Icon(PhosphorIcons.Bold.MapPin, "Selected map position", tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.Center).size(RideUiTokens.featureIconSize)
                            .offset(y = -RideUiTokens.iconSize))
                    Surface(Modifier.align(Alignment.TopCenter).padding(RideUiTokens.widgetPadding),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh, shape = MaterialTheme.shapes.large) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = controller::back) { Icon(PhosphorIcons.Bold.ArrowLeft, "Back") }
                            Text("Move map to choose ${if (state.editingField == MapSearchField.FROM) "start" else "destination"}",
                                style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(RideUiTokens.widgetPadding))
                        }
                    }
                    Button(onClick = controller::confirmMapPoint,
                        modifier = Modifier.align(Alignment.BottomCenter).padding(RideUiTokens.contentGap)) {
                        Icon(PhosphorIcons.Bold.MapPin, null)
                        Spacer(modifier = Modifier.width(10.dp))  // Horizontal gap
                        Text("Use this location")
                    }
                    state.message?.let { Text(it, Modifier.align(Alignment.Center).padding(RideUiTokens.contentGap),
                        color = MaterialTheme.colorScheme.error) }
                }
                MapStage.SELECTED -> {
                    Surface(Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(
                            topStart = RideUiTokens.widgetCardCorner, topEnd = RideUiTokens.widgetCardCorner),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh) {
                        Column(Modifier.padding(RideUiTokens.contentGap),
                            verticalArrangement = Arrangement.spacedBy(RideUiTokens.widgetPadding)) {
                            Text("Ready to ride", style = MaterialTheme.typography.titleMedium)
                            Row(verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(RideUiTokens.contentGap)) {
                                TripEndpoint("FROM", "Current location",
                                    PhosphorIcons.Bold.Crosshair, Modifier.weight(1f), enabled = false) {}
                                Icon(PhosphorIcons.Bold.ArrowRight, null, tint = MaterialTheme.colorScheme.primary)
                                TripEndpoint("TO", state.destination?.title.orEmpty(),
                                    PhosphorIcons.Bold.MapPin, Modifier.weight(1f)) { controller.editField(MapSearchField.TO) }
                                Button(onClick = controller::startNavigation) {
                                    Icon(PhosphorIcons.Bold.NavigationArrow, null)
                                    Spacer(Modifier.size(RideUiTokens.widgetPadding))
                                    Text("Start ride", style = MaterialTheme.typography.titleMedium)
                                }
                            }
                            state.message?.let { Text(it, color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall) }
                        }
                    }
                }
                MapStage.STARTING -> {
                    Surface(Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh) {
                        Row(Modifier.padding(RideUiTokens.contentGap),
                            horizontalArrangement = Arrangement.spacedBy(RideUiTokens.contentGap),
                            verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(Modifier.size(RideUiTokens.iconSize))
                            Text("Starting navigation", style = MaterialTheme.typography.titleSmall)
                        }
                    }
                }
                MapStage.NAVIGATING -> {
                    NavigationProgressCard(state, controller::requestStopNavigation, controller::setVoiceMode,
                        Modifier.align(Alignment.BottomCenter).fillMaxWidth())
                    state.message?.let { message ->
                        Surface(Modifier.align(Alignment.Center).padding(RideUiTokens.contentGap),
                            shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.errorContainer) {
                            Text(message, Modifier.padding(RideUiTokens.contentGap), style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
                else -> Unit
            }
            FadeRideConfirmation(state.stopConfirmationDeadline.takeIf { renderMap && stopConfirmationSeconds != null },
                reducedMotion) { deadline, opacity, interactive ->
                StopNavigationConfirmation(deadline, opacity, interactive,
                    controller::confirmStopNavigation, controller::dismissStopConfirmation)
            }
        }
    }
}

@Composable
private fun NavigationProgressCard(state: GoogleMapsState, onStop: () -> Unit,
    onVoiceMode: (MapVoiceMode) -> Unit, modifier: Modifier) {
    val context = LocalContext.current
    val progress = state.routeProgress
    val locale = androidx.core.os.ConfigurationCompat.getLocales(androidx.compose.ui.platform.LocalConfiguration.current)[0]
    val distance = progress?.let {
        if (it.remainingMeters < 1_000) "${it.remainingMeters} m"
        else String.format(locale, "%.1f km", it.remainingMeters / 1_000.0)
    }
    val duration = progress?.let {
        val minutes = (it.remainingSeconds + 59) / 60
        when {
            it.remainingSeconds == 0 -> "Arriving"
            minutes < 60 -> "$minutes min"
            else -> "${minutes / 60}h ${minutes % 60}m"
        }
    }
    val eta = progress?.let { DateFormat.getTimeFormat(context).format(Date(it.arrivalEpochMillis)) }
    Surface(modifier = modifier.padding(horizontal = RideUiTokens.sectionGap), shape = RoundedCornerShape(
        topStart = RideUiTokens.widgetCardCorner,
        topEnd = RideUiTokens.widgetCardCorner,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    ),
        color = MaterialTheme.colorScheme.surfaceContainerHigh) {
        BoxWithConstraints {
            val wide = maxWidth >= RideUiTokens.mapProgressWideWidth
            Row(Modifier.fillMaxWidth().heightIn(min = RideUiTokens.mapProgressHeight),
                verticalAlignment = Alignment.CenterVertically) {
                Row(Modifier.weight(1f).padding(horizontal = RideUiTokens.mapProgressPadding),
                    horizontalArrangement = Arrangement.spacedBy(RideUiTokens.widgetPadding),
                    verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = MaterialTheme.shapes.small) {
                        Text(duration ?: "…", style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Bold,
                            maxLines = 1, modifier = Modifier.padding(RideUiTokens.widgetPadding))
                    }
                    val details = androidx.compose.ui.text.buildAnnotatedString {
                        append(distance.orEmpty())
                        if (eta != null) {
                            append(if (wide) "  ·  Arrive " else " · ")
                            pushStyle(androidx.compose.ui.text.SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Normal))
                            append(eta)
                            pop()
                        }
                    }
                    if (wide) Text(details, style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                    else AdaptiveWidgetText(details.text, MaterialTheme.typography.labelSmall,
                        MaterialTheme.colorScheme.onSurfaceVariant,
                        Modifier.weight(1f).height(RideUiTokens.iconSize), maxLines = 1, verticalCentered = true)
                }
                if (state.expanded) VoiceModeControl(state.voiceMode, onVoiceMode)
                Surface(onClick = onStop, color = MaterialTheme.colorScheme.errorContainer,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(topStart = RideUiTokens.widgetCardCorner)) {
                    Box(Modifier.size(RideUiTokens.mapProgressHeight), contentAlignment = Alignment.Center) {
                        Icon(PhosphorIcons.Regular.X, "End navigation", tint = MaterialTheme.colorScheme.onErrorContainer)
                    }
                }
            }
        }
    }
}

@Composable
private fun VoiceModeControl(mode: MapVoiceMode, onVoiceMode: (MapVoiceMode) -> Unit) {
    var open by remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { open = true }, modifier = Modifier.size(RideUiTokens.minimumTouchTarget)) {
            Icon(when (mode) {
                MapVoiceMode.MUTE -> PhosphorIcons.Bold.SpeakerSlash
                MapVoiceMode.ALL -> PhosphorIcons.Bold.SpeakerHigh
                MapVoiceMode.ALERTS -> PhosphorIcons.Bold.Bell
            }, "Navigation voice: ${mode.label}")
        }
        DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
            MapVoiceMode.entries.forEach { choice ->
                DropdownMenuItem(text = { Text(choice.label) },
                    leadingIcon = { Icon(when (choice) {
                        MapVoiceMode.MUTE -> PhosphorIcons.Bold.SpeakerSlash
                        MapVoiceMode.ALL -> PhosphorIcons.Bold.SpeakerHigh
                        MapVoiceMode.ALERTS -> PhosphorIcons.Bold.Bell
                    }, null) },
                    trailingIcon = { if (choice == mode) Text("Selected", style = MaterialTheme.typography.labelSmall) },
                    onClick = { onVoiceMode(choice); open = false })
            }
        }
    }
}

@Composable
private fun StopNavigationConfirmation(deadline: Long, opacity: Float, interactive: Boolean, onConfirm: () -> Unit, onKeep: () -> Unit) {
    Surface(Modifier.fillMaxSize().graphicsLayer { alpha = opacity }
        .then(if (interactive) Modifier else Modifier.clearAndSetSemantics { }), color = RideUiTokens.confirmationScrimColor,
        contentColor = RideUiTokens.confirmationContentColor) {
        Box(Modifier.fillMaxSize().padding(RideUiTokens.widgetPadding), contentAlignment = Alignment.Center) {
            RideConfirmation("End navigation?", "", deadline, "End", PhosphorIcons.Bold.NavigationArrow,
                onDismiss = { if (interactive) onKeep() }, onConfirm = onConfirm,
                dismissLabel = "Keep", destructive = true, enabled = interactive,
                modifier = Modifier.widthIn(max = RideUiTokens.confirmationMaxWidth))
        }
    }
}

@Composable
private fun TripEndpoint(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier, enabled: Boolean = true, onClick: () -> Unit) {
    Surface(onClick = onClick, enabled = enabled, modifier = modifier, shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainer) {
        Row(Modifier.padding(RideUiTokens.contentGap).heightIn(min = RideUiTokens.minimumTouchTarget),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(RideUiTokens.widgetPadding)) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
            Column(Modifier.weight(1f)) {
                Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(value, style = MaterialTheme.typography.titleSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
private fun MapSearchView(state: GoogleMapsState, controller: GoogleMapsController, reducedMotion: Boolean) {
    val focusRequester = remember { androidx.compose.ui.focus.FocusRequester() }
    val keyboard = androidx.compose.ui.platform.LocalSoftwareKeyboardController.current
    val focusManager = androidx.compose.ui.platform.LocalFocusManager.current
    // Hiding the IME only changes available space; keep the query/results until Back or selection.
    androidx.compose.runtime.DisposableEffect(Unit) {
        onDispose { focusManager.clearFocus(); keyboard?.hide() }
    }
    LaunchedEffect(state.editingField) {
        if (state.editingField != null) {
            withFrameNanos { }
            focusRequester.requestFocus()
            keyboard?.show()
        } else {
            focusManager.clearFocus()
            keyboard?.hide()
        }
    }
    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surfaceContainerLow) {
        // safeDrawing includes the IME, system bars and cutouts; consume these insets once.
        Column(Modifier.fillMaxSize().safeDrawingPadding().padding(RideUiTokens.contentGap),
            verticalArrangement = Arrangement.spacedBy(RideUiTokens.widgetPadding)) {
            if (state.editingField == null) Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(RideUiTokens.widgetPadding)) {
                IconButton(onClick = controller::back) { Icon(PhosphorIcons.Bold.ArrowLeft, "Back") }
                Text(if (state.editingField == null) "Plan your ride" else
                    if (state.editingField == MapSearchField.FROM) "Current location" else "Destination",
                    style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                if (state.editingField == null) Button(onClick = controller::confirmTrip,
                    enabled = state.destination != null) { Text("Continue") }
            }
            val editing = state.editingField
            val fromWeight by androidx.compose.animation.core.animateFloatAsState(
                if (editing == MapSearchField.TO) 0f else 1f,
                androidx.compose.animation.core.tween(if (reducedMotion) 0 else RideUiTokens.mapContentMillis), label = "From width")
            val toWeight by androidx.compose.animation.core.animateFloatAsState(
                if (editing == MapSearchField.FROM) 0f else 1f,
                androidx.compose.animation.core.tween(if (reducedMotion) 0 else RideUiTokens.mapContentMillis), label = "To width")
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(RideUiTokens.widgetPadding * fromWeight)) {
                MapSearchField.entries.forEach { field ->
                    androidx.compose.runtime.key(field) {
                    val weight = if (field == MapSearchField.FROM) fromWeight else toWeight
                    if (weight > 0f) {
                        if (editing == field) {
                            TextField(value = state.query, onValueChange = controller::editQuery,
                                enabled = state.stage != MapStage.RESOLVING,
                                singleLine = true, placeholder = { Text("Search places") },
                                leadingIcon = { IconButton(onClick = controller::back) { Icon(PhosphorIcons.Bold.ArrowLeft, "Back to trip") } },
                                trailingIcon = { IconButton(onClick = { controller.editQuery("") }) {
                                    Icon(PhosphorIcons.Regular.X, "Clear search") } },
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(RideUiTokens.mapSearchFieldCorner),
                                colors = TextFieldDefaults.colors(focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent, disabledIndicatorColor = Color.Transparent),
                                modifier = Modifier.weight(weight).focusRequester(focusRequester))
                        } else TripEndpoint(if (field == MapSearchField.FROM) "FROM" else "TO",
                            if (field == MapSearchField.FROM) "Current location"
                            else state.destination?.title ?: "Choose destination",
                            if (field == MapSearchField.FROM) PhosphorIcons.Bold.Crosshair else PhosphorIcons.Bold.MapPin,
                            Modifier.weight(weight), enabled = field == MapSearchField.TO) { controller.editField(field) }
                    }
                    }
                }
            }
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(RideUiTokens.widgetPadding)) {
                if (state.searching) LinearProgressIndicator(Modifier.fillMaxWidth())
                state.message?.let {
                    Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
                    if (state.stage == MapStage.SEARCH && state.query.trim().length >= 3)
                        OutlinedButton(onClick = controller::retrySearch) { Text("Try again") }
                }
                if (state.stage == MapStage.SEARCH) state.predictions.forEach { prediction ->
                    TripEndpoint(prediction.subtitle, prediction.title, PhosphorIcons.Bold.MapPin,
                        Modifier.fillMaxWidth()) { controller.select(prediction) }
                }
                if (editing != null && state.stage == MapStage.SEARCH) {
                    TripEndpoint("DROP A PIN", "Choose from map", PhosphorIcons.Bold.MapTrifold,
                        Modifier.fillMaxWidth()) { controller.chooseFromMap() }
                }
                if (editing == null) Text("Choose your destination, then review your ride.",
                    style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                else if (state.query.trim().length < 3) Text("Enter at least 3 characters to find a place",
                    style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            // Text("Google Maps", style = MaterialTheme.typography.labelSmall,
            //     color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun MapLoadingIndicator() {
    androidx.compose.material3.CircularWavyProgressIndicator(
        modifier = Modifier.semantics { contentDescription = "Loading map" })
}
