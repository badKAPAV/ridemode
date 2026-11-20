package com.kapav.ridemode

import com.google.android.libraries.navigation.Navigator
import com.kapav.ridemode.feature.widgets.MapVoiceMode
import com.kapav.ridemode.feature.widgets.releaseMapNavigation
import com.kapav.ridemode.core.util.confirmationSecondsRemaining
import com.kapav.ridemode.core.util.confirmationFractionRemaining
import org.junit.Assert.*
import org.junit.Test

class MapNavigationCleanupTest {
    @Test fun stopClearsTheRouteBeforeReleasingTheSdk() {
        val calls = mutableListOf<String>()
        assertTrue(releaseMapNavigation(
            { calls += "stop" }, { calls += "clear" }, { calls += "cleanup" }))
        assertEquals(listOf("stop", "clear", "cleanup"), calls)
    }

    @Test fun failuresDoNotSkipTheRemainingCleanupOrClaimSuccess() {
        for (failure in listOf("stop", "clear", "cleanup")) {
            val calls = mutableListOf<String>()
            val errors = mutableListOf<RuntimeException>()
            fun call(step: String) {
                calls += step
                if (step == failure) throw IllegalStateException("SDK failure in $step")
            }
            assertFalse(releaseMapNavigation(
                { call("stop") }, { call("clear") }, { call("cleanup") }, { errors += it }))
            assertEquals(listOf("stop", "clear", "cleanup"), calls)
            assertEquals(1, errors.size)
        }
    }

    @Test fun confirmationExpiresAtFiveSecondsEvenAfterADelayedTick() {
        val deadline = 15_000L
        assertEquals(5, confirmationSecondsRemaining(deadline, 10_000))
        assertEquals(5, confirmationSecondsRemaining(deadline, 10_001))
        assertEquals(1, confirmationSecondsRemaining(deadline, 14_999))
        assertEquals(0, confirmationSecondsRemaining(deadline, 15_000))
        assertEquals(0, confirmationSecondsRemaining(deadline, 20_000))
    }

    @Test fun dismissFillTracksActualDeadlineAndClampsDelayedFrames() {
        assertEquals(1f, confirmationFractionRemaining(15_000, 10_000), 0f)
        assertEquals(1f, confirmationFractionRemaining(15_000, 9_000), 0f)
        assertEquals(0.5f, confirmationFractionRemaining(15_000, 12_500), 0f)
        assertEquals(0f, confirmationFractionRemaining(15_000, 15_000), 0f)
        assertEquals(0f, confirmationFractionRemaining(15_000, 25_000), 0f)
    }

    @Test fun voiceModesKeepMuteSilentAndEnableBluetoothForSpokenModes() {
        assertEquals(Navigator.AudioGuidance.SILENT, MapVoiceMode.MUTE.sdkFlags)
        assertEquals(Navigator.AudioGuidance.VOICE_ALERTS_ONLY or Navigator.AudioGuidance.BLUETOOTH_AUDIO,
            MapVoiceMode.ALERTS.sdkFlags)
        assertEquals(Navigator.AudioGuidance.VOICE_ALERTS_AND_GUIDANCE or Navigator.AudioGuidance.BLUETOOTH_AUDIO,
            MapVoiceMode.ALL.sdkFlags)
    }
}
