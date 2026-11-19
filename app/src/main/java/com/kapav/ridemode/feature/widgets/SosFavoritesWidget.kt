package com.kapav.ridemode.feature.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kapav.ridemode.core.ui.theme.RideUiTokens
import kotlin.math.floor

internal data class SosContactPreview(
    val name: String,
    val initials: String,
    val avatarPainter: Painter? = null
)

private val mockFavorites = listOf(
    SosContactPreview("Biker Mom", "BM"),
    SosContactPreview("Roadside Help", "RH"),
    SosContactPreview("Alex Morgan", "AM"),
    SosContactPreview("Jamie Lee", "JL"),
    SosContactPreview("Taylor Kim", "TK"),
    SosContactPreview("Priya Shah", "PS")
)

/** Number of square shortcuts that can fit without shrinking below the design minimum. */
internal data class SosGrid(val columns: Int, val rows: Int, val visible: Int, val tileDp: Float)

internal fun sosGrid(available: Int, widthDp: Float, heightDp: Float,
    minTileDp: Float, gapDp: Float): SosGrid {
    if (available <= 0 || widthDp <= 0f || heightDp <= 0f) return SosGrid(0, 0, 0, 0f)
    val count = minOf(available, RideUiTokens.sosMaxVisible)
    val effectiveMin = minOf(minTileDp, heightDp)
    val maxColumns = floor((widthDp + gapDp) / (effectiveMin + gapDp)).toInt().coerceAtLeast(1)
    val maxRows = floor((heightDp + gapDp) / (effectiveMin + gapDp)).toInt().coerceAtLeast(1)
    val candidates = (1..minOf(count, maxColumns)).flatMap { columns ->
        (1..minOf(count, maxRows)).map { rows ->
            val visible = minOf(count, columns * rows)
            val side = minOf((widthDp - gapDp * (columns - 1)) / columns,
                (heightDp - gapDp * (rows - 1)) / rows,
                RideUiTokens.sosMaxTileSide.value)
            SosGrid(columns, rows, visible, side)
        }
    }
    return candidates.filter { it.tileDp >= effectiveMin || (it.columns == 1 && it.rows == 1) }
        .maxWithOrNull(compareBy<SosGrid> { it.visible }.thenBy { it.tileDp })
        ?: SosGrid(1, 1, 1, minOf(widthDp, heightDp))
}

/** M1 previews only: images are supported when supplied; initials are the fallback. */
@Composable
internal fun SosFavoritesWidget(
    modifier: Modifier = Modifier,
    contacts: List<SosContactPreview> = mockFavorites
) {
    BoxWithConstraints(modifier) {
        val grid = sosGrid(contacts.size, maxWidth.value, maxHeight.value,
            RideUiTokens.sosMinTileWidth.value, RideUiTokens.sosTileGap.value)
        if (grid.visible == 0) return@BoxWithConstraints
        val side = grid.tileDp.dp
        val shape = RoundedCornerShape(side * RideUiTokens.sosTileCornerFraction)
        val initialsSize = (side.value * RideUiTokens.sosInitialsSizeFraction)
            .coerceIn(RideUiTokens.sosInitialsMinSp, RideUiTokens.sosInitialsMaxSp)
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally) {
            contacts.take(grid.visible).chunked(grid.columns).forEachIndexed { row, group ->
                Row(horizontalArrangement = Arrangement.spacedBy(RideUiTokens.sosTileGap),
                    verticalAlignment = Alignment.CenterVertically) {
                    group.forEachIndexed { column, contact ->
                val index = row * grid.columns + column
                Surface(Modifier.size(side).semantics {
                    contentDescription = "Mock SOS favorite ${contact.name}; action unavailable in M1"
                }, shape = shape, color = MaterialTheme.colorScheme.primary) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        if (contact.avatarPainter != null) {
                            Image(contact.avatarPainter, contentDescription = null,
                                modifier = Modifier.fillMaxSize().clip(shape),
                                contentScale = ContentScale.Crop)
                        } else {
                            AdaptiveWidgetText(contact.initials,
                                style = MaterialTheme.typography.headlineLarge,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.fillMaxSize().padding(RideUiTokens.widgetPadding),
                                maxSize = initialsSize.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        }
                        if (index == 0 && side >= RideUiTokens.sosMockBadgeMinTile) Text("Mock",
                            modifier = Modifier.align(Alignment.BottomEnd)
                                .padding(RideUiTokens.widgetPadding),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(
                                alpha = RideUiTokens.mockBadgeOpacity))
                    }
                }
                    }
                }
            }
        }
    }
}
