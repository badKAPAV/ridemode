package com.kapav.ridemode.feature.widgets

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Fill
import com.adamglin.phosphoricons.fill.Microphone
import com.kapav.ridemode.core.ui.theme.RideUiTokens

@Composable
internal fun AssistantWidget(modifier: Modifier = Modifier, editor: Boolean = false) {
    val context = LocalContext.current
    Surface(onClick = { openDeviceAssistant(context) }, enabled = !editor,
        modifier = modifier.semantics { contentDescription = "Open device assistant" },
        shape = MaterialTheme.shapes.large, color = MaterialTheme.colorScheme.surfaceContainerHigh) {
        BoxWithConstraints(Modifier.fillMaxSize().padding(RideUiTokens.widgetPadding)) {
            val compact = maxHeight < RideUiTokens.widgetCompactHeight
            val iconSize = minOf(RideUiTokens.plannedWidgetAvatar,
                if (compact) minOf(maxHeight, RideUiTokens.iconSize) else maxHeight / 3)
            val labelHeight = maxHeight - iconSize - RideUiTokens.widgetPadding
            if (compact) Row(Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(RideUiTokens.widgetPadding, Alignment.CenterHorizontally)) {
                Icon(PhosphorIcons.Fill.Microphone, null, tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(iconSize))
                AdaptiveWidgetText("Open Assistant", MaterialTheme.typography.titleLarge,
                    MaterialTheme.colorScheme.onSurface, Modifier.weight(1f).fillMaxHeight(),
                    maxLines = 2, textAlign = TextAlign.Center, verticalCentered = true)
            } else Column(Modifier.align(Alignment.Center).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(RideUiTokens.widgetPadding, Alignment.CenterVertically)) {
                Icon(PhosphorIcons.Fill.Microphone, null, tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(iconSize))
                AdaptiveWidgetText("Open Assistant", MaterialTheme.typography.titleLarge,
                    MaterialTheme.colorScheme.onSurface,
                    Modifier.fillMaxWidth().heightIn(max = labelHeight),
                    maxLines = 2, textAlign = TextAlign.Center, verticalCentered = true)
            }
        }
    }
}
