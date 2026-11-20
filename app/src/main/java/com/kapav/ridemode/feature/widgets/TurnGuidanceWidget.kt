package com.kapav.ridemode.feature.widgets

import android.os.SystemClock
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Bold
import com.adamglin.phosphoricons.bold.ArrowBendUpLeft
import com.adamglin.phosphoricons.bold.ArrowBendUpRight
import com.adamglin.phosphoricons.bold.ArrowUpLeft
import com.adamglin.phosphoricons.bold.ArrowUpRight
import com.adamglin.phosphoricons.bold.ArrowUp
import com.adamglin.phosphoricons.bold.ArrowUUpLeft
import com.adamglin.phosphoricons.bold.ArrowsClockwise
import com.adamglin.phosphoricons.bold.NavigationArrow
import com.kapav.ridemode.core.ui.components.RideConfirmationDialog
import com.kapav.ridemode.core.ui.components.FadeRideConfirmation
import com.kapav.ridemode.core.ui.theme.RideUiTokens
import com.kapav.ridemode.core.util.CONFIRMATION_TIMEOUT_SECONDS
import com.kapav.ridemode.data.Maneuver
import com.kapav.ridemode.data.TurnGuidanceState
import com.kapav.ridemode.feature.ride.openExternalMaps

@Composable
internal fun TurnGuidanceWidget(state: TurnGuidanceState, editor: Boolean, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val content = remember(state) { turnGuidancePresentation(state) }
    var confirmDeadline by remember { mutableStateOf<Long?>(null) }
    FadeRideConfirmation(confirmDeadline) { deadline, opacity, interactive ->
        RideConfirmationDialog("Open Google Maps?", "", deadline, "Open Maps", PhosphorIcons.Bold.NavigationArrow,
            opacity = opacity, enabled = interactive, onDismiss = { confirmDeadline = null }, onConfirm = {
                confirmDeadline = null
                openExternalMaps(context, allowBrowserFallback = false)
            })
    }
    Surface(onClick = {
        confirmDeadline = SystemClock.elapsedRealtime() + CONFIRMATION_TIMEOUT_SECONDS * 1_000L
    }, enabled = !editor, modifier = modifier.semantics {
        contentDescription = listOfNotNull(content.distance, content.instruction, content.arrivalTime,
            content.details.takeIf { it.isNotEmpty() }?.joinToString(". ")).joinToString(". ")
    }, color = MaterialTheme.colorScheme.surfaceContainerHigh, shape = MaterialTheme.shapes.large) {
        BoxWithConstraints(Modifier.fillMaxSize().padding(RideUiTokens.turnPadding)) {
            val wide = maxWidth > maxHeight * RideUiTokens.turnWideAspect
            val tall = maxHeight > maxWidth * RideUiTokens.turnWideAspect
            val icon = if (content.active) state.maneuver.icon() else PhosphorIcons.Bold.NavigationArrow
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(RideUiTokens.turnGap)) {
                val core = Modifier.fillMaxWidth().weight(1f)
                if (!content.active) Column(core, horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(RideUiTokens.turnGap)) {
                    ManeuverIcon(icon, Modifier.fillMaxWidth().weight(1f))
                    GuidanceInstruction(content.instruction, Modifier.fillMaxWidth().weight(1f), centered = true)
                } else if (wide) Row(core, verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(RideUiTokens.turnGap)) {
                    ManeuverIcon(icon, Modifier.weight(1f).fillMaxHeight())
                    Column(Modifier.weight(RideUiTokens.turnTextWeight).fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(RideUiTokens.turnTextGap)) {
                        content.distance?.let { GuidanceDistance(it, Modifier.fillMaxWidth().weight(1f)) }
                        GuidanceInstruction(content.instruction, Modifier.fillMaxWidth().weight(RideUiTokens.turnTextWeight))
                    }
                } else Column(core, horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(RideUiTokens.turnGap)) {
                    if (tall) {
                        ManeuverIcon(icon, Modifier.fillMaxWidth().weight(RideUiTokens.turnTextWeight))
                        content.distance?.let { GuidanceDistance(it, Modifier.fillMaxWidth().weight(1f), true) }
                    } else Row(Modifier.fillMaxWidth().weight(RideUiTokens.turnTextWeight), verticalAlignment = Alignment.CenterVertically) {
                        ManeuverIcon(icon, Modifier.weight(1f).fillMaxHeight())
                        content.distance?.let { GuidanceDistance(it, Modifier.weight(1f).fillMaxHeight(), true) }
                    }
                    GuidanceInstruction(content.instruction, Modifier.fillMaxWidth().weight(RideUiTokens.turnTextWeight), true)
                }
                if (content.active && content.details.isNotEmpty()) {
                    AdaptiveWidgetText(content.details.joinToString(" · "), MaterialTheme.typography.bodyLarge,
                        MaterialTheme.colorScheme.onSurfaceVariant,
                        Modifier.fillMaxWidth().weight(RideUiTokens.turnDetailsWeight),
                        minSize = RideUiTokens.turnSupportingMinSp.sp, maxSize = RideUiTokens.turnSupportingMaxSp.sp,
                        maxLines = if (tall) 4 else 2, textAlign = if (wide) TextAlign.Start else TextAlign.Center,
                        verticalCentered = true, allowEmergencyShrink = false, overflow = TextOverflow.Ellipsis)
                }
                content.arrivalTime?.let { arrival ->
                    Text(arrival, style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@Composable
private fun ManeuverIcon(icon: ImageVector, modifier: Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.sizeIn(maxWidth = RideUiTokens.turnIconMax, maxHeight = RideUiTokens.turnIconMax).fillMaxSize())
    }
}

@Composable
private fun GuidanceDistance(text: String, modifier: Modifier, centered: Boolean = false) {
    AdaptiveWidgetText(text, MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
        MaterialTheme.colorScheme.primary, modifier, minSize = RideUiTokens.turnDistanceMinSp.sp,
        maxSize = RideUiTokens.turnDistanceMaxSp.sp, maxLines = 1,
        textAlign = if (centered) TextAlign.Center else TextAlign.Start, verticalCentered = true,
        allowEmergencyShrink = false, overflow = TextOverflow.Ellipsis)
}

@Composable
private fun GuidanceInstruction(text: String, modifier: Modifier, centered: Boolean = false) {
    AdaptiveWidgetText(text, MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
        MaterialTheme.colorScheme.onSurface, modifier, minSize = RideUiTokens.turnInstructionMinSp.sp,
        maxSize = RideUiTokens.turnInstructionMaxSp.sp, maxLines = 4,
        textAlign = if (centered) TextAlign.Center else TextAlign.Start, verticalCentered = true,
        allowEmergencyShrink = false, overflow = TextOverflow.Ellipsis)
}

private fun Maneuver?.icon(): ImageVector = when (this) {
    Maneuver.LEFT -> PhosphorIcons.Bold.ArrowBendUpLeft
    Maneuver.RIGHT -> PhosphorIcons.Bold.ArrowBendUpRight
    Maneuver.SLIGHT_LEFT -> PhosphorIcons.Bold.ArrowUpLeft
    Maneuver.SLIGHT_RIGHT -> PhosphorIcons.Bold.ArrowUpRight
    Maneuver.STRAIGHT -> PhosphorIcons.Bold.ArrowUp
    Maneuver.U_TURN -> PhosphorIcons.Bold.ArrowUUpLeft
    Maneuver.ROUNDABOUT -> PhosphorIcons.Bold.ArrowsClockwise
    null -> PhosphorIcons.Bold.NavigationArrow
}
