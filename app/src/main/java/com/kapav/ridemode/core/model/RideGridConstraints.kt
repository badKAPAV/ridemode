package com.kapav.ridemode.core.model

import android.content.Context
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

/** Rows available on this device's landscape Ride canvas, with space reserved for its rail. */
object RideGridConstraints {
    data class GridSize(val columns: Int, val rows: Int)
    const val MIN_ROWS = 6
    const val DEFAULT_ROW_HEIGHT_DP = 52
    val ALLOWED_ROW_HEIGHT_DP = listOf(52, 60, 68)
    const val SCREEN_PADDING_DP = 16
    const val SIDE_PADDING_DP = 40
    const val RAIL_HEIGHT_DP = 56
    const val RAIL_GAP_DP = 12
    const val RIDE_CHROME_DP = SCREEN_PADDING_DP * 2 + RAIL_HEIGHT_DP + RAIL_GAP_DP

    fun size(context: Context, targetCellDp: Int): GridSize {
        val metrics = context.resources.displayMetrics
        val widthDp = max(metrics.widthPixels, metrics.heightPixels) / metrics.density
        val heightDp = min(metrics.widthPixels, metrics.heightPixels) / metrics.density
        return sizeForCanvas(widthDp - SIDE_PADDING_DP * 2,
            heightDp - RIDE_CHROME_DP, targetCellDp)
    }

    /** Keep the Pixel 8a six-row cell size as the floor on larger canvases. */
    internal fun sizeForCanvas(widthDp: Float, heightDp: Float, targetCellDp: Int): GridSize {
        require(targetCellDp > 0)
        if (widthDp <= 0f || heightDp <= 0f) return GridSize(GridLayout.COLUMNS, MIN_ROWS)
        val side = min(max(targetCellDp, DEFAULT_ROW_HEIGHT_DP).toFloat(),
            min(widthDp / GridLayout.COLUMNS, heightDp / MIN_ROWS))
        return GridSize(
            floor(widthDp / side).toInt().coerceAtLeast(GridLayout.COLUMNS),
            floor(heightDp / side).toInt().coerceAtLeast(MIN_ROWS)
        )
    }

    /** Use the same square side in both renderers, even if actual insets differ from metrics. */
    fun cellSideForCanvas(widthDp: Float, heightDp: Float, grid: GridSize): Float {
        require(grid.columns > 0 && grid.rows > 0)
        return min(widthDp / grid.columns, heightDp / grid.rows).coerceAtLeast(0f)
    }
}
