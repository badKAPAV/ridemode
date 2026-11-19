package com.kapav.ridemode

import android.media.session.PlaybackState
import com.kapav.ridemode.data.MusicAction
import com.kapav.ridemode.data.MusicAvailability
import com.kapav.ridemode.data.MusicSessionState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MediaSessionStateTest {
    @Test fun onlyAdvertisedActionsAreAvailableForAnActiveSession() {
        val paused = MusicSessionState(availability = MusicAvailability.READY,
            actions = PlaybackState.ACTION_PLAY or PlaybackState.ACTION_SKIP_TO_NEXT)
        assertTrue(paused.supports(MusicAction.PLAY_PAUSE))
        assertTrue(paused.supports(MusicAction.NEXT))
        assertFalse(paused.supports(MusicAction.PREVIOUS))
        assertFalse(paused.copy(availability = MusicAvailability.ACCESS_REQUIRED)
            .supports(MusicAction.PLAY_PAUSE))

        val playing = paused.copy(playing = true)
        assertFalse(playing.supports(MusicAction.PLAY_PAUSE))
        assertTrue(playing.copy(actions = PlaybackState.ACTION_PAUSE)
            .supports(MusicAction.PLAY_PAUSE))
    }

    @Test fun progressAdvancesWithPlaybackSpeedAndStaysWithinDuration() {
        val playing = MusicSessionState(availability = MusicAvailability.READY,
            durationMs = 10_000, positionMs = 2_000, positionUpdatedAtMs = 1_000,
            speed = 1.5f, playing = true)
        assertEquals(5_000, playing.positionAt(3_000))
        assertEquals(10_000, playing.positionAt(20_000))
        assertEquals(2_000, playing.copy(playing = false).positionAt(20_000))
        assertEquals(0, playing.copy(positionMs = -50).positionAt(1_000))
    }
}
