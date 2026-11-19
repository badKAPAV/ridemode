package com.kapav.ridemode.feature.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Fill
import com.adamglin.phosphoricons.fill.NavigationArrow
import com.kapav.ridemode.core.ui.theme.RideUiTokens

/** Filled visual shortcut only. M2 will add the actual Maps handoff. */
@Composable
fun MapsShortcutWidget(modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier.semantics {
        contentDescription = "Mock Open Maps shortcut; action unavailable in M1"
    }) {
        val corner = maxHeight * RideUiTokens.mapsShortcutCornerFraction
        val iconSize = minOf(maxHeight * RideUiTokens.mapsShortcutIconHeightFraction,
            RideUiTokens.mapsShortcutIconMax)
        val textSize = minOf(maxHeight.value * RideUiTokens.mapsShortcutTextHeightFraction,
            RideUiTokens.mapsShortcutTextMaxSp)
            .coerceAtLeast(RideUiTokens.mapsShortcutTextMinSp)
        Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(corner)) {
            Row(Modifier.fillMaxSize().padding(horizontal = RideUiTokens.widgetPadding), horizontalArrangement = Arrangement.spacedBy(
                minOf(RideUiTokens.mapsShortcutGap,
                    maxWidth * RideUiTokens.mapsShortcutGapWidthFraction), Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically) {
                Icon(PhosphorIcons.Fill.NavigationArrow, contentDescription = null,
                    modifier = Modifier.size(iconSize),
                    tint = MaterialTheme.colorScheme.onPrimary)
                Text("Open Maps",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = textSize.sp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
            }
        }
        if (maxHeight >= RideUiTokens.minimumTouchTarget) {
            Text("Mock", modifier = Modifier.align(Alignment.BottomEnd)
                .padding(RideUiTokens.widgetPadding),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = RideUiTokens.mockBadgeOpacity))
        }
    }
}
