package com.kapav.ridemode.feature.ride

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
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
    editorPreview: Boolean = false) {
    val visible = widgets.filter { it.visible }
    val mapWidget = visible.firstOrNull { it.typeId == "google_maps" }
    val mapState = mapsController?.state?.collectAsState()?.value
    val mapExpanded = mapWidget != null && mapState?.expanded == true
    val rows = maxRows
    BoxWithConstraints(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        val cellWidth = RideGridConstraints.cellSideForCanvas(maxWidth.value, maxHeight.value,
            RideGridConstraints.GridSize(maxColumns, rows)).dp
        val cellHeight = cellWidth
        Box(Modifier.size(width = cellWidth * maxColumns, height = cellHeight * rows)) {
        if (!mapExpanded) visible.filterNot { it === mapWidget && mapsController != null }.forEach { widget ->
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
        if (mapWidget != null && mapsController != null) {
            if (WidgetRegistry.valid(mapWidget.typeId, mapWidget.rect(), maxColumns)) {
                GoogleMapsWidget(mapsController, onRequestMapLocation,
                    modifier = Modifier
                        .offset(x = if (mapExpanded) 0.dp else cellWidth * mapWidget.x,
                            y = if (mapExpanded) 0.dp else cellHeight * mapWidget.y)
                        .size(width = if (mapExpanded) cellWidth * maxColumns else
                                cellWidth * mapWidget.width - RideUiTokens.gridGap,
                            height = if (mapExpanded) cellHeight * rows else
                                cellHeight * mapWidget.height - RideUiTokens.gridGap)
                        .zIndex(1f))
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
