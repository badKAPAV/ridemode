package com.kapav.ridemode.feature.ride

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import androidx.lifecycle.ViewModel
import com.kapav.ridemode.feature.widgets.GoogleMapsController
import com.kapav.ridemode.feature.widgets.NavigationSdkSession
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.lang.ref.WeakReference
import javax.inject.Inject

/** Route/controller state survives Activity recreation; all Activity/View bindings are transient. */
@HiltViewModel
class RideNavigationViewModel @Inject constructor(@ApplicationContext private val context: Context,
    private val sdk: NavigationSdkSession) : ViewModel() {
    private val ride = sdk.beginRide()
    private val controllers = mutableMapOf<String, GoogleMapsController>()
    private var activity = WeakReference<Activity>(null)
    private var finished = false
    fun bind(value: Activity) {
        activity = WeakReference(value)
        controllers.values.forEach { it.bindActivity(value) }
    }
    fun unbind(value: Activity) {
        controllers.values.forEach { it.unbindActivity(value) }
        if (activity.get() === value) activity.clear()
    }
    fun controller(id: String): GoogleMapsController = controllers.getOrPut(id) {
        check(!finished)
        GoogleMapsController(context, sdk, ride, id).also { controller -> activity.get()?.let(controller::bindActivity) }
    }
    fun refreshPermissions() = controllers.values.toList().forEach(GoogleMapsController::refreshPermission)
    fun pauseIdleLookups() = controllers.values.toList().forEach(GoogleMapsController::pauseIdleLocationLookup)
    fun saveViewStates() = controllers.values.toList().forEach(GoogleMapsController::saveViewState)
    fun configurationChanged(config: Configuration) = controllers.values.toList().forEach { it.configurationChanged(config) }
    fun trimMemory(level: Int) = controllers.values.toList().forEach { it.trimMemory(level) }
    fun finishRide() {
        if (finished) return
        finished = true
        controllers.values.toList().forEach(GoogleMapsController::finishRide)
        controllers.clear()
        activity.clear()
        sdk.endRide(ride)
    }
    override fun onCleared() { finishRide() }
}
