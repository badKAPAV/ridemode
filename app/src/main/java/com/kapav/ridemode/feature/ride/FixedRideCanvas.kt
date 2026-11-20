package com.kapav.ridemode.feature.ride

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import androidx.compose.ui.unit.dp
import com.kapav.ridemode.core.database.WidgetInstance
import com.kapav.ridemode.core.model.RideGridConstraints
import com.kapav.ridemode.core.ui.theme.RideUiTokens
import com.kapav.ridemode.feature.widgets.WidgetCard
import com.kapav.ridemode.data.MusicAction
import com.kapav.ridemode.data.MusicSessionState
import com.kapav.ridemode.data.VolumeState
import com.kapav.ridemode.data.WeatherState
import com.kapav.ridemode.data.NotificationFeedState
import com.kapav.ridemode.data.CallHistoryState
import com.kapav.ridemode.data.TurnGuidanceState
import com.kapav.ridemode.feature.widgets.GoogleMapsController
import com.kapav.ridemode.feature.widgets.GoogleMapsWidget
import com.kapav.ridemode.core.model.WidgetRegistry
import com.kapav.ridemode.data.rect

/** Draws one saved grid into the current viewport. The canvas never scrolls. */
@Composable
fun FixedRideCanvas(widgets: List<WidgetInstance>, maxRows: Int, maxColumns: Int,
    music: MusicSessionState = MusicSessionState(),
    onMusicAction: (MusicAction) -> Unit = {},
    volume: VolumeState = VolumeState(),
    onVolumeChange: (Float) -> Boolean = { false },
    weather: WeatherState = WeatherState(),
    notifications: NotificationFeedState = NotificationFeedState(),
    notificationPrivacy: Boolean = false,
    calls: CallHistoryState = CallHistoryState(),
    guidance: TurnGuidanceState = TurnGuidanceState(),
    mapsController: GoogleMapsController? = null,
    onRequestMapLocation: () -> Unit = {},
    editorPreview: Boolean = false,
    reducedMotion: Boolean = false,
    initializeMap: Boolean = true) {
    val visible = widgets.filter { it.visible }
    val mapWidget = visible.firstOrNull { it.typeId == "google_maps" }
    val mapState = mapsController?.state?.collectAsState()?.value
    val mapExpanded = mapWidget != null && mapState?.expanded == true
    val expansion by animateFloatAsState(if (mapExpanded) 1f else 0f,
        animationSpec = tween(if (reducedMotion) 0 else RideUiTokens.mapTransformMillis),
        label = "Map grid expansion")
    LaunchedEffect(expansion) {
        if (expansion == 0f || expansion == 1f) mapsController?.followNavigation()
    }
    var mapInitialized by remember(mapWidget?.id, mapsController) { mutableStateOf(false) }
    LaunchedEffect(initializeMap, mapWidget?.id, mapsController) {
        if (initializeMap && mapWidget != null && mapsController != null && !mapInitialized) {
            // Yield a frame after the pager settles before creating a native map.
            withFrameNanos { }
            mapInitialized = true
        }
    }
    val rows = maxRows
    BoxWithConstraints(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        val cellWidth = RideGridConstraints.cellSideForCanvas(maxWidth.value, maxHeight.value,
            RideGridConstraints.GridSize(maxColumns, rows)).dp
        val cellHeight = cellWidth
        Box(Modifier.size(width = cellWidth * maxColumns, height = cellHeight * rows)) {
        if (!mapExpanded && expansion == 0f) visible.filterNot { it === mapWidget && mapsController != null }.forEach { widget ->
            key(widget.id) {
            WidgetCard(widget, maxColumns = maxColumns,
                music = music, onMusicAction = onMusicAction,
                volume = volume, onVolumeChange = onVolumeChange, weather = weather,
                notifications = notifications, notificationPrivacy = notificationPrivacy,
                calls = calls, guidance = guidance, editor = editorPreview,
                modifier = Modifier
                    .offset(x = cellWidth * widget.x, y = cellHeight * widget.y)
                    .size(
                        width = cellWidth * widget.width - RideUiTokens.gridGap,
                        height = cellHeight * widget.height - RideUiTokens.gridGap
                    ),
                compact = cellHeight * widget.height < RideUiTokens.widgetCompactHeight
            )
            }
        }
        if (mapWidget != null && mapsController != null) {
            if (WidgetRegistry.valid(mapWidget.typeId, mapWidget.rect(), maxColumns)) {
                val mapModifier = Modifier
                        .offset(x = cellWidth * mapWidget.x * (1f - expansion),
                            y = cellHeight * mapWidget.y * (1f - expansion))
                        .size(width = (cellWidth * mapWidget.width - RideUiTokens.gridGap) * (1f - expansion) +
                                cellWidth * maxColumns * expansion,
                            height = (cellHeight * mapWidget.height - RideUiTokens.gridGap) * (1f - expansion) +
                                cellHeight * rows * expansion)
                        .zIndex(1f)
                if (mapInitialized) GoogleMapsWidget(mapsController, onRequestMapLocation,
                    reducedMotion = reducedMotion, modifier = mapModifier, renderMap = initializeMap)
                else Surface(mapModifier, shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.surfaceContainer) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        com.kapav.ridemode.feature.widgets.MapLoadingIndicator()
                    }
                }
            } else {
                WidgetCard(mapWidget, maxColumns = maxColumns,
                    modifier = Modifier.offset(x = cellWidth * mapWidget.x,
                        y = cellHeight * mapWidget.y)
                        .size(cellWidth * mapWidget.width, cellHeight * mapWidget.height))
            }
        }
        }
    }
}
