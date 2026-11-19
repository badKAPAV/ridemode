package com.kapav.ridemode

import com.kapav.ridemode.data.WeatherReading
import com.kapav.ridemode.data.WeatherState
import com.kapav.ridemode.data.MAX_DISPLAY_AGE_MS
import com.kapav.ridemode.data.weatherRetryDelay
import com.kapav.ridemode.data.validWeatherCode
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class WeatherPolicyTest {
    @Test fun cachedWeatherExpiresRatherThanShowingStaleConditions() {
        val reading = WeatherReading(12.9, 77.6, 24, 2, 1_000_000L)
        val state = WeatherState(reading)
        assertEquals(reading, state.displayReading(1_000_000L + MAX_DISPLAY_AGE_MS))
        assertNull(state.displayReading(1_000_000L + MAX_DISPLAY_AGE_MS + 1))
        assertNull(state.displayReading(999_999L))
    }

    @Test fun retriesBackOffAndStayCapped() {
        assertEquals(60_000L, weatherRetryDelay(1))
        assertEquals(5 * 60_000L, weatherRetryDelay(2))
        assertEquals(15 * 60_000L, weatherRetryDelay(3))
        assertEquals(30 * 60_000L, weatherRetryDelay(20))
    }

    @Test fun rejectsUnknownConditionRatherThanPresentingInventedWeather() {
        assertTrue(validWeatherCode(95))
        assertFalse(validWeatherCode(4))
        assertFalse(validWeatherCode(-1))
    }
}
