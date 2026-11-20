package com.kapav.ridemode.feature.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextOverflow
import com.kapav.ridemode.core.database.WidgetInstance
import com.kapav.ridemode.core.model.WidgetRegistry
import com.kapav.ridemode.data.rect
import com.kapav.ridemode.data.MusicSessionState
import com.kapav.ridemode.data.MusicAction
import com.kapav.ridemode.data.VolumeState
import com.kapav.ridemode.data.WeatherState
import com.kapav.ridemode.data.NotificationFeedState
import com.kapav.ridemode.data.CallHistoryState
import com.kapav.ridemode.data.TurnGuidanceState
import com.kapav.ridemode.core.ui.theme.RideUiTokens

@Composable
fun WidgetCard(
    widget: WidgetInstance,
    maxColumns: Int = com.kapav.ridemode.core.model.GridLayout.COLUMNS,
    modifier: Modifier = Modifier,
    oled: Boolean = false,
    compact: Boolean = false,
    editor: Boolean = false,
    music: MusicSessionState = MusicSessionState(),
    onMusicAction: (MusicAction) -> Unit = {},
    volume: VolumeState = VolumeState(),
    onVolumeChange: (Float) -> Boolean = { false },
    weather: WeatherState = WeatherState(),
    notifications: NotificationFeedState = NotificationFeedState(),
    notificationPrivacy: Boolean = false,
    calls: CallHistoryState = CallHistoryState(),
    guidance: TurnGuidanceState = TurnGuidanceState(),
    deferredContent: DeferredWidgetContent = DeferredWidgetContent()
) {
    if (widget.typeId == "speedometer") {
        SpeedometerWidget(widget.id, widget.configJson, editor, modifier)
        return
    }
    val definition = WidgetRegistry.get(widget.typeId)
    if (widget.typeId == "music") {
        if (WidgetRegistry.valid(widget.typeId, widget.rect(), maxColumns)) {
            MusicWidget(editor, music, onMusicAction, modifier)
        } else {
            Surface(modifier, color = MaterialTheme.colorScheme.errorContainer) {
                Box(Modifier.fillMaxSize().padding(RideUiTokens.widgetPadding),
                    contentAlignment = Alignment.Center) {
                    AdaptiveWidgetText("Music needs at least 5 × 4 cells and a square or landscape shape. Resize it in the editor.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.fillMaxSize())
                }
            }
        }
        return
    }
    if (widget.typeId == "volume") {
        if (WidgetRegistry.valid(widget.typeId, widget.rect(), maxColumns)) {
            VolumeWidget(editor, vertical = widget.width == 1, state = volume,
                onChange = onVolumeChange, modifier = modifier)
        } else {
            Surface(modifier, color = MaterialTheme.colorScheme.errorContainer) {
                Box(Modifier.fillMaxSize().padding(RideUiTokens.widgetPadding),
                    contentAlignment = Alignment.Center) {
                    AdaptiveWidgetText("Volume needs one grid cell on its short side. Resize it in the editor.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.fillMaxSize())
                }
            }
        }
        return
    }
    if (widget.typeId == "clock" || widget.typeId == "weather") {
        if (WidgetRegistry.valid("clock", widget.rect(), maxColumns)) {
            ClockWidget(weather, modifier, gridRows = widget.height)
        } else {
            Surface(modifier, color = MaterialTheme.colorScheme.errorContainer) {
                Box(Modifier.fillMaxSize().padding(RideUiTokens.widgetPadding),
                    contentAlignment = Alignment.Center) {
                    AdaptiveWidgetText("Clock needs at least 2 × 1 cells. Resize it in the editor.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.fillMaxSize())
                }
            }
        }
        return
    }
    if (widget.typeId == "notifications") {
        if (WidgetRegistry.valid(widget.typeId, widget.rect(), maxColumns))
            NotificationFeedWidget(notifications, notificationPrivacy, modifier)
        else Surface(modifier, color = MaterialTheme.colorScheme.errorContainer) {
            AdaptiveWidgetText("Resize Notifications to at least 4 × 5 cells.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.fillMaxSize().padding(RideUiTokens.widgetPadding))
        }
        return
    }
    if (widget.typeId == "google_maps") {
        Surface(modifier, color = MaterialTheme.colorScheme.surfaceContainerHigh,
            shape = MaterialTheme.shapes.large) {
            Box(Modifier.fillMaxSize().padding(RideUiTokens.widgetPadding),
                contentAlignment = Alignment.Center) {
                Text(if (WidgetRegistry.valid(widget.typeId, widget.rect(), maxColumns))
                    "Google Maps · Live in Ride" else "Resize Google Maps to at least 4 × 4 cells",
                    style = MaterialTheme.typography.titleMedium)
            }
        }
        return
    }
    if (widget.typeId == "calls") {
        if (WidgetRegistry.valid(widget.typeId, widget.rect(), maxColumns)) CallHistoryWidget(calls, editor, modifier)
        else Surface(modifier, color = MaterialTheme.colorScheme.errorContainer) {
            AdaptiveWidgetText("Resize Calls to at least 4 × 5 cells.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.fillMaxSize().padding(RideUiTokens.widgetPadding))
        }
        return
    }
    if (widget.typeId == "battery" || widget.typeId == "maps_shortcut" ||
        widget.typeId == "sos_contacts") {
        if (WidgetRegistry.valid(widget.typeId, widget.rect(), maxColumns)) {
            when (widget.typeId) {
                "battery" -> BatteryWidget(vertical = widget.height > widget.width, modifier = modifier)
                "maps_shortcut" -> MapsShortcutWidget(modifier)
                "sos_contacts" -> SosFavoritesWidget(modifier, widgetId = widget.id, configJson = widget.configJson, editor = editor)
            }
        } else {
            Surface(modifier, color = MaterialTheme.colorScheme.errorContainer) {
                Box(Modifier.fillMaxSize().padding(RideUiTokens.widgetPadding),
                    contentAlignment = Alignment.Center) {
                    AdaptiveWidgetText("Resize this widget to a supported grid size in the editor.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.fillMaxSize())
                }
            }
        }
        return
    }
    if (widget.typeId in setOf("turn_guidance", "assistant")) {
        if (WidgetRegistry.valid(widget.typeId, widget.rect(), maxColumns)) {
            when (widget.typeId) {
                "turn_guidance" -> TurnGuidanceWidget(guidance, editor, modifier)
                "assistant" -> AssistantWidget(modifier, editor = editor)
            }
        } else {
            Surface(modifier, color = MaterialTheme.colorScheme.errorContainer,
                shape = MaterialTheme.shapes.medium) {
                AdaptiveWidgetText("Resize this widget to a supported grid size.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.fillMaxSize().padding(RideUiTokens.widgetPadding))
            }
        }
        return
    }
    Surface(
        modifier = modifier,
        color = if (oled) androidx.compose.ui.graphics.Color.Black else MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(RideUiTokens.widgetPadding),
            verticalArrangement = Arrangement.spacedBy(RideUiTokens.widgetPadding)
        ) {
            if (!(compact && widget.height == 1)) {
                AdaptiveWidgetText(
                    definition?.title ?: "Unknown widget",
                    style = if (compact) MaterialTheme.typography.labelLarge else MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxSize().weight(1f)
                )
            }
            if (compact && widget.height == 1) {
                AdaptiveWidgetText(compactLabel(widget.typeId), style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxSize().weight(1f))
            } else when (widget.typeId) {
                "calls" -> Placeholder("Mock · Recent calls unavailable", Modifier.weight(2f))
                "notifications" -> Placeholder("Mock · Message preview · No access", Modifier.weight(2f))
                "turn_guidance" -> Placeholder("Experimental · Guidance unavailable", Modifier.weight(2f))
                "assistant" -> Placeholder("Mock · Tap assistant in rail", Modifier.weight(2f))
                else -> Placeholder("Unavailable widget", Modifier.weight(2f))
            }
        }
    }
}

private fun compactLabel(type: String): String = when (type) {
    "turn_guidance" -> "Guidance unavailable"
    else -> "Mock · No live control"
}

@Composable
private fun Placeholder(text: String, modifier: Modifier) {
    AdaptiveWidgetText(text, style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = modifier.fillMaxSize())
}

