package com.kapav.ridemode

import com.kapav.ridemode.feature.widgets.RetainedNavigationResource
import com.kapav.ridemode.feature.widgets.NavigationSessionGate
import com.kapav.ridemode.feature.widgets.stopMapNavigation
import org.junit.Assert.*
import org.junit.Test

class NavigationSessionLifecycleTest {
    @Test fun onlyOneWidgetMayOwnGlobalGuidance() {
        val gate = NavigationSessionGate()
        val ride = gate.beginRide()
        val first = gate.claim(ride, "one")!!
        assertNull(gate.claim(ride, "two"))
        gate.release(first)
        val second = gate.claim(ride, "two")!!
        gate.release(first)
        assertTrue(gate.owns(second))
        assertFalse(gate.owns(first))
    }
    @Test fun oldActivityCallbacksCannotEndANewerRide() {
        val gate = NavigationSessionGate()
        val oldRide = gate.beginRide()
        val old = gate.claim(oldRide, "same-widget")!!
        val newRide = gate.beginRide()
        val current = gate.claim(newRide, "same-widget")!!
        assertFalse(gate.endRide(oldRide))
        gate.release(old)
        assertTrue(gate.owns(current))
        assertFalse(gate.owns(old))
        assertTrue(gate.endRide(newRide))
        assertFalse(gate.owns(current))
        assertNull(gate.claim(newRide, "same-widget"))
    }
    @Test fun endingGuidanceLeavesSdkAvailableForIdleAndFinalCleanupHappensOnce() {
        val calls = mutableListOf<String>()
        val value = calls
        val sdk = resourceOwner()
        val ride = sdk.beginRide()
        assertTrue(sdk.accept(ride, value))
        assertTrue(stopMapNavigation({ value += "stopGuidance" }, { value += "clearDestinations" }))
        assertEquals(listOf("stopGuidance", "clearDestinations"), calls)
        assertSame(value, sdk.resource)
        sdk.endRide(ride)
        sdk.endRide(ride)
        assertEquals(listOf("stopGuidance", "clearDestinations", "stopGuidance", "clearDestinations", "cleanup"), calls)
        assertNull(sdk.resource)
    }
    @Test fun aLateNavigatorCallbackNeverCleansUpANewerRide() {
        val sdk = resourceOwner()
        val oldRide = sdk.beginRide()
        val oldCalls = mutableListOf<String>()
        val old = oldCalls
        sdk.endRide(oldRide)
        val newRide = sdk.beginRide()
        val currentCalls = mutableListOf<String>()
        val current = currentCalls
        assertTrue(sdk.accept(newRide, current))
        assertFalse(sdk.accept(oldRide, old))
        assertSame(current, sdk.resource)
        assertTrue(oldCalls.isEmpty())
        assertTrue(currentCalls.isEmpty())
    }
    @Test fun staleReadyBeforeNewReadyCannotPoisonTheNewRidesCache() {
        val sdk = resourceOwner()
        val oldRide = sdk.beginRide()
        sdk.endRide(oldRide)
        val newRide = sdk.beginRide()
        val old = mutableListOf<String>()
        val current = mutableListOf<String>()
        assertFalse(sdk.accept(oldRide, old))
        assertNull(sdk.resource)
        assertTrue(sdk.accept(newRide, current))
        assertSame(current, sdk.resource)
        sdk.endRide(newRide)
        assertTrue(old.isEmpty())
        assertEquals(listOf("stopGuidance", "clearDestinations", "cleanup"), current)
    }

    @Test fun aLateReadyAfterRideEndIsReleasedAndStopAttemptsBothSteps() {
        val sdk = resourceOwner()
        val ride = sdk.beginRide()
        sdk.endRide(ride)
        val calls = mutableListOf<String>()
        assertFalse(sdk.accept(ride, calls))
        assertEquals(listOf("stopGuidance", "clearDestinations", "cleanup"), calls)
        calls.clear()
        assertFalse(stopMapNavigation({ calls += "stop"; throw IllegalStateException() }, { calls += "clear" }))
        assertEquals(listOf("stop", "clear"), calls)
    }
    private fun resourceOwner() = RetainedNavigationResource<MutableList<String>> {
        it += listOf("stopGuidance", "clearDestinations", "cleanup")
    }
}
