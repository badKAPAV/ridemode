package com.kapav.ridemode.feature.widgets

import com.kapav.ridemode.core.util.CONFIRMATION_TIMEOUT_SECONDS
import com.kapav.ridemode.core.util.confirmationSecondsRemaining
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import java.lang.ref.WeakReference
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.CancellationSignal
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.libraries.navigation.ArrivalEvent
import com.google.android.libraries.navigation.NavigationApi
import com.google.android.libraries.navigation.Navigator
import com.google.android.libraries.navigation.RoutingOptions
import com.google.android.libraries.navigation.Waypoint
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.kapav.ridemode.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class MapSearchField { FROM, TO }

enum class MapStage { IDLE, SEARCH, RESOLVING, PICKING, SELECTED, STARTING, NAVIGATING, STOPPING }

enum class MapVoiceMode(val label: String, val sdkFlags: Int) {
    MUTE("Mute", Navigator.AudioGuidance.SILENT),
    ALL("All", Navigator.AudioGuidance.VOICE_ALERTS_AND_GUIDANCE or Navigator.AudioGuidance.BLUETOOTH_AUDIO),
    ALERTS("Alerts only", Navigator.AudioGuidance.VOICE_ALERTS_ONLY or Navigator.AudioGuidance.BLUETOOTH_AUDIO)
}

data class MapPrediction(val placeId: String, val title: String, val subtitle: String, val location: LatLng? = null)

data class MapRouteProgress(
    val remainingSeconds: Int,
    val remainingMeters: Int,
    val arrivalEpochMillis: Long
)

data class GoogleMapsState(
    val stage: MapStage = MapStage.IDLE,
    val expanded: Boolean = false,
    val hasPreciseLocation: Boolean = false,
    val query: String = "",
    val editingField: MapSearchField? = null,
    val predictions: List<MapPrediction> = emptyList(),
    val searching: Boolean = false,
    val destination: MapPrediction? = null,
    val routeProgress: MapRouteProgress? = null,
    val voiceMode: MapVoiceMode = MapVoiceMode.ALL,
    val stopConfirmationSeconds: Int? = null,
    val stopConfirmationDeadline: Long? = null,
    val message: String? = null,
    val mapRevision: Long = 0,
    val mapUnavailable: Boolean = false
)

