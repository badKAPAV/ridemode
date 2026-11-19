package com.kapav.ridemode.core.model

data class GridRect(val x: Int, val y: Int, val width: Int, val height: Int)

enum class GridIssue {
    INVALID_SIZE,
    OUT_OF_BOUNDS,
    OVERLAP
}

/** Geometry shared by the future editor and Ride renderer. Coordinates are integer grid cells. */
object GridLayout {
    const val COLUMNS = 12

    fun issues(
        rect: GridRect,
        others: Iterable<GridRect> = emptyList(),
        maxRows: Int = Int.MAX_VALUE,
        maxColumns: Int = COLUMNS
    ): Set<GridIssue> {
        val result = mutableSetOf<GridIssue>()
        if (rect.width < 1 || rect.height < 1) result += GridIssue.INVALID_SIZE
        if (rect.x < 0 || rect.y < 0 || rect.width > maxColumns ||
            rect.x.toLong() + rect.width > maxColumns ||
            rect.y.toLong() + rect.height > maxRows
        ) result += GridIssue.OUT_OF_BOUNDS
        if (result.isEmpty() && others.any { overlaps(rect, it) }) result += GridIssue.OVERLAP
        return result
    }

    fun clamp(rect: GridRect, maxColumns: Int = COLUMNS): GridRect {
        val width = rect.width.coerceIn(1, maxColumns)
        return GridRect(
            x = rect.x.coerceIn(0, maxColumns - width),
            y = rect.y.coerceAtLeast(0),
            width = width,
            height = rect.height.coerceAtLeast(1)
        )
    }

    fun move(
        rect: GridRect,
        x: Int,
        y: Int,
        others: Iterable<GridRect> = emptyList(),
        maxColumns: Int = COLUMNS
    ): GridRect? = clamp(rect.copy(x = x, y = y), maxColumns)
        .takeIf { issues(it, others, maxColumns = maxColumns).isEmpty() }

    fun resize(
        rect: GridRect,
        width: Int,
        height: Int,
        minWidth: Int,
        minHeight: Int,
        maxWidth: Int,
        maxHeight: Int,
        others: Iterable<GridRect> = emptyList(),
        maxColumns: Int = COLUMNS
    ): GridRect? {
        require(minWidth in 1..maxColumns && maxWidth in minWidth..maxColumns)
        require(minHeight >= 1 && maxHeight >= minHeight)
        val candidate = clamp(
            rect.copy(
                width = width.coerceIn(minWidth, maxWidth),
                height = height.coerceIn(minHeight, maxHeight)
            ), maxColumns
        )
        return candidate.takeIf { issues(it, others, maxColumns = maxColumns).isEmpty() }
    }

    /** Move one widget into an occupied area and place the displaced widget near the vacated area. */
    fun displacedPlacement(
        movingFrom: GridRect,
        movingTo: GridRect,
        displaced: GridRect,
        fixed: List<GridRect>,
        maxRows: Int,
        maxColumns: Int = COLUMNS
    ): GridRect? {
        if (movingFrom.width != movingTo.width || movingFrom.height != movingTo.height ||
            issues(movingTo, fixed, maxRows, maxColumns).isNotEmpty() ||
            issues(movingTo, listOf(displaced), maxRows, maxColumns) != setOf(GridIssue.OVERLAP)) return null
        val xLimit = maxColumns - displaced.width
        val yLimit = maxRows - displaced.height
        if (xLimit < 0 || yLimit < 0) return null
        return (0..yLimit).flatMap { y -> (0..xLimit).map { x -> GridRect(x, y, displaced.width, displaced.height) } }
            .sortedWith(compareBy<GridRect> { kotlin.math.abs(it.x - movingFrom.x) + kotlin.math.abs(it.y - movingFrom.y) }
                .thenBy { it.y }.thenBy { it.x })
            .firstOrNull { issues(it, fixed + movingTo, maxRows, maxColumns).isEmpty() }
    }

    private fun overlaps(a: GridRect, b: GridRect): Boolean =
        a.x.toLong() < b.x.toLong() + b.width &&
            b.x.toLong() < a.x.toLong() + a.width &&
            a.y.toLong() < b.y.toLong() + b.height &&
            b.y.toLong() < a.y.toLong() + a.height
}
