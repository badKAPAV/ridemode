package com.kapav.ridemode.feature.widgets

/** Attempt every teardown step, even when an earlier SDK operation fails. */
internal fun releaseMapNavigation(
    stopGuidance: () -> Unit,
    clearDestinations: () -> Unit,
    cleanup: () -> Unit,
    onFailure: (RuntimeException) -> Unit = {}
): Boolean {
    var complete = true
    fun attempt(action: () -> Unit) {
        try { action() } catch (error: RuntimeException) {
            complete = false
            onFailure(error)
        }
    }
    attempt(stopGuidance)
    attempt(clearDestinations)
    attempt(cleanup)
    return complete
}

/** Ending guidance returns to a live idle map; final SDK cleanup is a separate Ride teardown. */
internal fun stopMapNavigation(stopGuidance: () -> Unit, clearDestinations: () -> Unit,
    onFailure: (RuntimeException) -> Unit = {}): Boolean =
    releaseMapNavigation(stopGuidance, clearDestinations, {}, onFailure)
