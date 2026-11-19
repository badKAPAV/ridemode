package com.kapav.ridemode.feature.widgets

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.kapav.ridemode.core.ui.theme.RideUiTokens
import com.kapav.ridemode.data.GuidanceAccess
import com.kapav.ridemode.data.Maneuver
import com.kapav.ridemode.data.TurnGuidanceState

@Composable
internal fun TurnGuidanceWidget(
    state: TurnGuidanceState,
    editor: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val current = state.access == GuidanceAccess.CURRENT
    val title = when (state.access) {
        GuidanceAccess.UNAVAILABLE -> "Allow notification access"
        GuidanceAccess.LISTENING -> "Listening for Maps"
        GuidanceAccess.INACTIVE -> "No active direction"
        GuidanceAccess.STALE -> "Direction expired"
        GuidanceAccess.CURRENT -> state.instruction
    }
    val supporting = when (state.access) {
        GuidanceAccess.UNAVAILABLE -> "Grant access in Settings"
        GuidanceAccess.LISTENING -> "Start navigation in Google Maps"
        GuidanceAccess.INACTIVE -> "Start navigation in Google Maps"
        GuidanceAccess.STALE -> "Open Maps for current guidance"
        GuidanceAccess.CURRENT -> null
    }
    val description = buildString {
        append("Experimental Maps guidance. ")
        append(title)
        if (current) {
            state.distance?.let { append(". In $it") }
            state.eta?.let { append(". ETA $it") }
            state.remaining?.let { append(". $it") }
        }
    }
    BoxWithConstraints(modifier.semantics { contentDescription = description }) {
        val compact = maxWidth < RideUiTokens.turnCompactWidth || maxHeight < RideUiTokens.turnExpandedHeight
        val showFooter = maxHeight >= RideUiTokens.turnFooterMinHeight
        val availableHeight = maxHeight
        val chipDetails = buildList {
            state.distance?.let { add("IN $it") }
            state.eta?.let { add("ETA $it") }
            state.remaining?.let { if (it != state.eta) add(it) }
            state.destination?.let { add("TO $it") }
        }.distinct()
        val secondary = state.details.filterNot { detail ->
            chipDetails.any { it.contains(detail, true) || detail.contains(it, true) }
        }
        Surface(Modifier.fillMaxSize().then(if (editor) Modifier else Modifier.clickable {
            val sent = runCatching { state.openIntent?.send(); state.openIntent != null }.getOrDefault(false)
            if (!sent) {
                val url = Uri.parse("https://www.google.com/maps")
                runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, url)
                    .setPackage("com.google.android.apps.maps")
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
                    .onFailure { runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, url)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) } }
            }
        }), color = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(RideUiTokens.widgetCardCorner)) {
            Column(Modifier.fillMaxSize().padding(RideUiTokens.turnPadding),
                verticalArrangement = Arrangement.spacedBy(RideUiTokens.turnGap)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("MAPS · LIVE CUE", style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = RideUiTokens.turnMutedOpacity),
                        maxLines = 1)
                    Spacer(Modifier.weight(1f))
                    if (!compact) Text("EXPERIMENTAL", style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = RideUiTokens.turnMutedOpacity))
                }
                Row(Modifier.fillMaxWidth().weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(RideUiTokens.turnGap),
                    verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = RoundedCornerShape(RideUiTokens.plannedWidgetInsetCorner),
                        color = MaterialTheme.colorScheme.primary) {
                        Box(Modifier.size(if (compact) RideUiTokens.turnArrowCompact else RideUiTokens.turnArrowExpanded),
                            contentAlignment = Alignment.Center) {
                            Text(if (current) state.maneuver.glyph() else "↗",
                                style = if (compact) MaterialTheme.typography.headlineMedium
                                    else MaterialTheme.typography.displayMedium,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold)
                        }
                    }
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(RideUiTokens.turnTextGap)) {
                        Text(title, style = if (compact) MaterialTheme.typography.titleMedium
                            else MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold,
                            maxLines = if (compact) 2 else 3, overflow = TextOverflow.Ellipsis)
                        if (supporting != null) Text(supporting, style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            maxLines = 2, overflow = TextOverflow.Ellipsis)
                    }
                }
                if (current && showFooter) {
                    val footer = if (compact) chipDetails.take(2) else
                        (chipDetails + secondary).distinct().take(if (availableHeight >= RideUiTokens.turnTallHeight) 8 else 4)
                    if (footer.isNotEmpty()) Text(footer.joinToString("   ·   "),
                        style = if (compact) MaterialTheme.typography.labelMedium else MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        maxLines = if (compact) 1 else if (availableHeight >= RideUiTokens.turnTallHeight) 4 else 2,
                        overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}

private fun Maneuver?.glyph(): String = when (this) {
    Maneuver.LEFT -> "↰"
    Maneuver.RIGHT -> "↱"
    Maneuver.SLIGHT_LEFT -> "↖"
    Maneuver.SLIGHT_RIGHT -> "↗"
    Maneuver.STRAIGHT -> "↑"
    Maneuver.U_TURN -> "↶"
    Maneuver.ROUNDABOUT -> "⟳"
    null -> "↗"
}
