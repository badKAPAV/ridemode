package com.kapav.ridemode.feature.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextOverflow
import com.kapav.ridemode.core.ui.theme.RideUiTokens
import com.kapav.ridemode.data.NotificationAccess
import com.kapav.ridemode.data.NotificationEntry
import com.kapav.ridemode.data.NotificationFeedState

/** One bounded child scroll area; messages are ordered first by the repository. */
@Composable
fun NotificationFeedWidget(state: NotificationFeedState, privateMode: Boolean,
    modifier: Modifier = Modifier) {
    Surface(modifier, color = MaterialTheme.colorScheme.tertiaryContainer,
        shape = MaterialTheme.shapes.medium) {
        Column(Modifier.fillMaxSize().padding(RideUiTokens.widgetCardInset),
            verticalArrangement = Arrangement.spacedBy(RideUiTokens.plannedWidgetItemGap)) {
            Text(if (privateMode) "Notifications · Private" else "Notifications in da panel",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                maxLines = 1)
            Column(Modifier.fillMaxWidth().weight(1f).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(RideUiTokens.plannedWidgetItemGap)) {
                if (privateMode) {
                    Text("Notifications hidden in Privacy mode",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer)
                } else if (state.entries.isEmpty()) {
                    Text(when (state.access) {
                        NotificationAccess.REQUIRED -> "Allow notification access in Settings"
                        NotificationAccess.CONNECTING -> "Connecting to notifications"
                        NotificationAccess.READY -> "No messages or high-priority alerts"
                    }, style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer)
                } else state.entries.forEach { entry -> NotificationRow(entry) }
            }
        }
    }
}

@Composable
private fun NotificationRow(entry: NotificationEntry) {
    Row(Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(RideUiTokens.plannedWidgetItemGap),
        verticalAlignment = Alignment.Top) {
        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.tertiary) {
            Box(Modifier.size(RideUiTokens.plannedWidgetAvatar), contentAlignment = Alignment.Center) {
                val avatar = entry.avatar
                if (avatar != null) Image(avatar.asImageBitmap(), contentDescription = null,
                    modifier = Modifier.fillMaxSize())
                else Text(entry.sender.firstOrNull()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onTertiary)
            }
        }
        Column(Modifier.weight(1f)) {
            Text(entry.sender, style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(entry.body, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}
