package com.kapav.ridemode

import com.kapav.ridemode.feature.ride.oledContentOpacity
import com.kapav.ridemode.feature.ride.oledWindowBrightness
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OledBrightnessTest {
    @Test fun normalRideNeverOverridesBrightness() {
        for (resumed in listOf(false, true)) for (pip in listOf(false, true)) {
            for (dim in listOf(false, true)) {
                assertEquals(-1f, oledWindowBrightness(false, resumed, pip, dim), 0f)
            }
        }
    }

    @Test fun pausedOrPipCallbacksCannotReapplySaverBrightness() {
        for (dim in listOf(false, true)) {
            assertEquals(-1f, oledWindowBrightness(true, false, false, dim), 0f)
            assertEquals(-1f, oledWindowBrightness(true, false, true, dim), 0f)
            assertEquals(-1f, oledWindowBrightness(true, true, true, dim), 0f)
        }
    }

    @Test fun saverAlwaysDimsAndExtraDimLowersBothWindowAndContent() {
        val baseline = oledWindowBrightness(true, true, false, false)
        val dim = oledWindowBrightness(true, true, false, true)
        assertTrue(baseline > 0f && baseline <= 0.05f)
        assertTrue(dim > 0f && dim < baseline)
        assertTrue(oledContentOpacity(false) in 0f..1f)
        assertTrue(oledContentOpacity(true) > 0f)
        assertTrue(oledContentOpacity(true) < oledContentOpacity(false) / 2)
    }

}
