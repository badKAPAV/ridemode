package com.kapav.ridemode

import com.kapav.ridemode.feature.widgets.batteryContentPlacement
import org.junit.Assert.*
import org.junit.Test

class BatteryContentPlacementTest {
    @Test fun oppositeEndsStayInsideTheirOwnSegmentsWithoutOverlap() {
        for (fill in listOf(0f, 0.01f, 0.1f, 0.49f, 0.5f, 0.51f, 0.9f, 0.99f, 1f)) {
            val p = batteryContentPlacement(200f, fill, 8f, 8f, 124f, 24f)
            val gap = if (fill > 0 && fill < 1) 4f else 0f
            val filledEnd = (200 * fill - gap).coerceAtLeast(0f)
            val emptyStart = (200 * fill + gap).coerceAtMost(200f)
            assertTrue("percentage visible at $fill", p.percentLength > 0)
            assertTrue("status visible at $fill", p.iconLength > 0)
            assertTrue(p.percentStart >= 0 && p.percentStart + p.percentLength <= 200)
            assertTrue(p.iconStart >= 0 && p.iconStart + p.iconLength <= 200)
            if (p.percentOnFill) assertTrue(p.percentStart + p.percentLength <= filledEnd)
            else assertTrue(p.percentStart >= emptyStart)
            if (p.iconOnFill) assertTrue(p.iconStart + p.iconLength <= filledEnd)
            else assertTrue(p.iconStart >= emptyStart)
            assertTrue("labels never overlap at $fill", p.percentStart + p.percentLength <= p.iconStart)
        }
    }

    @Test fun tinySegmentsMoveContentToTheRegionWithRoom() {
        val low = batteryContentPlacement(200f, 0.05f, 8f, 8f, 124f, 24f)
        assertFalse(low.percentOnFill)
        assertFalse(low.iconOnFill)
        val middle = batteryContentPlacement(200f, 0.5f, 8f, 8f, 124f, 24f)
        assertTrue(middle.percentOnFill)
        assertFalse(middle.iconOnFill)
        val full = batteryContentPlacement(200f, 1f, 8f, 8f, 124f, 24f)
        assertTrue(full.percentOnFill)
        assertTrue(full.iconOnFill)
    }

    @Test fun statusSpaceCountsWhenChoosingThePercentageRegion() {
        val p = batteryContentPlacement(200f, 0.49f, 8f, 8f, 124f, 24f)
        assertTrue(p.percentOnFill)
        assertFalse(p.iconOnFill)
    }

    @Test fun unpoweredBarDoesNotReserveAnIconAndReclaimsItsSpace() {
        val powered = batteryContentPlacement(200f, 1f, 8f, 8f, 200f, 24f)
        val unplugged = batteryContentPlacement(200f, 1f, 8f, 8f, 200f, 0f)
        assertEquals(0f, unplugged.iconLength, 0f)
        assertTrue(unplugged.percentLength > powered.percentLength)
    }
}
