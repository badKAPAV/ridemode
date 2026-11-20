package com.kapav.ridemode

import com.kapav.ridemode.core.model.*
import com.kapav.ridemode.data.validSpeedSample
import org.junit.Assert.*
import org.junit.Test

class SpeedAndSosPolicyTest {
    @Test fun speedRequiresFreshUsableDataAndPreservesRealZero() {
        assertTrue(validSpeedSample(0f, 0.5f, 0))
        assertTrue(validSpeedSample(12f, null, 5000))
        assertFalse(validSpeedSample(12f, 1f, 5001))
        assertFalse(validSpeedSample(12f, 1f, -1))
        assertFalse(validSpeedSample(null, 1f, 0))
        assertFalse(validSpeedSample(-1f, 1f, 0))
        assertFalse(validSpeedSample(Float.NaN, 1f, 0))
        assertFalse(validSpeedSample(Float.POSITIVE_INFINITY, 1f, 0))
        assertFalse(validSpeedSample(12f, 3.1f, 0))
        assertFalse(validSpeedSample(12f, Float.NaN, 0))
    }
    @Test fun unitsInheritUnlessTileOverrides() {
        assertEquals(SpeedUnit.MPH, SpeedTileConfig().resolvedUnit(SpeedUnit.MPH))
        assertEquals(SpeedUnit.KMH, SpeedTileConfig("kmh").resolvedUnit(SpeedUnit.MPH))
        assertEquals(36.0, 10 * SpeedUnit.KMH.factor, 0.001)
        assertEquals(22.36936, 10 * SpeedUnit.MPH.factor, 0.001)
    }
    @Test fun speedAllowsEveryAxisAndRotation() {
        for (width in 1..30) for (height in 1..24) {
            assertTrue("$width x $height", WidgetRegistry.valid("speedometer", GridRect(0, 0, width, height), 30))
            assertTrue(WidgetRegistry.valid("speedometer", GridRect(0, 0, height, width), 30))
        }
        assertFalse(WidgetRegistry.valid("speedometer", GridRect(0, 0, 31, 1), 30))
    }
    @Test fun contactLimitsRejectDuplicatesAndUnsafeNumbers() {
        val contacts = (1..6).map { SosFavorite("$it", "Person $it", "+12345678$it") }
        assertTrue(validFavorites(contacts))
        assertFalse(validFavorites(contacts + SosFavorite("7", "Extra", "1234567890")))
        assertFalse(validFavorites(contacts + contacts.first()))
        assertFalse(validFavorites(listOf(contacts.first().copy(number = "*123#"))))
        assertFalse(validFavorites(listOf(contacts.first().copy(name = " "))))
        assertFalse(validFavorites(listOf(contacts.first().copy(photo = "x".repeat(32769)))))
        assertEquals("AB", SosFavorite("1", "  Alice   Baker ", "123").initials)
    }
}
