package com.kapav.ridemode

import com.kapav.ridemode.core.model.GridIssue
import com.kapav.ridemode.core.model.GridLayout
import com.kapav.ridemode.core.model.GridRect
import com.kapav.ridemode.core.model.RideGridConstraints
import com.kapav.ridemode.core.model.WidgetRegistry
import com.kapav.ridemode.feature.widgets.sosGrid
import com.kapav.ridemode.feature.widgets.batteryTextOnFill
import com.kapav.ridemode.core.ui.theme.RideUiTokens
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GridLayoutTest {
    @Test fun deviceGridKeepsSixReferenceRowsAndStableSquareSizeOnLargerScreens() {
        assertEquals(12, GridLayout.COLUMNS)
        assertEquals(RideGridConstraints.GridSize(16, 6),
            RideGridConstraints.sizeForCanvas(834f, 311.4f, 52))
        assertEquals(RideGridConstraints.GridSize(15, 8),
            RideGridConstraints.sizeForCanvas(800f, 420f, 52))
        assertEquals(RideGridConstraints.GridSize(12, 9),
            RideGridConstraints.sizeForCanvas(400f, 300f, 52))
        for (width in 360..1200 step 37) for (height in 120..800 step 29) {
            val grid = RideGridConstraints.sizeForCanvas(width.toFloat(), height.toFloat(), 52)
            val side = RideGridConstraints.cellSideForCanvas(width.toFloat(), height.toFloat(), grid)
            assertTrue(grid.columns >= 12 && grid.rows >= 6)
            assertTrue(side * grid.columns <= width + 0.01f)
            assertTrue(side * grid.rows <= height + 0.01f)
            if (width >= 624 && height >= 312) assertTrue(side >= 52f - 0.01f)
            assertTrue((grid.columns + 1) * side > width - 1f ||
                (grid.rows + 1) * side > height - 1f)
        }
    }

    @Test fun overflowedSavedWidgetShrinksOrMovesOnlyWithinValidFreeCells() {
        val grid = RideGridConstraints.GridSize(16, 6)
        assertEquals(GridRect(0, 0, 1, 6), WidgetRegistry.fittedPlacement(
            "volume", GridRect(0, 0, 1, 7), emptyList(), grid))
        assertEquals(GridRect(0, 3, 4, 3), WidgetRegistry.fittedPlacement(
            "clock", GridRect(0, 5, 4, 3), emptyList(), grid))
        assertEquals(GridRect(0, 0, 4, 5), WidgetRegistry.fittedPlacement(
            "calls", GridRect(0, 0, 3, 2), emptyList(), grid))
        assertNull(WidgetRegistry.fittedPlacement("clock", GridRect(0, 5, 4, 3),
            listOf(GridRect(0, 0, 16, 6)), grid))
    }

    @Test fun rotateUsesNearestEmptyValidPlacementWithoutDisplacingWidgets() {
        val volume = GridRect(0, 0, 4, 1)
        assertEquals(GridRect(0, 0, 1, 4),
            WidgetRegistry.rotatedPlacement("volume", volume, emptyList(), 5))
        assertEquals(GridRect(1, 0, 1, 4),
            WidgetRegistry.rotatedPlacement("volume", volume, listOf(GridRect(0, 1, 1, 3)), 5))
        assertEquals(GridRect(8, 0, 2, 4),
            WidgetRegistry.rotatedPlacement("battery", GridRect(8, 0, 4, 2), emptyList(), 5))
        assertEquals(GridRect(16, 0, 1, 4),
            WidgetRegistry.rotatedPlacement("volume", GridRect(16, 0, 4, 1),
                emptyList(), 7, 20))
        assertNull(WidgetRegistry.rotatedPlacement("battery", GridRect(8, 0, 4, 2), emptyList(), 3))
        assertNull(WidgetRegistry.rotatedPlacement("music", GridRect(0, 0, 8, 4), emptyList(), 10))
        assertNull(WidgetRegistry.rotatedPlacement("clock", GridRect(0, 0, 4, 4), emptyList(), 10))
    }

    @Test fun boundaryAndSizeChecks() {
        assertEquals(setOf(GridIssue.INVALID_SIZE), GridLayout.issues(GridRect(0, 0, 0, 2)))
        assertTrue(GridIssue.OUT_OF_BOUNDS in GridLayout.issues(GridRect(11, 0, 2, 2)))
        assertTrue(GridIssue.OUT_OF_BOUNDS in GridLayout.issues(GridRect(0, -1, 2, 2)))
        assertEquals(emptySet<GridIssue>(), GridLayout.issues(GridRect(0, 0, 12, 1)))
        assertTrue(GridIssue.OUT_OF_BOUNDS in GridLayout.issues(GridRect(17, 0, 3, 2)))
        assertEquals(emptySet<GridIssue>(), GridLayout.issues(GridRect(17, 0, 3, 2),
            maxRows = 7, maxColumns = 20))
        assertTrue(WidgetRegistry.valid("clock", GridRect(17, 0, 3, 2), 20))
        assertTrue(WidgetRegistry.valid("calls", GridRect(0, 0, 20, 5), 20))
        assertTrue(WidgetRegistry.valid("notifications", GridRect(0, 0, 20, 5), 20))
        assertTrue(WidgetRegistry.valid("volume", GridRect(0, 0, 20, 1), 20))
    }

    @Test fun adjacentTilesDoNotCollideButOverlappingMoveDoes() {
        val left = GridRect(0, 0, 4, 2)
        val right = GridRect(4, 0, 4, 2)
        assertEquals(emptySet<GridIssue>(), GridLayout.issues(right, listOf(left)))
        assertNull(GridLayout.move(right, 3, 0, listOf(left)))
        assertEquals(GridRect(8, 0, 4, 2), GridLayout.move(right, 20, -3, listOf(left)))
    }

    @Test fun resizeHonorsCatalogConstraintsAndRejectsCollision() {
        val tile = GridRect(8, 1, 3, 2)
        assertEquals(
            GridRect(7, 1, 5, 3),
            GridLayout.resize(tile, 9, 3, 2, 1, 5, 4)
        )
        assertNull(GridLayout.resize(tile, 4, 2, 2, 1, 5, 4, listOf(GridRect(11, 1, 1, 2))))
    }

    @Test fun viewportRowBoundaryRejectsOverflow() {
        assertTrue(GridIssue.OUT_OF_BOUNDS in GridLayout.issues(GridRect(0, 5, 4, 2), maxRows = 6))
        assertEquals(emptySet<GridIssue>(), GridLayout.issues(GridRect(0, 4, 4, 2), maxRows = 6))
    }

    @Test fun musicMinimumAndFlexibleVolumeStayInsideGrid() {
        assertTrue(WidgetRegistry.valid("music", GridRect(0, 0, 6, 4)))
        assertTrue(WidgetRegistry.valid("music", GridRect(0, 0, 5, 4)))
        assertTrue(!WidgetRegistry.valid("music", GridRect(0, 0, 6, 3)))
        assertTrue(WidgetRegistry.valid("music", GridRect(0, 0, 6, 6)))
        assertTrue(WidgetRegistry.valid("music", GridRect(0, 0, 7, 6)))
        assertTrue(WidgetRegistry.valid("music", GridRect(0, 0, 8, 7)))
        assertTrue(WidgetRegistry.valid("music", GridRect(0, 0, 7, 7)))
        assertTrue(!WidgetRegistry.valid("music", GridRect(0, 0, 12, 24)))
        assertTrue(WidgetRegistry.valid("music", GridRect(0, 0, 12, 12)))
        assertTrue(WidgetRegistry.valid("volume", GridRect(0, 0, 12, 1)))
        assertTrue(WidgetRegistry.valid("volume", GridRect(0, 0, 1, 12)))
        assertTrue(!WidgetRegistry.valid("volume", GridRect(0, 0, 1, 1)))
        assertTrue(!WidgetRegistry.valid("volume", GridRect(0, 0, 4, 2)))
        assertTrue(GridIssue.OUT_OF_BOUNDS in GridLayout.issues(GridRect(0, 0, 1, 12), maxRows = 6))
    }

    @Test fun clockRequiresRoomForTimeDateAndWeather() {
        assertNull(WidgetRegistry.get("weather"))
        assertTrue(WidgetRegistry.valid("clock", GridRect(0, 0, 2, 2)))
        assertTrue(WidgetRegistry.valid("clock", GridRect(0, 0, 12, 24)))
        assertTrue(!WidgetRegistry.valid("clock", GridRect(0, 0, 2, 1)))
    }

    @Test fun callsAndNotificationsNeedScrollableListAreaButMayFillViewport() {
        listOf("calls", "notifications").forEach { type ->
            assertTrue(WidgetRegistry.valid(type, GridRect(0, 0, 4, 5)))
            assertTrue(WidgetRegistry.valid(type, GridRect(0, 0, 12, 24)))
            assertTrue(!WidgetRegistry.valid(type, GridRect(0, 0, 3, 5)))
            assertTrue(!WidgetRegistry.valid(type, GridRect(0, 0, 4, 4)))
            assertTrue(GridIssue.OUT_OF_BOUNDS in GridLayout.issues(
                GridRect(0, 0, 4, 9), maxRows = 8))
        }
    }

    @Test fun batteryRequiresExactlyTwoCellsOnOneAxisAndFourOnTheOther() {
        assertTrue(WidgetRegistry.valid("battery", GridRect(0, 0, 4, 2)))
        assertTrue(WidgetRegistry.valid("battery", GridRect(0, 0, 2, 4)))
        assertTrue(WidgetRegistry.valid("battery", GridRect(0, 0, 2, 24)))
        assertTrue(!WidgetRegistry.valid("battery", GridRect(0, 0, 3, 2)))
        assertTrue(!WidgetRegistry.valid("battery", GridRect(0, 0, 2, 3)))
        assertTrue(!WidgetRegistry.valid("battery", GridRect(0, 0, 4, 3)))
    }

    @Test fun sosGridUsesBothDimensionsAndContactCount() {
        val min = RideUiTokens.sosMinTileWidth.value
        val gap = RideUiTokens.sosTileGap.value
        assertEquals(0, sosGrid(0, 840f, 240f, min, gap).visible)
        assertEquals(1, sosGrid(6, 140f, 120f, min, gap).visible)
        assertEquals(4, sosGrid(6, 420f, 120f, min, gap).visible)
        assertEquals(6, sosGrid(6, 420f, 240f, min, gap).visible)
        assertEquals(2, sosGrid(2, 840f, 240f, min, gap).visible)
        assertEquals(2, sosGrid(6, 420f, 240f, min, gap).rows)
    }

    @Test fun batteryTextMovesToSegmentWithRoom() {
        assertTrue(!batteryTextOnFill(20f, 180f, 100f))
        assertTrue(batteryTextOnFill(126f, 74f, 100f))
        assertTrue(batteryTextOnFill(100f, 100f, 110f))
    }

    @Test fun displacedWidgetUsesVacatedSpaceAndNeverMovesAThirdWidget() {
        val source = GridRect(0, 0, 4, 2)
        val target = GridRect(4, 0, 4, 2)
        assertEquals(source, GridLayout.displacedPlacement(source, target, target, emptyList(), 4))
        assertNull(GridLayout.displacedPlacement(source, target, target,
            listOf(GridRect(0, 0, 4, 4), GridRect(8, 0, 4, 4), GridRect(4, 2, 4, 2)), 4))
        assertNull(GridLayout.displacedPlacement(source, target.copy(width = 5), target, emptyList(), 4))
    }
}