/** Ride-retained state for one widget. The current Activity is only weakly bound. */
class GoogleMapsController internal constructor(private val context: Context,
    private val sdk: NavigationSdkSession, private val rideId: Long, private val widgetId: String) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val _state = MutableStateFlow(GoogleMapsState())
    val state = _state.asStateFlow()
    private var placesClient: PlacesClient? = null
    private var searchToken: AutocompleteSessionToken? = null
    private var searchJob: Job? = null
    private var searchCancellation: CancellationTokenSource? = null
    private var searchGeneration = 0
    private var routeGeneration = 0
    private var finished = false
    private var stopConfirmationJob: Job? = null
    private var stopConfirmationDeadlineMillis: Long? = null
    private var navigationView: com.google.android.libraries.navigation.NavigationView? = null
    private var navigator: Navigator? = null
    private var activity = WeakReference<Activity>(null)
    private var lease: NavigationSessionGate.Lease? = null
    private var savedViewState: Bundle? = null
    private var restoringIdle = false
    private var automaticMapRecoveryUsed = false
    private var arrivalListener: Navigator.ArrivalListener? = null
    private var progressListener: Navigator.RemainingTimeOrDistanceChangedListener? = null
    private var map: GoogleMap? = null
    private var mapBottomInset = 0
    private var locationSignal: CancellationSignal? = null
    private var legacyLocationListener: LocationListener? = null
    private var locationTimeout: Runnable? = null
    private var locationGeneration = 0
    private val locationManager = context.getSystemService(LocationManager::class.java)
    private val handler = Handler(Looper.getMainLooper())

    init { refreshPermission() }

    fun bindActivity(value: Activity) { if (!finished) activity = WeakReference(value) }
    fun unbindActivity(value: Activity) { if (activity.get() === value) activity.clear() }
    internal fun takeViewState(): Bundle? = savedViewState.also { savedViewState = null }
    fun saveViewState() { navigationView?.let { view ->
        runCatching { Bundle().also { view.onSaveInstanceState(it) } }.onSuccess { savedViewState = it }
            .onFailure { Log.w("RidemodeMaps", "Could not save map view state", it) }
    } }
    fun configurationChanged(config: Configuration) { navigationView?.let { view ->
        runCatching { view.onConfigurationChanged(config) }.onFailure { Log.w("RidemodeMaps", "Map configuration update failed", it) }
    } }
    fun trimMemory(level: Int) { navigationView?.let { view -> runCatching { view.onTrimMemory(level) } } }
    internal fun isCurrentView(view: com.google.android.libraries.navigation.NavigationView) = !finished && navigationView === view

    fun onNavigationUiChanged(view: com.google.android.libraries.navigation.NavigationView, enabled: Boolean) {
        if (!isCurrentView(view)) return
        // SDK callbacks can be synchronous and repeated. Never rebuild/recenter from this callback.
        handler.post { if (isCurrentView(view) && !enabled) checkNavigationEnded() }
    }

    private fun restoreIdleMap() {
        if (_state.value.stage != MapStage.IDLE || finished || restoringIdle) return
        restoringIdle = true
        try {
            setMapBottomInset(0)
            withMap { it.stopAnimation() }
            // Navigation SDK does not support custom LocationSource. Keep its normal source.
            enableLocationOnMap()
            recenter()
        } finally { restoringIdle = false }
    }

    fun checkNavigationEnded() {
        if (finished) return
        val value = navigator ?: return
        if (_state.value.stage == MapStage.NAVIGATING && sdk.gate.owns(lease)) {
            runCatching { value.isGuidanceRunning }.onSuccess { running -> if (!running) stopNavigation() }
                .onFailure { Log.w("RidemodeMaps", "Could not inspect guidance state", it) }
        }
    }

    fun refreshPermission() {
        if (finished) return
        checkNavigationEnded()
        val granted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
        _state.value = _state.value.copy(hasPreciseLocation = granted,
            message = if (granted && _state.value.message == "Precise location is needed") null else _state.value.message)
        if (granted) {
            enableLocationOnMap()
            if (map != null && _state.value.stage == MapStage.IDLE) recenter()
        } else {
            cancelLocationLookup()
            enableLocationOnMap()
            if (_state.value.stage == MapStage.NAVIGATING ||
                _state.value.stage == MapStage.STARTING) {
                stopNavigation()
                _state.value = _state.value.copy(message = "Precise location is needed")
            }
        }
    }

    fun attachNavigationView(view: com.google.android.libraries.navigation.NavigationView) {
        if (finished) return
        navigationView = view
        updatePresentation(view, _state.value.expanded, _state.value.stage == MapStage.NAVIGATING)
    }

    fun detachNavigationView(view: com.google.android.libraries.navigation.NavigationView) {
        if (navigationView === view) {
            navigationView = null
            map = null
            cancelLocationLookup()
        }
    }

    fun attachMap(view: com.google.android.libraries.navigation.NavigationView, googleMap: GoogleMap) {
        if (!isCurrentView(view)) return
        map = googleMap
        if (_state.value.message == "Reloading map…") _state.value = _state.value.copy(message = null)
        withMap { target ->
            target.setPadding(0, 0, 0, mapBottomInset)
            target.setOnMapClickListener { if (!finished && map === target && !_state.value.expanded) expand() }
        }
        enableLocationOnMap()
        if (_state.value.hasPreciseLocation && _state.value.stage == MapStage.IDLE) recenter()
        if (_state.value.stage == MapStage.NAVIGATING) followNavigation()
    }

    fun detachMap(googleMap: GoogleMap) {
        if (map === googleMap) {
            map = null
            runCatching { googleMap.setOnMapClickListener(null) }
            cancelLocationLookup()
        }
    }

    fun setMapBottomInset(pixels: Int) {
        mapBottomInset = pixels
        withMap { it.setPadding(0, 0, 0, pixels) }
    }

    /** Catch invalid native handles once, detach them and request a fresh view, never loop on them. */
    private fun <T> withMap(action: (GoogleMap) -> T): T? {
        val target = map ?: return null
        if (finished) return null
        try { return action(target) }
        catch (error: RuntimeException) {
            Log.w("RidemodeMaps", "Map operation failed; recreating native view", error)
            if (map === target) {
                map = null
                cancelLocationLookup()
                if (!automaticMapRecoveryUsed) {
                    automaticMapRecoveryUsed = true
                    _state.value = _state.value.copy(mapRevision = _state.value.mapRevision + 1,
                        message = "Reloading map…")
                } else _state.value = _state.value.copy(mapUnavailable = true,
                    message = "Map display unavailable. Reload the map to retry")
            }
            return null
        }
    }

    fun retryMap() {
        if (finished) return
        automaticMapRecoveryUsed = false
        cancelLocationLookup()
        map = null
        _state.value = _state.value.copy(mapRevision = _state.value.mapRevision + 1,
            mapUnavailable = false, message = "Reloading map…")
    }

    internal fun updatePresentation(view: com.google.android.libraries.navigation.NavigationView,
        expanded: Boolean, navigating: Boolean) {
        if (!isCurrentView(view)) return
        try {
            if (view.isNavigationUiEnabled != navigating) view.setNavigationUiEnabled(navigating)
            view.setHeaderEnabled(navigating && expanded)
            view.setRecenterButtonEnabled(navigating && expanded)
        } catch (error: RuntimeException) { Log.w("RidemodeMaps", "Map presentation update failed", error) }
    }

    fun pauseIdleLocationLookup() {
        cancelLocationLookup()
        dismissStopConfirmation()
    }

    fun expand() { _state.value = _state.value.copy(expanded = true) }

    fun openSearch() {
        if (_state.value.stage in listOf(MapStage.STARTING, MapStage.NAVIGATING, MapStage.STOPPING)) return
        cancelSearch()
        cancelLocationLookup()
        searchToken = AutocompleteSessionToken.newInstance()
        _state.value = GoogleMapsState(MapStage.SEARCH, expanded = true,
            hasPreciseLocation = _state.value.hasPreciseLocation, voiceMode = _state.value.voiceMode,
            mapRevision = _state.value.mapRevision, mapUnavailable = _state.value.mapUnavailable)
    }

    fun editField(field: MapSearchField) {
        if (field != MapSearchField.TO || _state.value.stage !in listOf(MapStage.SEARCH, MapStage.SELECTED)) return
        cancelSearch()
        searchToken = AutocompleteSessionToken.newInstance()
        _state.value = _state.value.copy(stage = MapStage.SEARCH, editingField = field,
            query = "", predictions = emptyList(), searching = false, message = null)
    }

    fun closeField() {
        cancelSearch()
        _state.value = _state.value.copy(stage = MapStage.SEARCH, editingField = null,
            query = "", predictions = emptyList(), searching = false, message = null)
    }

    fun back() {
        if (_state.value.stopConfirmationSeconds != null) {
            dismissStopConfirmation()
            return
        }
        if (_state.value.stage == MapStage.PICKING) {
            _state.value = _state.value.copy(stage = MapStage.SEARCH, editingField = null)
        } else if (_state.value.editingField != null) closeField() else collapse()
    }

    fun chooseFromMap() {
        if (_state.value.stage != MapStage.SEARCH || _state.value.editingField == null) return
        cancelSearch()
        _state.value = _state.value.copy(stage = MapStage.PICKING, query = "",
            predictions = emptyList(), searching = false, message = null)
    }

    fun confirmMapPoint() {
        if (_state.value.stage != MapStage.PICKING) return
        val point = withMap { it.cameraPosition.target } ?: run {
            _state.value = _state.value.copy(message = "Map is still loading. Try again")
            return
        }
        val coordinates = java.lang.String.format(java.util.Locale.getDefault(), "%.5f, %.5f", point.latitude, point.longitude)
        completeEndpoint(MapPrediction("", coordinates, "Pinned location", point))
    }

    private fun completeEndpoint(point: MapPrediction) {
        _state.value = _state.value.copy(stage = MapStage.SEARCH, destination = point,
            editingField = null, query = "", predictions = emptyList(), searching = false, message = null)
    }

    fun confirmTrip() {
        if (_state.value.stage != MapStage.SEARCH || _state.value.destination == null) return
        cancelSearch()
        _state.value = _state.value.copy(stage = MapStage.SELECTED, editingField = null, message = null)
    }

    fun editQuery(value: String) {
        if (_state.value.stage != MapStage.SEARCH || _state.value.editingField == null) return
        cancelPendingQuery()
        val generation = ++searchGeneration
        val query = value.take(120)
        _state.value = _state.value.copy(query = query, predictions = emptyList(),
            searching = query.trim().length >= 3, message = null)
        if (query.trim().length < 3) return
        searchJob = scope.launch {
            delay(1_000)
            if (generation != searchGeneration || _state.value.stage != MapStage.SEARCH) return@launch
            val client = placesClient() ?: return@launch
            val cancellation = CancellationTokenSource()
            searchCancellation = cancellation
            val request = FindAutocompletePredictionsRequest.builder()
                .setQuery(query.trim()).setCountries("IN")
                .setSessionToken(searchToken ?: AutocompleteSessionToken.newInstance())
                .setCancellationToken(cancellation.token).build()
            client.findAutocompletePredictions(request)
                .addOnSuccessListener { result ->
                    if (generation == searchGeneration && _state.value.stage == MapStage.SEARCH) {
                        _state.value = _state.value.copy(searching = false,
                            predictions = result.autocompletePredictions.map {
                                MapPrediction(it.placeId, it.getPrimaryText(null).toString(),
                                    it.getSecondaryText(null).toString())
                            }, message = if (result.autocompletePredictions.isEmpty()) "No places found" else null)
                    }
                }.addOnFailureListener { error ->
                    if (generation == searchGeneration && _state.value.stage == MapStage.SEARCH) {
                        _state.value = _state.value.copy(searching = false,
                            message = "Places search unavailable. Check connection and try again")
                    }
                }
        }
    }

    fun retrySearch() {
        if (_state.value.stage == MapStage.SEARCH && _state.value.query.trim().length >= 3)
            editQuery(_state.value.query)
    }

    fun select(prediction: MapPrediction) {
        if (_state.value.stage != MapStage.SEARCH || _state.value.editingField == null) return
        cancelPendingQuery()
        val generation = ++searchGeneration
        val client = placesClient() ?: return
        _state.value = _state.value.copy(stage = MapStage.RESOLVING, searching = true,
            message = null)
        // One minimal Details request closes this endpoint's autocomplete billing session.
        val request = FetchPlaceRequest.builder(prediction.placeId, listOf(Place.Field.LOCATION))
            .setSessionToken(searchToken).build()
        client.fetchPlace(request).addOnSuccessListener { response ->
            if (generation == searchGeneration && _state.value.stage == MapStage.RESOLVING) {
                if (response.place.location != null) {
                    searchToken = null
                    completeEndpoint(prediction.copy(location = response.place.location))
                } else _state.value = _state.value.copy(stage = MapStage.SEARCH,
                    searching = false, message = "Place has no usable location")
            }
        }.addOnFailureListener {
            if (generation == searchGeneration && _state.value.stage == MapStage.RESOLVING) {
                _state.value = _state.value.copy(stage = MapStage.SEARCH,
                    searching = false, message = "Place unavailable. Choose another result")
            }
        }
    }

    fun collapse() {
        dismissStopConfirmation()
        when (_state.value.stage) {
            MapStage.SEARCH, MapStage.RESOLVING, MapStage.PICKING, MapStage.SELECTED -> {
                cancelSearch()
                _state.value = GoogleMapsState(hasPreciseLocation = _state.value.hasPreciseLocation,
                    voiceMode = _state.value.voiceMode, mapRevision = _state.value.mapRevision,
                    mapUnavailable = _state.value.mapUnavailable)
                recenter()
            }
            else -> _state.value = _state.value.copy(expanded = false)
        }
    }

    /** This is the only entry point that may request a Navigation SDK route. */
    fun startNavigation() {
        if (finished || _state.value.destination == null) return
        if (_state.value.stage != MapStage.SELECTED) return
        if (!_state.value.hasPreciseLocation) {
            _state.value = _state.value.copy(message = "Precise location is needed")
            return
        }
        val host = activity.get()?.takeUnless { it.isFinishing || it.isDestroyed } ?: run {
            _state.value = _state.value.copy(message = "Return to Ride to start navigation")
            return
        }
        val claim = sdk.gate.claim(rideId, widgetId) ?: run {
            _state.value = _state.value.copy(message = "Navigation is already running in another map widget")
            return
        }
        lease = claim
        val generation = ++routeGeneration
        _state.value = _state.value.copy(stage = MapStage.STARTING, message = null)
        val ready = sdk.navigator
        navigator = ready
        if (ready != null) requestRoute(ready, generation)
        else try { NavigationApi.getNavigator(host, object : NavigationApi.NavigatorListener {
            override fun onNavigatorReady(value: Navigator) {
                if (!sdk.acceptNavigator(rideId, value) || finished || generation != routeGeneration ||
                    !sdk.gate.owns(claim) || _state.value.stage != MapStage.STARTING) return
                navigator = value
                try {
                    requestRoute(value, generation)
                } catch (error: RuntimeException) {
                    Log.w("RidemodeMaps", "Could not initialize guidance", error)
                    failRoute("Navigation could not start. Try again")
                }
            }
            override fun onError(errorCode: Int) {
                if (finished || generation != routeGeneration || !sdk.gate.owns(claim)) return
                sdk.gate.release(claim)
                lease = null
                val message = when (errorCode) {
                    NavigationApi.ErrorCode.NOT_AUTHORIZED -> "Navigation key is not authorized"
                    NavigationApi.ErrorCode.TERMS_NOT_ACCEPTED -> "Accept navigation terms to continue"
                    NavigationApi.ErrorCode.NETWORK_ERROR -> "Navigation needs a connection"
                    NavigationApi.ErrorCode.LOCATION_PERMISSION_MISSING -> "Precise location is needed"
                    else -> "Navigation unavailable ($errorCode)"
                }
                _state.value = _state.value.copy(stage = MapStage.SELECTED, message = message)
            }
        }) } catch (error: RuntimeException) {
            Log.w("RidemodeMaps", "Could not acquire navigation", error)
            failRoute("Navigation unavailable. Try again")
        }
    }

    private fun requestRoute(value: Navigator, generation: Int) {
        val destination = _state.value.destination ?: return
        val waypoint = try {
            val builder = Waypoint.builder().setTitle(destination.title)
            if (destination.placeId.isNotBlank()) builder.setPlaceIdString(destination.placeId)
            else destination.location?.let { builder.setLatLng(it.latitude, it.longitude) }
            builder.build()
        } catch (_: Waypoint.UnsupportedPlaceIdException) {
            sdk.gate.release(lease); lease = null
            _state.value = _state.value.copy(stage = MapStage.SELECTED, message = "This place cannot be routed")
            return
        }
        val options = RoutingOptions().travelMode(RoutingOptions.TravelMode.TWO_WHEELER)
        val route = try {
            value.setTaskRemovedBehavior(Navigator.TaskRemovedBehavior.QUIT_SERVICE)
            value.setDestination(waypoint, options)
        } catch (error: RuntimeException) {
            Log.w("RidemodeMaps", "Could not request route", error)
            failRoute("Navigation could not start. Try again")
            return
        }
        route.setOnResultListener { status ->
            if (finished || generation != routeGeneration || !sdk.gate.owns(lease) ||
                navigator !== value || _state.value.stage != MapStage.STARTING) return@setOnResultListener
            try {
            if (status == Navigator.RouteStatus.OK) {
                if (arrivalListener == null) {
                    arrivalListener = Navigator.ArrivalListener { event: ArrivalEvent ->
                        if (!finished && navigator === value && sdk.gate.owns(lease) &&
                            _state.value.stage == MapStage.NAVIGATING && event.isFinalDestination) stopNavigation()
                    }.also(value::addArrivalListener)
                }
                val started = runCatching { value.startGuidance() }.isSuccess
                if (started) {
                    navigationView?.let { updatePresentation(it, _state.value.expanded, true) }
                    _state.value = _state.value.copy(stage = MapStage.NAVIGATING, message = null)
                    applyVoiceMode(_state.value.voiceMode)
                    if (progressListener == null) {
                        progressListener = Navigator.RemainingTimeOrDistanceChangedListener {
                            if (navigator === value && _state.value.stage == MapStage.NAVIGATING)
                                updateRouteProgress(value)
                        }.also { value.addRemainingTimeOrDistanceChangedListener(30, 50, it) }
                    }
                    updateRouteProgress(value)
                    followNavigation()
                }
                else {
                    failRoute("Navigation could not start. Check location and try again")
                }
            } else {
                failRoute(when (status) {
                    Navigator.RouteStatus.NO_ROUTE_FOUND -> "No route found for this destination"
                    Navigator.RouteStatus.NETWORK_ERROR -> "Route needs a connection"
                    Navigator.RouteStatus.QUOTA_CHECK_FAILED -> "Navigation quota unavailable"
                    Navigator.RouteStatus.LOCATION_DISABLED, Navigator.RouteStatus.LOCATION_UNKNOWN -> "Location unavailable"
                    else -> "Could not start navigation: $status"
                })
            }
            } catch (error: RuntimeException) {
                Log.w("RidemodeMaps", "Guidance transition failed", error)
                failRoute("Navigation could not start. Try again")
            }
        }
    }

    fun setVoiceMode(mode: MapVoiceMode) {
        if (_state.value.stage != MapStage.NAVIGATING || !_state.value.expanded) return
        applyVoiceMode(mode)
    }

    private fun applyVoiceMode(mode: MapVoiceMode) {
        val value = navigator ?: return
        try {
            value.setAudioGuidance(mode.sdkFlags)
            _state.value = _state.value.copy(voiceMode = mode, message = null)
        } catch (error: RuntimeException) {
            Log.w("RidemodeMaps", "Could not change navigation voice", error)
            _state.value = _state.value.copy(message = "Voice setting could not change. Try again")
        }
    }

    fun requestStopNavigation() {
        if (_state.value.stage != MapStage.NAVIGATING || _state.value.stopConfirmationSeconds != null) return
        val deadline = SystemClock.elapsedRealtime() + CONFIRMATION_TIMEOUT_SECONDS * 1_000L
        stopConfirmationDeadlineMillis = deadline
        _state.value = _state.value.copy(stopConfirmationSeconds = CONFIRMATION_TIMEOUT_SECONDS, stopConfirmationDeadline = deadline)
        stopConfirmationJob = scope.launch {
            while (true) {
                delay(1_000)
                val left = confirmationSecondsRemaining(deadline, SystemClock.elapsedRealtime())
                if (left <= 0) {
                    stopConfirmationDeadlineMillis = null
                    _state.value = _state.value.copy(stopConfirmationSeconds = null, stopConfirmationDeadline = null)
                    break
                }
                _state.value = _state.value.copy(stopConfirmationSeconds = left)
            }
        }
    }

    fun dismissStopConfirmation() {
        stopConfirmationJob?.cancel()
        stopConfirmationJob = null
        stopConfirmationDeadlineMillis = null
        if (_state.value.stopConfirmationSeconds != null)
            _state.value = _state.value.copy(stopConfirmationSeconds = null, stopConfirmationDeadline = null)
    }

    fun confirmStopNavigation() {
        val deadline = stopConfirmationDeadlineMillis ?: return
        if (confirmationSecondsRemaining(deadline, SystemClock.elapsedRealtime()) == 0) {
            dismissStopConfirmation()
            return
        }
        if (_state.value.stage == MapStage.NAVIGATING && _state.value.stopConfirmationSeconds != null)
            stopNavigation()
    }

    private fun removeSessionListeners() {
        val value = navigator
        arrivalListener?.let { listener -> runCatching { value?.removeArrivalListener(listener) } }
        progressListener?.let { listener -> runCatching { value?.removeRemainingTimeOrDistanceChangedListener(listener) } }
        arrivalListener = null
        progressListener = null
    }

    private fun releaseSession(): Boolean {
        removeSessionListeners()
        val value = navigator
        val released = if (value != null && sdk.gate.owns(lease))
            stopMapNavigation(value::stopGuidance, value::clearDestinations) {
                Log.w("RidemodeMaps", "Could not stop guidance", it)
            } else true
        if (released) {
            sdk.gate.release(lease)
            lease = null
            navigator = null
            navigationView?.let { updatePresentation(it, _state.value.expanded, false) }
        }
        // cleanup() belongs to the end of Ride, while idle still needs the SDK map.
        return released
    }

    private fun failRoute(message: String) {
        routeGeneration++
        if (!releaseSession()) {
            _state.value = _state.value.copy(stage = MapStage.STOPPING,
                message = "Navigation could not fully end. Retry ending navigation")
            return
        }
        _state.value = _state.value.copy(stage = MapStage.SELECTED, routeProgress = null, message = message)
    }

    fun stopNavigation() {
        routeGeneration++
        dismissStopConfirmation()
        _state.value = _state.value.copy(stage = MapStage.STOPPING, message = null)
        if (!releaseSession()) {
            _state.value = _state.value.copy(message = "Navigation could not fully end. Retry ending navigation")
            return
        }
        val previous = _state.value
        _state.value = GoogleMapsState(hasPreciseLocation = previous.hasPreciseLocation,
            expanded = previous.expanded, voiceMode = previous.voiceMode, mapRevision = previous.mapRevision, mapUnavailable = previous.mapUnavailable)
        restoreIdleMap()
    }

    fun followNavigation() {
        if (_state.value.stage != MapStage.NAVIGATING || !sdk.gate.owns(lease)) return
        withMap { target ->
            try { target.followMyLocation(GoogleMap.CameraPerspective.TILTED) }
            catch (_: SecurityException) { refreshPermission() }
        }
    }

    private fun updateRouteProgress(value: Navigator) {
        val estimate = runCatching { value.currentTimeAndDistance }.getOrNull() ?: return
        if (estimate.seconds < 0 || estimate.meters < 0) return
        _state.value = _state.value.copy(routeProgress = MapRouteProgress(
            estimate.seconds, estimate.meters,
            System.currentTimeMillis() + estimate.seconds * 1_000L))
    }

    fun recenter() {
        if (!_state.value.hasPreciseLocation || _state.value.stage != MapStage.IDLE) return
        val target = map ?: return
        cancelLocationLookup()
        val generation = locationGeneration
        _state.value = _state.value.copy(message = "Locating you…")
        try {
            val enabled = locationManager.getProviders(true)
            val providers = buildList {
                if (Build.VERSION.SDK_INT >= 31 && LocationManager.FUSED_PROVIDER in enabled)
                    add(LocationManager.FUSED_PROVIDER)
                if (LocationManager.NETWORK_PROVIDER in enabled) add(LocationManager.NETWORK_PROVIDER)
                if (LocationManager.GPS_PROVIDER in enabled) add(LocationManager.GPS_PROVIDER)
            }
            if (providers.isEmpty()) {
                _state.value = _state.value.copy(message = "Turn on device location")
                return
            }
            val now = SystemClock.elapsedRealtimeNanos()
            val cached = providers.mapNotNull { provider ->
                runCatching { locationManager.getLastKnownLocation(provider) }.getOrNull()
            }.filter { fix ->
                (now - fix.elapsedRealtimeNanos) in 0..LOCATION_CACHE_MAX_AGE_NANOS
            }.maxByOrNull(Location::getElapsedRealtimeNanos)
            var hasFix = cached != null
            cached?.let { fix ->
                moveCamera(target, fix)
                if (now - fix.elapsedRealtimeNanos <= LOCATION_CACHE_NO_REFRESH_NANOS) return
                _state.value = _state.value.copy(message = "Updating your location…")
            }
            var activeAttempt = -1
            fun isCurrent(index: Int) = generation == locationGeneration && map === target &&
                _state.value.stage == MapStage.IDLE && activeAttempt == index
            fun tryProvider(index: Int) {
                activeAttempt = index
                clearLocationAttempt()
                if (!isCurrent(index)) return
                if (index >= providers.size) {
                    _state.value = _state.value.copy(message = if (hasFix) "Recent location" else
                        "Location unavailable. Tap recenter to retry")
                    return
                }
                val provider = providers[index]
                val timeout = Runnable { if (isCurrent(index)) tryProvider(index + 1) }
                locationTimeout = timeout
                handler.postDelayed(timeout, LOCATION_ATTEMPT_TIMEOUT_MS)
                try {
                    if (Build.VERSION.SDK_INT >= 30) {
                        val signal = CancellationSignal()
                        locationSignal = signal
                        locationManager.getCurrentLocation(provider, signal, ContextCompat.getMainExecutor(context)) { fix ->
                            if (!isCurrent(index)) return@getCurrentLocation
                            if (fix == null) tryProvider(index + 1)
                            else {
                                clearLocationAttempt()
                                activeAttempt = -1
                                hasFix = true
                                moveCamera(target, fix)
                            }
                        }
                    } else {
                        val listener = object : LocationListener {
                            override fun onProviderEnabled(provider: String) = Unit
                            override fun onProviderDisabled(provider: String) = Unit
                            @Deprecated("Legacy platform callback")
                            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) = Unit
                            override fun onLocationChanged(fix: Location) {
                                if (!isCurrent(index)) return
                                clearLocationAttempt()
                                activeAttempt = -1
                                hasFix = true
                                moveCamera(target, fix)
                            }
                        }
                        legacyLocationListener = listener
                        locationManager.requestSingleUpdate(provider, listener, Looper.getMainLooper())
                    }
                } catch (_: SecurityException) {
                    clearLocationAttempt()
                    _state.value = _state.value.copy(message = "Precise location is needed")
                } catch (_: IllegalArgumentException) {
                    tryProvider(index + 1)
                }
            }
            tryProvider(0)
        } catch (_: SecurityException) {
            _state.value = _state.value.copy(message = "Precise location is needed")
        }
    }

    private fun clearLocationAttempt() {
        locationTimeout?.let(handler::removeCallbacks)
        locationTimeout = null
        locationSignal?.cancel()
        locationSignal = null
        legacyLocationListener?.let { runCatching { locationManager.removeUpdates(it) } }
        legacyLocationListener = null
    }

    private fun cancelLocationLookup() {
        locationGeneration++
        clearLocationAttempt()
    }

    private fun moveCamera(target: GoogleMap, location: Location) {
        if (_state.value.stage != MapStage.IDLE || map !== target) return
        withMap { current -> current.animateCamera(CameraUpdateFactory.newCameraPosition(
            com.google.android.gms.maps.model.CameraPosition.builder()
                .target(LatLng(location.latitude, location.longitude)).zoom(15f).bearing(0f).tilt(0f).build())) }
        if (map === target) _state.value = _state.value.copy(message = null)
    }

    private fun enableLocationOnMap() {
        withMap { target ->
            try {
                target.isMyLocationEnabled = _state.value.hasPreciseLocation
                target.uiSettings.isMyLocationButtonEnabled = false
            } catch (_: SecurityException) {
                cancelLocationLookup()
                _state.value = _state.value.copy(hasPreciseLocation = false, message = "Precise location is needed")
            }
        }
    }

    private fun placesClient(): PlacesClient? {
        if (BuildConfig.PLACES_API_KEY.isBlank()) {
            _state.value = _state.value.copy(searching = false, message = "Places key is missing")
            return null
        }
        return try {
            if (!Places.isInitialized()) Places.initializeWithNewPlacesApiEnabled(
                context.applicationContext, BuildConfig.PLACES_API_KEY)
            placesClient ?: Places.createClient(context.applicationContext).also { placesClient = it }
        } catch (_: Exception) {
            _state.value = _state.value.copy(searching = false, message = "Places unavailable")
            null
        }
    }

    private fun cancelPendingQuery() {
        searchJob?.cancel()
        searchCancellation?.cancel()
        searchCancellation = null
    }

    private fun cancelSearch() {
        searchGeneration++
        cancelPendingQuery()
        searchToken = null
    }

    fun finishRide() {
        if (finished) return
        finished = true
        cancelSearch()
        cancelLocationLookup()
        dismissStopConfirmation()
        routeGeneration++
        removeSessionListeners()
        sdk.gate.release(lease)
        lease = null
        navigator = null
        activity.clear()
        handler.removeCallbacksAndMessages(null)
        map = null
        navigationView = null
        scope.coroutineContext[Job]?.cancel()
    }

    private companion object {
        const val LOCATION_ATTEMPT_TIMEOUT_MS = 8_000L
        const val LOCATION_CACHE_MAX_AGE_NANOS = 120_000_000_000L
        const val LOCATION_CACHE_NO_REFRESH_NANOS = 30_000_000_000L
    }
}
