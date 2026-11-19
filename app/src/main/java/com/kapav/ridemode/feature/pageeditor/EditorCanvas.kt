package com.kapav.ridemode.feature.pageeditor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.kapav.ridemode.core.database.WidgetInstance
import com.kapav.ridemode.core.model.GridLayout
import com.kapav.ridemode.core.model.GridRect
import com.kapav.ridemode.core.model.RideGridConstraints
import com.kapav.ridemode.core.model.WidgetRegistry
import com.kapav.ridemode.core.ui.theme.RideUiTokens
import com.kapav.ridemode.data.rect
import com.kapav.ridemode.data.MusicSessionState
import com.kapav.ridemode.data.VolumeState
import com.kapav.ridemode.data.WeatherState
import com.kapav.ridemode.data.NotificationFeedState
import com.kapav.ridemode.data.CallHistoryState
import com.kapav.ridemode.data.TurnGuidanceState
import com.kapav.ridemode.feature.widgets.WidgetCard
import kotlin.math.roundToInt

@Composable
fun EditorCanvas(
    widgets: List<WidgetInstance>,
    music: MusicSessionState,
    volume: VolumeState,
    weather: WeatherState,
    notifications: NotificationFeedState,
    notificationPrivacy: Boolean,
    calls: CallHistoryState,
    guidance: TurnGuidanceState,
    maxRows: Int,
    maxColumns: Int,
    interactive: Boolean,
    selectedWidgetId: String?,
    onSelect: (String) -> Unit,
    onPlace: (WidgetInstance, GridRect) -> Unit,
    modifier: Modifier = Modifier
) {
    val rows = maxRows
    val gridColor = MaterialTheme.colorScheme.outlineVariant
    BoxWithConstraints(modifier = modifier.fillMaxSize()
        .background(MaterialTheme.colorScheme.surface), contentAlignment = Alignment.TopCenter) {
        val cellWidth = RideGridConstraints.cellSideForCanvas(maxWidth.value, maxHeight.value,
            RideGridConstraints.GridSize(maxColumns, rows)).dp
        val rowHeight = cellWidth
        val density = LocalDensity.current
        val cellWidthPx = with(density) { cellWidth.toPx() }
        val rowHeightPx = with(density) { rowHeight.toPx() }
        Box(Modifier.size(width = cellWidth * maxColumns, height = rowHeight * rows)
            .drawBehind {
                for (column in 0..maxColumns) for (row in 0..rows) {
                    drawCircle(gridColor, radius = RideUiTokens.gridDotRadius.toPx(),
                        center = Offset(column * cellWidthPx, row * rowHeightPx))
                }
            }) {
        widgets.forEach { widget ->
            var dx by remember(widget.id) { mutableFloatStateOf(0f) }
            var dy by remember(widget.id) { mutableFloatStateOf(0f) }
            var dragging by remember(widget.id) { mutableStateOf(false) }
            var resizeRect by remember(widget.id, widget.rect()) { mutableStateOf(widget.rect()) }
            var resizing by remember(widget.id) { mutableStateOf(false) }
            val definition = WidgetRegistry.get(widget.typeId)
            val drag = if (interactive) Modifier.pointerInput(widget.id, widget.x, widget.y, cellWidthPx, rowHeightPx) {
                detectDragGestures(
                    onDragStart = { dragging = true; onSelect(widget.id) },
                    onDragEnd = {
                        onPlace(widget, widget.rect().copy(
                            x = widget.x + (dx / cellWidthPx).roundToInt(),
                            y = widget.y + (dy / rowHeightPx).roundToInt()
                        ))
                        dx = 0f; dy = 0f; dragging = false
                    },
                    onDragCancel = { dx = 0f; dy = 0f; dragging = false }
                ) { change, amount: Offset ->
                    change.consume()
                    dx = (dx + amount.x).coerceIn(-widget.x * cellWidthPx,
                        maxOf(-widget.x, maxColumns - widget.width - widget.x) * cellWidthPx)
                    dy = (dy + amount.y).coerceIn(-widget.y * rowHeightPx,
                        maxOf(-widget.y, maxRows - widget.height - widget.y) * rowHeightPx)
                }
            } else Modifier
            val proposed = widget.rect().copy(
                x = widget.x + (dx / cellWidthPx).roundToInt(),
                y = widget.y + (dy / rowHeightPx).roundToInt()
            )
            val previewRect = resizeRect
            val others = widgets.filter { it.id != widget.id }
            val overlapping = others.filter { GridLayout.issues(proposed, listOf(it.rect()), maxRows, maxColumns)
                .contains(com.kapav.ridemode.core.model.GridIssue.OVERLAP) }
            val swappable = dragging && !resizing && overlapping.size == 1 &&
                GridLayout.displacedPlacement(widget.rect(), proposed, overlapping.single().rect(),
                    others.filter { it.id != overlapping.single().id }.map { it.rect() }, maxRows, maxColumns) != null
            val collision = (dragging || resizing) && !swappable && (
                GridLayout.issues(if (resizing) previewRect else proposed,
                    others.map { it.rect() }, maxRows, maxColumns).isNotEmpty() ||
                    (resizing && !WidgetRegistry.valid(widget.typeId, previewRect, maxColumns)))
            val placementModifier = Modifier
                    .offset(x = cellWidth * resizeRect.x + with(density) { dx.toDp() },
                        y = rowHeight * resizeRect.y + with(density) { dy.toDp() })
                    .size(width = cellWidth * resizeRect.width - RideUiTokens.gridGap,
                        height = rowHeight * resizeRect.height - RideUiTokens.gridGap)
            Box(modifier = placementModifier
                .then(if (interactive && (selectedWidgetId == widget.id || collision))
                    Modifier.border(RideUiTokens.selectionStroke,
                        if (collision) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(RideUiTokens.selectionCorner))
                else Modifier)
                .alpha(if (!widget.visible) 0.5f else if (dragging) 0.85f else 1f)) {
                WidgetCard(widget = widget.copy(x = resizeRect.x, y = resizeRect.y,
                    width = resizeRect.width, height = resizeRect.height), maxColumns = maxColumns,
                    editor = true, music = music,
                    volume = volume,
                    weather = weather,
                    notifications = notifications,
                    notificationPrivacy = notificationPrivacy,
                    calls = calls,
                    guidance = guidance,
                    modifier = Modifier.fillMaxSize().then(drag)
                        .clickable(enabled = interactive) { onSelect(widget.id) })
                if (interactive && selectedWidgetId == widget.id &&
                    WidgetRegistry.valid(widget.typeId, widget.rect(), maxColumns) &&
                    widget.y + widget.height <= maxRows) {
                    val resizePreview: (Int, Int, Float, Float) -> Unit = { horizontal, vertical, deltaX, deltaY ->
                        resizing = true
                        val start = widget.rect()
                        val left = if (horizontal == -1) (start.x + (deltaX / cellWidthPx).roundToInt())
                            .coerceIn(0, maxOf(0, start.x + start.width - (definition?.minWidth ?: 1))) else start.x
                        val right = if (horizontal == 1) (start.x + start.width + (deltaX / cellWidthPx).roundToInt())
                            .coerceIn(minOf(start.x + (definition?.minWidth ?: 1), maxColumns), maxColumns) else start.x + start.width
                        val top = if (vertical == -1) (start.y + (deltaY / rowHeightPx).roundToInt())
                            .coerceIn(0, maxOf(0, start.y + start.height - (definition?.minHeight ?: 1))) else start.y
                        val bottom = if (vertical == 1) (start.y + start.height + (deltaY / rowHeightPx).roundToInt())
                            .coerceIn(minOf(start.y + (definition?.minHeight ?: 1), maxRows), maxRows) else start.y + start.height
                        resizeRect = GridRect(left, top, right - left, bottom - top)
                    }
                    val resizeFinish: () -> Unit = {
                        onPlace(widget, resizeRect)
                        resizeRect = widget.rect(); resizing = false
                    }
                    val resizeCancel: () -> Unit = {
                        resizeRect = widget.rect(); resizing = false
                    }
                    for (horizontal in -1..1) for (vertical in -1..1) {
                        if (horizontal == 0 && vertical == 0) continue
                        val align = when (vertical) {
                            -1 -> when (horizontal) { -1 -> Alignment.TopStart; 0 -> Alignment.TopCenter; else -> Alignment.TopEnd }
                            0 -> if (horizontal == -1) Alignment.CenterStart else Alignment.CenterEnd
                            else -> when (horizontal) { -1 -> Alignment.BottomStart; 0 -> Alignment.BottomCenter; else -> Alignment.BottomEnd }
                        }
                        ResizeHandle(widget, horizontal, vertical, resizePreview, resizeFinish, resizeCancel,
                            Modifier.align(align).offset(
                                x = if (horizontal == -1) -RideUiTokens.resizeHandleTouch / 2 else if (horizontal == 1) RideUiTokens.resizeHandleTouch / 2 else 0.dp,
                                y = if (vertical == -1) -RideUiTokens.resizeHandleTouch / 2 else if (vertical == 1) RideUiTokens.resizeHandleTouch / 2 else 0.dp),
                            "Resize ${if (horizontal == -1) "left" else if (horizontal == 1) "right" else ""} ${if (vertical == -1) "top" else if (vertical == 1) "bottom" else ""}")
                    }
                }
            }
        }
        }
    }
}

@Composable
private fun ResizeHandle(
    widget: WidgetInstance,
    horizontal: Int,
    vertical: Int,
    onPreview: (Int, Int, Float, Float) -> Unit,
    onFinish: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier,
    label: String
) {
    var dx by remember(widget.id, horizontal, vertical) { mutableFloatStateOf(0f) }
    var dy by remember(widget.id, horizontal, vertical) { mutableFloatStateOf(0f) }
    Box(modifier.size(RideUiTokens.resizeHandleTouch)
        .semantics { contentDescription = "$label ${widget.typeId}" }
        .pointerInput(widget.id, widget.width, widget.height, horizontal, vertical) {
            detectDragGestures(
                onDragEnd = {
                    onFinish()
                    dx = 0f; dy = 0f
                },
                onDragCancel = { onCancel(); dx = 0f; dy = 0f }
            ) { change, amount ->
                change.consume(); dx += amount.x; dy += amount.y
                onPreview(horizontal, vertical, dx, dy)
            }
        }, contentAlignment = Alignment.Center) {
        Box(Modifier.size(RideUiTokens.resizeHandleVisual)
            .background(MaterialTheme.colorScheme.primary, CircleShape))
    }
}
