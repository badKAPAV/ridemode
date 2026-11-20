package com.kapav.ridemode.feature.widgets

import android.util.Log
import com.google.android.libraries.navigation.Navigator
import javax.inject.Inject
import javax.inject.Singleton

/** Application-wide SDK owner; never retains an Activity or native View. All calls are on main. */
@Singleton
class NavigationSdkSession @Inject constructor() {
    private val retained = RetainedNavigationResource<Navigator> { value ->
        releaseMapNavigation(value::stopGuidance, value::clearDestinations, value::cleanup) {
            Log.w("RidemodeMaps", "Final SDK cleanup failed", it)
        }
    }
    internal val gate get() = retained.gate
    internal val navigator get() = retained.resource
    internal fun beginRide() = retained.beginRide()
    // SDK acquisition is global: late callbacks must not tear down a newer ride's resource.
    internal fun acceptNavigator(ride: Long, value: Navigator) = retained.accept(ride, value)
    internal fun endRide(ride: Long) = retained.endRide(ride)
    fun endCurrentRide() { gate.ride?.let(::endRide) }
}
