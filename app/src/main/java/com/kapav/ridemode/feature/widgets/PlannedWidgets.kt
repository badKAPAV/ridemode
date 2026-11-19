package com.kapav.ridemode.feature.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Fill
import com.adamglin.phosphoricons.fill.AddressBook
import com.adamglin.phosphoricons.fill.Bell
import com.adamglin.phosphoricons.fill.Microphone
import com.kapav.ridemode.core.ui.theme.RideUiTokens

/** Presentation-only inputs. Native adapters can supply data without Android services in Compose. */
sealed interface CallsState {
    data object Unavailable : CallsState
    data object Empty : CallsState
    data class Ready(val name: String, val detail: String, val count: Int) : CallsState
}

sealed interface NotificationsState {
    data object NoAccess : NotificationsState
    data object Empty : NotificationsState
    data object Redacted : NotificationsState
    data class Ready(val source: String, val headline: String, val count: Int) : NotificationsState
}

sealed interface AssistantState {
    data object MockIdle : AssistantState
    data object Idle : AssistantState
    data object Listening : AssistantState
    data class Message(val text: String) : AssistantState
    data object Unavailable : AssistantState
}

/** WidgetCard input boundary for later per-instance M2/M3 state mapping. */
data class DeferredWidgetContent(
    val calls: CallsState = CallsState.Unavailable,
    val notifications: NotificationsState = NotificationsState.NoAccess,
    val assistant: AssistantState = AssistantState.MockIdle
)

@Composable
internal fun CallsWidget(modifier: Modifier = Modifier, state: CallsState = CallsState.Unavailable) {
    val summary = when (state) {
        CallsState.Unavailable -> "Recent calls unavailable"
        CallsState.Empty -> "No recent calls"
        is CallsState.Ready -> state.name
    }
    val detail = when (state) {
        CallsState.Unavailable -> "Contacts and calls arrive in M2"
        CallsState.Empty -> "Favorites can appear here"
        is CallsState.Ready -> state.detail
    }
    val status = when (state) {
        CallsState.Unavailable -> "M2 PREVIEW"
        CallsState.Empty -> "NO CALLS"
        is CallsState.Ready -> "${state.count} RECENT"
    }
    PlannedSurface("Calls", status, summary, detail,
        PhosphorIcons.Fill.AddressBook, MaterialTheme.colorScheme.secondaryContainer,
        MaterialTheme.colorScheme.onSecondaryContainer, modifier) {
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(RideUiTokens.plannedWidgetItemGap)) {
            Surface(shape = RoundedCornerShape(RideUiTokens.plannedWidgetAvatarCorner),
                color = MaterialTheme.colorScheme.secondary) {
                Box(Modifier.size(RideUiTokens.plannedWidgetAvatar), contentAlignment = Alignment.Center) {
                    Text(if (state is CallsState.Ready) state.name.take(1).uppercase() else "—",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSecondary)
                }
            }
            WidgetSummary(summary, detail)
        }
    }
}

