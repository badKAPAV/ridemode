package com.kapav.ridemode.feature.widgets

/** SDK singleton ownership. Tokens never reset, so old callbacks cannot control a newer route. */
internal class NavigationSessionGate {
    data class Lease(val ride: Long, val request: Long, val widget: String)
    private var sequence = 0L
    var ride: Long? = null
        private set
    private var owner: Lease? = null
    fun beginRide(): Long = (++sequence).also { ride = it; owner = null }
    fun claim(rideId: Long, widget: String): Lease? {
        if (ride != rideId || owner != null) return null
        return Lease(rideId, ++sequence, widget).also { owner = it }
    }
    fun owns(lease: Lease?) = lease != null && ride == lease.ride && owner == lease
    fun release(lease: Lease?) { if (owns(lease)) owner = null }
    fun endRide(rideId: Long): Boolean {
        if (ride != rideId) return false
        ride = null; owner = null
        return true
    }
}

/** Separate the resource lifetime from route leases and from disposable UI bindings. */
internal class RetainedNavigationResource<T : Any>(private val close: (T) -> Unit) {
    val gate = NavigationSessionGate()
    var resource: T? = null
        private set
    private var unownedReady: T? = null
    fun beginRide() = gate.beginRide()
    fun accept(ride: Long, value: T): Boolean {
        if (gate.ride == null) { close(value); return false }
        if (gate.ride != ride) {
            // A stale request cannot become the active cache. Keep it only for final cleanup
            // if the new ride never acquires its own handle to the global SDK.
            if (resource == null) unownedReady = value
            return false
        }
        if (resource == null) resource = value
        unownedReady = null
        return gate.ride == ride && resource === value
    }
    fun endRide(ride: Long) {
        if (!gate.endRide(ride)) return
        val value = resource ?: unownedReady
        resource = null
        unownedReady = null
        if (value != null) close(value)
    }
}
