package com.kapav.ridemode.core.model

data class WidgetDefinition(
    val id: String,
    val title: String,
    val minWidth: Int,
    val minHeight: Int,
    val defaultWidth: Int,
    val defaultHeight: Int,
    val maxWidth: Int,
    val maxHeight: Int,
    val oledSupported: Boolean = true,
    val landscapeOnly: Boolean = false,
    val oneCellAxis: Boolean = false,
    val fixedShortAxis: Int? = null
)

/** Stable IDs and constraints match config/widget-catalog.json. */
object WidgetRegistry {
    const val DEVICE_WIDTH_MAX = Int.MAX_VALUE
    val all = listOf(
        WidgetDefinition("speedometer", "Speedometer", 1, 1, 3, 3, DEVICE_WIDTH_MAX, 2147483647),
        WidgetDefinition("music", "Music", 5, 4, 8, 4, 12, 12, landscapeOnly = true),
        WidgetDefinition("volume", "Volume", 1, 1, 4, 1, DEVICE_WIDTH_MAX, 24, oneCellAxis = true),
        WidgetDefinition("clock", "Clock and weather", 2, 1, 4, 3, 12, 24),
        WidgetDefinition("sos_contacts", "SOS favorites", 2, 2, 6, 3, 12, 6),
        WidgetDefinition("calls", "Calls", 4, 5, 6, 5, DEVICE_WIDTH_MAX, 24),
        WidgetDefinition("notifications", "Notifications", 4, 5, 6, 5, DEVICE_WIDTH_MAX, 24),
        WidgetDefinition("maps_shortcut", "Maps shortcut", 2, 1, 3, 2, 6, 3),
        WidgetDefinition("google_maps", "Google Maps", 4, 4, 6, 4, DEVICE_WIDTH_MAX, 24),
        WidgetDefinition("turn_guidance", "Turn guidance", 4, 4, 7, 4, 7, 2147483647),
        WidgetDefinition("battery", "Battery", 2, 2, 4, 2, 12, 24, fixedShortAxis = 2),
        WidgetDefinition("assistant", "Assistant", 2, 1, 3, 2, 6, 3)
    )

    val hiddenFromPicker = setOf("maps_shortcut")
    val pickerWidgets: List<WidgetDefinition> get() = all.filterNot { it.id in hiddenFromPicker }

    private val byId = all.associateBy { it.id }
    fun get(id: String): WidgetDefinition? = byId[id]

    /** Swap axes and use the nearest empty placement; rotation never displaces another widget. */
    fun rotatedPlacement(id: String, current: GridRect, others: Iterable<GridRect>,
        maxRows: Int, maxColumns: Int = GridLayout.COLUMNS): GridRect? {
        if (current.width == current.height) return null
        val rotated = current.copy(width = current.height, height = current.width)
        if (!valid(id, rotated.copy(x = 0, y = 0), maxColumns)) return null
        val xLimit = maxColumns - rotated.width
        val yLimit = maxRows - rotated.height
        if (xLimit < 0 || yLimit < 0) return null
        return (0..yLimit).flatMap { y -> (0..xLimit).map { x -> rotated.copy(x = x, y = y) } }
            .sortedWith(compareBy<GridRect> { kotlin.math.abs(it.x - current.x) + kotlin.math.abs(it.y - current.y) }
                .thenBy { it.y }.thenBy { it.x })
            .firstOrNull { GridLayout.issues(it, others, maxRows, maxColumns).isEmpty() }
    }

    /** Preserve a saved widget's size where possible, then find a valid free repair. */
    fun fittedPlacement(id: String, current: GridRect, others: Iterable<GridRect>,
        grid: RideGridConstraints.GridSize): GridRect? {
        val definition = get(id) ?: return null
        val widest = minOf(maxOf(current.width, definition.minWidth), definition.maxWidth, grid.columns)
        val tallest = minOf(maxOf(current.height, definition.minHeight), definition.maxHeight, grid.rows)
        if (widest < definition.minWidth || tallest < definition.minHeight) return null
        val sizes = (definition.minWidth..widest).flatMap { width ->
            (definition.minHeight..tallest).map { height -> width to height }
        }.sortedWith(compareByDescending<Pair<Int, Int>> { it.first * it.second }
            .thenBy { kotlin.math.abs(it.first - current.width) + kotlin.math.abs(it.second - current.height) })
        return sizes.firstNotNullOfOrNull { (width, height) ->
            val xLimit = grid.columns - width
            val yLimit = grid.rows - height
            (0..yLimit).flatMap { y -> (0..xLimit).map { x -> GridRect(x, y, width, height) } }
                .sortedWith(compareBy<GridRect> { kotlin.math.abs(it.x - current.x) + kotlin.math.abs(it.y - current.y) }
                    .thenBy { it.y }.thenBy { it.x })
                .firstOrNull { valid(id, it, grid.columns) &&
                    GridLayout.issues(it, others, grid.rows, grid.columns).isEmpty() }
        }
    }

    fun valid(id: String, rect: GridRect, maxColumns: Int = GridLayout.COLUMNS): Boolean {
        val definition = get(id) ?: return false
        return rect.width in definition.minWidth..definition.maxWidth &&
            rect.height in definition.minHeight..definition.maxHeight &&
            (!definition.landscapeOnly || rect.width >= rect.height) &&
            (!definition.oneCellAxis || (rect.width == 1) != (rect.height == 1)) &&
            (definition.fixedShortAxis == null ||
                ((rect.width == definition.fixedShortAxis && rect.height >= 4) ||
                    (rect.height == definition.fixedShortAxis && rect.width >= 4))) &&
            GridLayout.issues(rect, maxColumns = maxColumns).isEmpty()
    }
}
