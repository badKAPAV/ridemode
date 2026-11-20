package com.kapav.ridemode

import com.kapav.ridemode.core.model.GridRect
import com.kapav.ridemode.core.model.WidgetRegistry
import com.kapav.ridemode.data.GuidanceAccess
import com.kapav.ridemode.data.Maneuver
import com.kapav.ridemode.data.TurnGuidanceState
import com.kapav.ridemode.feature.widgets.TURN_GUIDANCE_IDLE_PROMPT
import com.kapav.ridemode.feature.widgets.turnGuidancePresentation
import org.junit.Assert.*
import org.junit.Test

class GuidancePresentationTest {
    @Test fun unavailableAndExpiredDataCannotLeakOldInstructionsOrMetrics() {
        GuidanceAccess.entries.filter { it != GuidanceAccess.CURRENT }.forEach { access ->
            val result = turnGuidancePresentation(TurnGuidanceState(access, "Turn left", Maneuver.LEFT,
                distance = "200 m", eta = "9:00", details = listOf("Old route")))
            assertFalse(result.active)
            assertEquals(TURN_GUIDANCE_IDLE_PROMPT, result.instruction)
            assertNull(result.arrivalTime)
            assertNull(result.distance)
            assertTrue(result.details.isEmpty())
        }
    }
    @Test fun currentDirectionPreservesSourceAndDoesNotInventMissingMetrics() {
        val result = turnGuidancePresentation(TurnGuidanceState(GuidanceAccess.CURRENT,
            "Turn right onto MG Road", Maneuver.RIGHT))
        assertTrue(result.active)
        assertEquals("Turn right onto MG Road", result.instruction)
        assertNull(result.distance)
        assertTrue(result.details.isEmpty())
        assertFalse(turnGuidancePresentation(TurnGuidanceState(GuidanceAccess.CURRENT, "Turn right")).active)
    }
    @Test fun duplicateDetailsCollapseWithoutChangingNotificationContent() {
        val result = turnGuidancePresentation(TurnGuidanceState(GuidanceAccess.CURRENT,
            "Turn left", Maneuver.LEFT, "200 m", "9:00", "10 min remaining", details =
            listOf("Turn left", "200 m", "9:00", "9:00 · 10 min remaining", "9:00 · 10 min remaining")))
        assertEquals("200 m", result.distance)
        assertEquals("9:00", result.arrivalTime)
        assertEquals(listOf("10 min remaining"), result.details)
    }
    @Test fun distinctNumbersAreNotMistakenForDuplicateDetails() {
        val result = turnGuidancePresentation(TurnGuidanceState(GuidanceAccess.CURRENT,
            "Turn left", Maneuver.LEFT, eta = "9:00", remaining = "24 min remaining",
            details = listOf("19:00 arrival", "4 min")))
        assertEquals("9:00", result.arrivalTime)
        assertEquals(listOf("24 min remaining", "19:00 arrival", "4 min"), result.details)
    }

    @Test fun arrivalIsSeparatedFromCombinedDetailsAndBlankArrivalIsOmitted() {
        val state = TurnGuidanceState(GuidanceAccess.CURRENT, "Turn left", Maneuver.LEFT,
            eta = " 9:00 ", details = listOf("ETA: 9:00 · 10 min remaining | Destination"))
        val result = turnGuidancePresentation(state)
        assertEquals("9:00", result.arrivalTime)
        assertEquals(listOf("10 min remaining", "Destination"), result.details)
        assertNull(turnGuidancePresentation(state.copy(eta = " ", details = emptyList())).arrivalTime)
    }

    @Test fun guidanceAllowsOnlyFourToSevenColumnsAndDeviceBoundedTallShapes() {
        for (width in 4..7) for (height in listOf(4, 5, 6, 24, 25, 100))
            assertTrue(WidgetRegistry.valid("turn_guidance", GridRect(0, 0, width, height)))
        assertFalse(WidgetRegistry.valid("turn_guidance", GridRect(0, 0, 3, 4)))
        assertFalse(WidgetRegistry.valid("turn_guidance", GridRect(0, 0, 4, 3)))
        assertFalse(WidgetRegistry.valid("turn_guidance", GridRect(0, 0, 8, 4)))
    }
    @Test fun hiddenMapsTileRemainsRegisteredForSavedLayouts() {
        assertNotNull(WidgetRegistry.get("maps_shortcut"))
        assertFalse(WidgetRegistry.pickerWidgets.any { it.id == "maps_shortcut" })
        assertTrue(WidgetRegistry.pickerWidgets.any { it.id == "google_maps" })
        assertTrue(WidgetRegistry.pickerWidgets.any { it.id == "turn_guidance" })
    }
}
