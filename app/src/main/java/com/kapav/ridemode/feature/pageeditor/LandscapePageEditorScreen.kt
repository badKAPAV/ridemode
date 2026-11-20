package com.kapav.ridemode.feature.pageeditor

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.zIndex
import com.kapav.ridemode.core.database.PageWithWidgets
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Fill
import com.adamglin.phosphoricons.fill.ArrowLeft
import com.adamglin.phosphoricons.fill.Copy
import com.adamglin.phosphoricons.fill.Eye
import com.adamglin.phosphoricons.fill.Gear
import com.adamglin.phosphoricons.fill.List
import com.adamglin.phosphoricons.fill.Pencil
import com.adamglin.phosphoricons.fill.Trash
import com.adamglin.phosphoricons.Regular
import com.adamglin.phosphoricons.regular.Plus
import com.kapav.ridemode.core.model.WidgetRegistry
import com.kapav.ridemode.core.model.RideGridConstraints
import com.kapav.ridemode.core.ui.theme.RideUiTokens
import com.kapav.ridemode.data.rect
import com.kapav.ridemode.feature.ride.FixedRideCanvas
import com.kapav.ridemode.feature.ride.RideRailButton
import com.kapav.ridemode.feature.ride.RideViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandscapePageEditorScreen(viewModel: RideViewModel, onBack: () -> Unit) {
    val pages by viewModel.pages.collectAsState()
    val preferences by viewModel.preferences.collectAsState()
    val music by viewModel.music.collectAsState()
    val volume by viewModel.volume.collectAsState()
    val weather by viewModel.weather.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val calls by viewModel.calls.collectAsState()
    val guidance by viewModel.guidance.collectAsState()
    val maxRows by viewModel.maxRows.collectAsState()
    val maxColumns by viewModel.maxColumns.collectAsState()
    LaunchedEffect(Unit) { viewModel.refreshGuidance() }
    val scope = rememberCoroutineScope()
    var selectedPageId by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedWidgetId by rememberSaveable { mutableStateOf<String?>(null) }
    var preview by rememberSaveable { mutableStateOf(false) }
    var pageMenu by remember { mutableStateOf(false) }
    var actionsMenu by remember { mutableStateOf(false) }
    var addMenu by remember { mutableStateOf(false) }
    var widgetMenu by remember { mutableStateOf(false) }
    var configuringWidgetId by rememberSaveable { mutableStateOf<String?>(null) }
    var renamePageId by remember { mutableStateOf<String?>(null) }
    var pendingPageAction by remember { mutableStateOf<Pair<String, String>?>(null) }
    var renameText by rememberSaveable { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    val selected = pages.firstOrNull { it.page.id == selectedPageId } ?: pages.firstOrNull()
    val pageIndex = pages.indexOfFirst { it.page.id == selected?.page?.id }
    val widget = selected?.widgets?.firstOrNull { it.id == selectedWidgetId }
    val needsResize = selected?.widgets?.any {
        !WidgetRegistry.valid(it.typeId, it.rect(), maxColumns) ||
            it.y + it.height > maxRows
    } == true
    LaunchedEffect(selected?.page?.id) {
        selectedPageId = selected?.page?.id
        selectedWidgetId = null
    }
    LaunchedEffect(message) {
        if (message.isNotEmpty()) { delay(3_000); message = "" }
    }
    BackHandler(enabled = preview) { preview = false }

    Column(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
            .then(if (addMenu) Modifier.blur(RideUiTokens.sheetBackgroundBlur) else Modifier)
            .padding(horizontal = RideUiTokens.rideSidePadding,
                vertical = RideUiTokens.rideScreenPadding),
        verticalArrangement = Arrangement.spacedBy(RideUiTokens.rideRailGap)
    ) {
        Box(Modifier.fillMaxWidth().weight(1f)) {
            if (selected != null) {
                if (preview) {
                    FixedRideCanvas(selected.widgets, maxRows, maxColumns, music, viewModel::controlMusic,
                        volume, { viewModel.setVolume(it) }, weather, notifications,
                        preferences.privacyMode, calls, guidance,
                        editorPreview = true)
                } else {
                    EditorCanvas(
                        widgets = selected.widgets,
                        music = music,
                        volume = volume,
                        weather = weather,
                        notifications = notifications,
                        notificationPrivacy = preferences.privacyMode,
                        calls = calls,
                        guidance = guidance,
                        maxRows = maxRows,
                        maxColumns = maxColumns,
                        interactive = true,
                        selectedWidgetId = selectedWidgetId,
                        onSelect = { selectedWidgetId = it },
                        onPlace = { item, rect -> scope.launch {
                            message = if (viewModel.placeWidget(item.id, rect)) "Widget saved"
                                else "Position or size conflicts with another widget"
                        } },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            } else {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Loading pages…") }
            }
            if (message.isNotEmpty()) {
                Surface(color = MaterialTheme.colorScheme.inverseSurface,
                    shape = RoundedCornerShape(RideUiTokens.rideButtonCorner),
                    modifier = Modifier.align(Alignment.BottomCenter)) {
                    Text(message, modifier = Modifier.padding(RideUiTokens.contentGap),
                        color = MaterialTheme.colorScheme.inverseOnSurface)
                }
            }
        }
        Crossfade(targetState = preview,
            animationSpec = tween(if (preferences.reducedMotion) 0 else 150),
            label = "editor preview") { showingPreview ->
            if (showingPreview) {
                Row(Modifier.fillMaxWidth().height(RideUiTokens.rideRailHeight)
                    .background(MaterialTheme.colorScheme.surfaceContainerLow,
                        RoundedCornerShape(RideUiTokens.panelCorner)),
                    horizontalArrangement = Arrangement.spacedBy(RideUiTokens.rideRailGap),
                    verticalAlignment = Alignment.CenterVertically) {
                    RideRailButton("End ride", onClick = { preview = false })
                    Text("${pageIndex + 1} / ${pages.size} · ${selected?.page?.name.orEmpty()}",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1, overflow = TextOverflow.Ellipsis)
                    RideRailButton("Maps", onClick = {}, enabled = false)
                    RideRailButton("Assistant", onClick = {})
                }
            } else {
                Row(Modifier.fillMaxWidth().height(RideUiTokens.rideRailHeight)
                    .background(MaterialTheme.colorScheme.surfaceContainerLow,
                        RoundedCornerShape(RideUiTokens.panelCorner)),
                    horizontalArrangement = Arrangement.spacedBy(RideUiTokens.rideRailGap),
                    verticalAlignment = Alignment.CenterVertically) {
                    EditorIcon(PhosphorIcons.Fill.ArrowLeft, "Back", onBack)
                    if (widget != null) {
                Surface(
                    modifier = Modifier.height(RideUiTokens.rideRailHeight),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    shape = RoundedCornerShape(RideUiTokens.rideButtonCorner)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(RideUiTokens.widgetPadding)) {
                        Text(WidgetRegistry.get(widget.typeId)?.title ?: widget.typeId,
                            modifier = Modifier.padding(start = RideUiTokens.contentGap),
                            style = MaterialTheme.typography.titleMedium)
                        Box {
                            EditorIcon(PhosphorIcons.Fill.Gear, "Widget options") { widgetMenu = true }
                            DropdownMenu(expanded = widgetMenu, onDismissRequest = { widgetMenu = false }) {
                                fun action(block: suspend () -> Unit) {
                                    widgetMenu = false
                                    scope.launch { block() }
                                }
                                if (!WidgetRegistry.valid(widget.typeId, widget.rect(), maxColumns) ||
                                    widget.y + widget.height > maxRows) {
                                    DropdownMenuItem(text = { Text("Fit to grid") }, onClick = {
                                        action { message = if (viewModel.fitWidget(widget.id)) "Widget fitted to grid"
                                            else "No valid space for this widget" }
                                    })
                                }
                                if (widget.typeId in listOf("speedometer", "sos_contacts")) {
                                    DropdownMenuItem(text = { Text("Widget settings") }, onClick = {
                                        widgetMenu = false
                                        configuringWidgetId = widget.id
                                    })
                                }
                                val canRotate = WidgetRegistry.rotatedPlacement(widget.typeId,
                                    widget.rect(), selected?.widgets.orEmpty().filter { it.id != widget.id }
                                        .map { it.rect() }, maxRows, maxColumns) != null
                                if (canRotate) {
                                    DropdownMenuItem(text = { Text("Rotate") }, onClick = {
                                        action { message = if (viewModel.rotateWidget(widget.id)) "Widget rotated"
                                            else "No room to rotate this widget" }
                                    })
                                }
                                DropdownMenuItem(text = { Text("Wider") }, onClick = {
                                    action { message = if (viewModel.placeWidget(widget.id,
                                            widget.rect().copy(width = widget.width + 1))) "Widget saved"
                                        else "Maximum size, edge, or occupied cells" }
                                })
                                DropdownMenuItem(text = { Text("Narrower") }, onClick = {
                                    action { message = if (viewModel.placeWidget(widget.id,
                                            widget.rect().copy(width = widget.width - 1))) "Widget saved"
                                        else "Minimum size or occupied cells" }
                                })
                                DropdownMenuItem(text = { Text("Taller") }, onClick = {
                                    action { message = if (viewModel.placeWidget(widget.id,
                                            widget.rect().copy(height = widget.height + 1))) "Widget saved"
                                        else "Maximum size, edge, or occupied cells" }
                                })
                                DropdownMenuItem(text = { Text("Shorter") }, onClick = {
                                    action { message = if (viewModel.placeWidget(widget.id,
                                            widget.rect().copy(height = widget.height - 1))) "Widget saved"
                                        else "Minimum size or occupied cells" }
                                })
                                DropdownMenuItem(text = { Text("Duplicate") }, onClick = {
                                    action { message = if (viewModel.duplicateWidget(widget.id))
                                        "Widget duplicated" else "No room to duplicate" }
                                })
                                DropdownMenuItem(text = { Text(if (widget.visible) "Hide" else "Show") }, onClick = {
                                    action { viewModel.setWidgetVisible(widget.id, !widget.visible) }
                                })
                                DropdownMenuItem(text = { Text("Remove") }, onClick = {
                                    action { viewModel.removeWidget(widget.id) }
                                })
                            }
                        }
                    }
                }
            }
                    Box(Modifier.weight(1f)) {
                        Button(onClick = { pageMenu = true },
                            modifier = Modifier.fillMaxWidth().height(RideUiTokens.rideRailHeight),
                            shape = RoundedCornerShape(RideUiTokens.rideButtonCorner),
                            contentPadding = PaddingValues(horizontal = RideUiTokens.contentGap)) {
                            Text("${pageIndex + 1} / ${pages.size} · ${selected?.page?.name.orEmpty()}" +
                                if (needsResize) " · Resize needed" else "",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                    EditorIcon(PhosphorIcons.Regular.Plus, "Add widget") { addMenu = true }
                    Box {
                        EditorIcon(PhosphorIcons.Fill.List, "Page actions") { actionsMenu = true }
                        DropdownMenu(expanded = actionsMenu, onDismissRequest = { actionsMenu = false }) {
                            if (selected != null) {
                                DropdownMenuItem(text = { Text("New page") }, leadingIcon = { Icon(PhosphorIcons.Regular.Plus, null) },
                                    onClick = { actionsMenu = false; scope.launch { selectedPageId = viewModel.addPage() } })
                                DropdownMenuItem(text = { Text("Rename page") }, leadingIcon = { Icon(PhosphorIcons.Fill.Pencil, null) },
                                    onClick = { actionsMenu = false; renameText = selected.page.name; renamePageId = selected.page.id })
                                DropdownMenuItem(text = { Text("Duplicate page") }, leadingIcon = { Icon(PhosphorIcons.Fill.Copy, null) },
                                    onClick = { actionsMenu = false; scope.launch {
                                        selectedPageId = viewModel.duplicatePage(selected.page.id)
                                    } })
                                DropdownMenuItem(text = { Text(if (selected.page.enabled) "Disable page" else "Enable page") },
                                    enabled = !selected.page.enabled || pages.count { it.page.enabled } > 1,
                                    onClick = { actionsMenu = false; scope.launch {
                                        viewModel.setPageEnabled(selected.page.id, !selected.page.enabled)
                                    } })
                                DropdownMenuItem(text = { Text("Move earlier") }, enabled = pageIndex > 0, onClick = {
                                    actionsMenu = false; scope.launch { viewModel.shiftPage(selected.page.id, -1) }
                                })
                                DropdownMenuItem(text = { Text("Move later") }, enabled = pageIndex < pages.lastIndex, onClick = {
                                    actionsMenu = false; scope.launch { viewModel.shiftPage(selected.page.id, 1) }
                                })
                                DropdownMenuItem(text = { Text("Reset page layout") }, onClick = {
                                    actionsMenu = false
                                    pendingPageAction = selected.page.id to "reset"
                                })
                                DropdownMenuItem(text = { Text("Delete page") }, leadingIcon = { Icon(PhosphorIcons.Fill.Trash, null) },
                                    enabled = pages.size > 1,
                                    onClick = {
                                        actionsMenu = false
                                        pendingPageAction = selected.page.id to "delete"
                                    })
                                RideGridConstraints.ALLOWED_ROW_HEIGHT_DP.forEach { height ->
                                    DropdownMenuItem(text = { Text("Grid density: $height dp") }, onClick = {
                                        actionsMenu = false; scope.launch {
                                            message = if (viewModel.setRowHeight(height)) "Grid density saved"
                                                else "Fit widgets inside this grid before choosing the density"
                                        }
                                    })
                                }

                            }
                        }
                    }
                    EditorIcon(PhosphorIcons.Fill.Eye, "Preview") { preview = true }
                }
            }
        }
    }

    pages.flatMap { it.widgets }.firstOrNull { it.id == configuringWidgetId }?.let { target ->
        WidgetSettingsDialog(target, onDismiss = { configuringWidgetId = null }, onSave = { json ->
            val targetId = target.id
            val saved = viewModel.updateWidgetConfig(targetId, json)
            if (saved) {
                message = "Widget settings saved"
                configuringWidgetId = null
            }
            saved
        })
    }

    if (addMenu) {
        ModalBottomSheet(onDismissRequest = { addMenu = false }) {
            Text("Add widget", style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(horizontal = RideUiTokens.screenPadding))
            Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
                .padding(RideUiTokens.screenPadding),
                verticalArrangement = Arrangement.spacedBy(RideUiTokens.widgetPadding)) {
                WidgetRegistry.pickerWidgets.forEach { definition ->
                    val shortest = (definition.minWidth..definition.maxWidth.coerceAtMost(maxColumns)).flatMap { width ->
                        (definition.minHeight..definition.maxHeight.coerceAtMost(maxRows)).map { height -> width to height }
                    }.sortedWith(compareBy<Pair<Int, Int>> { it.first * it.second }
                        .thenBy { it.second }.thenBy { it.first })
                        .firstOrNull { (width, height) ->
                            WidgetRegistry.valid(definition.id, com.kapav.ridemode.core.model.GridRect(0, 0, width, height))
                        }
                    Surface(onClick = {
                        addMenu = false
                        selected?.let { page -> scope.launch {
                            message = if (viewModel.addWidget(page.page.id, definition.id))
                                "Widget added" else "No space for this widget"
                        } }
                    }, shape = RoundedCornerShape(RideUiTokens.rideButtonCorner),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh) {
                        Row(Modifier.fillMaxWidth().height(RideUiTokens.minimumTouchTarget)
                            .padding(horizontal = RideUiTokens.contentGap),
                            verticalAlignment = Alignment.CenterVertically) {
                            Text(definition.title, modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.titleMedium)
                            Text(shortest?.let { "${it.first}×${it.second}" } ?: "Unavailable",
                                style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }

    if (pageMenu) {
        ModalBottomSheet(onDismissRequest = { pageMenu = false }) {
            Row(Modifier.fillMaxWidth().padding(horizontal = RideUiTokens.screenPadding),
                horizontalArrangement = Arrangement.spacedBy(RideUiTokens.widgetPadding),
                verticalAlignment = Alignment.CenterVertically) {
                Text("Pages", modifier = Modifier.weight(1f), style = MaterialTheme.typography.headlineSmall)
                Button(onClick = { scope.launch {
                    selectedPageId = viewModel.addPage()
                } }) { Text("Add page") }
            }
            Text("Hold a page and drag it to change Ride order.",
                modifier = Modifier.padding(horizontal = RideUiTokens.screenPadding),
                style = MaterialTheme.typography.bodyMedium)
            Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
                .padding(RideUiTokens.screenPadding),
                verticalArrangement = Arrangement.spacedBy(RideUiTokens.widgetPadding)) {
                pages.forEachIndexed { index, page ->
                    PageManagerRow(page, index, pages.lastIndex,
                        selected = page.page.id == selected?.page?.id,
                        onOpen = { selectedPageId = page.page.id; pageMenu = false },
                        onRename = { pageMenu = false; renameText = page.page.name; renamePageId = page.page.id },
                        onDelete = { pageMenu = false; pendingPageAction = page.page.id to "delete" },
                        onMove = { target -> scope.launch { viewModel.movePage(page.page.id, target) } })
                }
            }
        }
    }

    renamePageId?.let { pageId ->
        AlertDialog(
            onDismissRequest = { renamePageId = null },
            title = { Text("Rename page") },
            text = { OutlinedTextField(value = renameText, onValueChange = { renameText = it.take(40) },
                label = { Text("Page name") }, singleLine = true) },
            confirmButton = { Button(onClick = {
                scope.launch { viewModel.renamePage(pageId, renameText) }
                renamePageId = null
            }) { Text("Save") } },
            dismissButton = { Button(onClick = { renamePageId = null }) { Text("Cancel") } }
        )
    }
    pendingPageAction?.let { (pageId, action) ->
        val name = pages.firstOrNull { it.page.id == pageId }?.page?.name ?: "this page"
        AlertDialog(
            onDismissRequest = { pendingPageAction = null },
            title = { Text(if (action == "delete") "Delete page?" else "Reset page layout?") },
            text = { Text(if (action == "delete")
                "Delete $name and its widgets?" else "Remove the current widget placements from $name?") },
            confirmButton = { Button(onClick = {
                pendingPageAction = null
                scope.launch {
                    if (action == "delete") viewModel.deletePage(pageId)
                    else viewModel.resetPage(pageId)
                    message = if (action == "delete") "Page deleted" else "Page layout reset"
                }
            }) { Text(if (action == "delete") "Delete" else "Reset") } },
            dismissButton = { Button(onClick = { pendingPageAction = null }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun PageManagerRow(
    page: PageWithWidgets,
    index: Int,
    lastIndex: Int,
    selected: Boolean,
    onOpen: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit,
    onMove: (Int) -> Unit
) {
    val rowHeightPx = with(LocalDensity.current) { RideUiTokens.pageManagerRowHeight.toPx() }
    var dragOffset by remember(page.page.id) { mutableStateOf(0f) }
    var targetIndex by remember(page.page.id) { mutableStateOf(index) }
    Surface(
        modifier = Modifier.fillMaxWidth()
            .offset { IntOffset(0, dragOffset.roundToInt()) }
            .zIndex(if (dragOffset != 0f) 1f else 0f),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = RoundedCornerShape(RideUiTokens.rideButtonCorner)
    ) {
        Row(Modifier.fillMaxWidth().height(RideUiTokens.pageManagerRowHeight),
            verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(RideUiTokens.minimumTouchTarget)
                .pointerInput(page.page.id, index, lastIndex) {
                    detectDragGesturesAfterLongPress(
                        onDragStart = { dragOffset = 0f; targetIndex = index },
                        onDragEnd = {
                            if (targetIndex != index) onMove(targetIndex)
                            dragOffset = 0f
                        },
                        onDragCancel = { dragOffset = 0f },
                        onDrag = { change, amount ->
                            change.consume()
                            dragOffset += amount.y
                            targetIndex = (index + (dragOffset / rowHeightPx).roundToInt())
                                .coerceIn(0, lastIndex)
                        }
                    )
                }, contentAlignment = Alignment.Center) {
                Icon(PhosphorIcons.Fill.List, contentDescription = "Hold and drag to reorder")
            }
            Button(onClick = onOpen, modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = RideUiTokens.widgetPadding)) {
                Text(page.page.name + if (page.page.enabled) "" else " (off)",
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            IconButton(onClick = onRename,
                modifier = Modifier.size(RideUiTokens.minimumTouchTarget)) {
                Icon(PhosphorIcons.Fill.Pencil, contentDescription = "Rename ${page.page.name}")
            }
            IconButton(onClick = onDelete, enabled = lastIndex > 0,
                modifier = Modifier.size(RideUiTokens.minimumTouchTarget)) {
                Icon(PhosphorIcons.Fill.Trash, contentDescription = "Delete ${page.page.name}")
            }
        }
    }
}

@Composable
private fun EditorIcon(icon: ImageVector, label: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.size(RideUiTokens.rideRailHeight),
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(RideUiTokens.rideButtonCorner)
    ) {
        IconButton(onClick = onClick, modifier = Modifier.fillMaxSize()) {
            Icon(icon, contentDescription = label,
                modifier = Modifier.size(RideUiTokens.iconSize),
                tint = MaterialTheme.colorScheme.onPrimaryContainer)
        }
    }
}