@Composable
internal fun NotificationsWidget(
    modifier: Modifier = Modifier,
    state: NotificationsState = NotificationsState.NoAccess
) {
    val summary = when (state) {
        NotificationsState.NoAccess -> "Access not granted"
        NotificationsState.Empty -> "All caught up"
        NotificationsState.Redacted -> "Content hidden"
        is NotificationsState.Ready -> state.headline
    }
    val detail = when (state) {
        NotificationsState.NoAccess -> "Grant access in M2 to see a feed"
        NotificationsState.Empty -> "New alerts will appear here"
        NotificationsState.Redacted -> "Private notification"
        is NotificationsState.Ready -> state.source
    }
    val status = when (state) {
        NotificationsState.NoAccess -> "M2 PREVIEW"
        NotificationsState.Empty -> "CLEAR"
        NotificationsState.Redacted -> "PRIVATE"
        is NotificationsState.Ready -> "${state.count} NEW"
    }
    PlannedSurface("Notifications", status, summary, detail,
        PhosphorIcons.Fill.Bell, MaterialTheme.colorScheme.tertiaryContainer,
        MaterialTheme.colorScheme.onTertiaryContainer, modifier) {
        Surface(Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(RideUiTokens.plannedWidgetInsetCorner),
            color = MaterialTheme.colorScheme.surface.copy(alpha = RideUiTokens.plannedWidgetInsetOpacity)) {
            Column(Modifier.padding(RideUiTokens.plannedWidgetItemInset)) {
                Text(detail.uppercase(), style = MaterialTheme.typography.labelSmall,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(summary, style = MaterialTheme.typography.titleMedium,
                    maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
internal fun AssistantWidget(
    modifier: Modifier = Modifier,
    state: AssistantState = AssistantState.MockIdle
) {
    val summary = when (state) {
        AssistantState.MockIdle -> "Ready to help"
        AssistantState.Idle -> "Ready to help"
        AssistantState.Listening -> "Listening"
        is AssistantState.Message -> state.text
        AssistantState.Unavailable -> "Assistant unavailable"
    }
    val detail = when (state) {
        AssistantState.MockIdle -> "Explore mock states from the Ride rail"
        AssistantState.Idle -> "Tap the Ride rail microphone"
        AssistantState.Listening -> "Microphone support arrives in M3"
        is AssistantState.Message -> "Local command preview"
        AssistantState.Unavailable -> "Voice support arrives in M3"
    }
    val status = if (state == AssistantState.MockIdle) "MOCK · M3" else "ASSISTANT"
    PlannedSurface("Assistant", status, summary, detail,
        PhosphorIcons.Fill.Microphone, MaterialTheme.colorScheme.surfaceContainerHigh,
        MaterialTheme.colorScheme.onSurface, modifier) {
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(RideUiTokens.plannedWidgetItemGap)) {
            Surface(shape = RoundedCornerShape(RideUiTokens.plannedWidgetAvatarCorner),
                color = MaterialTheme.colorScheme.secondaryContainer) {
                Box(Modifier.size(RideUiTokens.plannedWidgetAvatar), contentAlignment = Alignment.Center) {
                    Icon(PhosphorIcons.Fill.Microphone, contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(RideUiTokens.iconSize))
                }
            }
            WidgetSummary(summary, detail)
        }
    }
}

@Composable
private fun WidgetSummary(summary: String, detail: String) {
    Column {
        Text(summary, style = MaterialTheme.typography.titleMedium,
            maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(detail, style = MaterialTheme.typography.bodySmall,
            maxLines = 2, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun PlannedSurface(
    title: String,
    status: String,
    summary: String,
    detail: String,
    icon: ImageVector,
    background: Color,
    foreground: Color,
    modifier: Modifier,
    content: @Composable () -> Unit
) {
    BoxWithConstraints(modifier.semantics {
        contentDescription = "$title. $summary. $detail. $status"
    }) {
        val expanded = maxWidth >= RideUiTokens.clockCompactWidth &&
            maxHeight >= RideUiTokens.plannedWidgetExpandedHeight
        Surface(Modifier.fillMaxSize(), color = background,
            shape = RoundedCornerShape(RideUiTokens.widgetCardCorner)) {
            if (maxHeight < RideUiTokens.widgetCompactHeight) {
                Box(Modifier.fillMaxSize().padding(RideUiTokens.widgetPadding),
                    contentAlignment = Alignment.Center) {
                    Text(if (title == "Assistant" && status.startsWith("MOCK")) "Mock assistant"
                        else "$title · $status",
                        style = MaterialTheme.typography.labelSmall, color = foreground,
                        maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            } else if (!expanded) {
                Row(Modifier.fillMaxSize().padding(RideUiTokens.widgetCardInset),
                    horizontalArrangement = Arrangement.spacedBy(RideUiTokens.widgetPadding),
                    verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, contentDescription = null, tint = foreground,
                        modifier = Modifier.size(RideUiTokens.iconSize))
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                        Text(title, style = MaterialTheme.typography.titleSmall, color = foreground,
                            maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(summary, style = MaterialTheme.typography.labelSmall, color = foreground,
                            maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(status, style = MaterialTheme.typography.labelSmall,
                            color = foreground.copy(alpha = RideUiTokens.pageIndicatorCurrentOpacity),
                            maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            } else {
                Column(Modifier.fillMaxSize().padding(RideUiTokens.panelContentPadding),
                    verticalArrangement = Arrangement.spacedBy(RideUiTokens.plannedWidgetItemGap)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                        Text(title, style = MaterialTheme.typography.titleLarge, color = foreground,
                            maxLines = 1, overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f))
                        Text(status, style = MaterialTheme.typography.labelSmall, color = foreground,
                            maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    Spacer(Modifier.weight(1f))
                    content()
                }
            }
        }
    }
}
