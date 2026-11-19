package com.kapav.ridemode

import com.kapav.ridemode.data.VolumeState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class VolumeStateTest {
    @Test fun mapsActualStreamRangeAndClampsInput() {
        val state = VolumeState(minimum = 2, maximum = 12, current = 7)
        assertEquals(50, state.percent)
        assertEquals(2, state.levelForFraction(-1f))
        assertEquals(8, state.levelForFraction(0.56f))
        assertEquals(12, state.levelForFraction(2f))
    }

    @Test fun unavailableRangeHasNoFalsePercentage() {
        val state = VolumeState()
        assertFalse(state.available)
        assertEquals(0f, state.fraction)
    }
}
