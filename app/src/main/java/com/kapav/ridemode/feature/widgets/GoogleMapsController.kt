package com.kapav.ridemode.feature.widgets

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.CancellationSignal
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
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

enum class MapStage { IDLE, SEARCH, RESOLVING, SELECTED, STARTING, NAVIGATING }

data class MapPrediction(val placeId: String, val title: String, val subtitle: String)

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
    val predictions: List<MapPrediction> = emptyList(),
    val searching: Boolean = false,
    val destination: MapPrediction? = null,
    val routeProgress: MapRouteProgress? = null,
    val message: String? = null
)

/** Activity-owned state for one Maps widget. No other widget observes or modifies it. */
class GoogleMapsController(private val activity: Activity) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val _state = MutableStateFlow(GoogleMapsState())
    val state = _state.asStateFlow()
    private var placesClient: PlacesClient? = null
    private var searchToken: AutocompleteSessionToken? = null
    private var searchJob: Job? = null
    private var searchCancellation: CancellationTokenSource? = null
    private var searchGeneration = 0
    private var routeGeneration = 0
    private var navigator: Navigator? = null
    private var arrivalListener: Navigator.ArrivalListener? = null
    private var progressListener: Navigator.RemainingTimeOrDistanceChangedListener? = null
    private var map: GoogleMap? = null
    private var locationSignal: CancellationSignal? = null
    private var legacyLocationListener: LocationListener? = null
    private var locationTimeout: Runnable? = null
    private var locationGeneration = 0
    private val locationManager = activity.getSystemService(LocationManager::class.java)
    private val handler = Handler(Looper.getMainLooper())

    init { refreshPermission() }

    fun refreshPermission() {
        val granted = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) ==
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

    fun attachMap(googleMap: GoogleMap) {
        map = googleMap
        googleMap.setOnMapClickListener { if (!_state.value.expanded) expand() }
        enableLocationOnMap()
        if (_state.value.hasPreciseLocation && _state.value.stage == MapStage.IDLE) recenter()
        if (_state.value.stage == MapStage.NAVIGATING) followNavigation()
    }

    fun detachMap(googleMap: GoogleMap) {
        if (map === googleMap) {
            googleMap.setOnMapClickListener(null)
            map = null
            cancelLocationLookup()
        }
    }

    fun pauseIdleLocationLookup() {
        cancelLocationLookup()
    }

    fun expand() { _state.value = _state.value.copy(expanded = true) }

    fun openSearch() {
        if (_state.value.stage == MapStage.STARTING || _state.value.stage == MapStage.NAVIGATING) return
        cancelSearch()
        searchToken = AutocompleteSessionToken.newInstance()
        _state.value = GoogleMapsState(MapStage.SEARCH, expanded = true,
            hasPreciseLocation = _state.value.hasPreciseLocation)
    }

    fun editQuery(value: String) {
        if (_state.value.stage != MapStage.SEARCH) return
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
        if (_state.value.stage != MapStage.SEARCH) return
        cancelPendingQuery()
        val generation = ++searchGeneration
        val client = placesClient() ?: return
        _state.value = _state.value.copy(stage = MapStage.RESOLVING, searching = true,
            message = null)
        // One minimal Details request closes the autocomplete billing session.
        val request = FetchPlaceRequest.builder(prediction.placeId, listOf(Place.Field.LOCATION))
            .setSessionToken(searchToken).build()
        client.fetchPlace(request).addOnSuccessListener { response ->
            if (generation == searchGeneration && _state.value.stage == MapStage.RESOLVING) {
                if (response.place.location != null) {
                    searchToken = null
                    _state.value = _state.value.copy(stage = MapStage.SELECTED,
                        searching = false, destination = prediction, message = null)
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
        when (_state.value.stage) {
            MapStage.SEARCH, MapStage.RESOLVING, MapStage.SELECTED -> {
                cancelSearch()
                _state.value = GoogleMapsState(hasPreciseLocation = _state.value.hasPreciseLocation)
                recenter()
            }
            else -> _state.value = _state.value.copy(expanded = false)
        }
    }

    /** This is the only entry point that may request a Navigation SDK route. */
    fun startNavigation() {
        val destination = _state.value.destination ?: return
        if (_state.value.stage != MapStage.SELECTED) return
        if (!_state.value.hasPreciseLocation) {
            _state.value = _state.value.copy(message = "Precise location is needed")
            return
        }
        val generation = ++routeGeneration
        _state.value = _state.value.copy(stage = MapStage.STARTING, message = null)
        val ready = navigator
        if (ready != null) requestRoute(ready, destination, generation)
        else NavigationApi.getNavigator(activity, object : NavigationApi.NavigatorListener {
            override fun onNavigatorReady(value: Navigator) {
                if (generation != routeGeneration || _state.value.stage != MapStage.STARTING) {
                    value.cleanup()
                    return
                }
                navigator = value
                requestRoute(value, destination, generation)
            }
            override fun onError(errorCode: Int) {
                if (generation != routeGeneration) return
                val message = when (errorCode) {
                    NavigationApi.ErrorCode.NOT_AUTHORIZED -> "Navigation key is not authorized"
                    NavigationApi.ErrorCode.TERMS_NOT_ACCEPTED -> "Accept navigation terms to continue"
                    NavigationApi.ErrorCode.NETWORK_ERROR -> "Navigation needs a connection"
                    NavigationApi.ErrorCode.LOCATION_PERMISSION_MISSING -> "Precise location is needed"
                    else -> "Navigation unavailable ($errorCode)"
                }
                _state.value = _state.value.copy(stage = MapStage.SELECTED, message = message)
            }
        })
    }

    private fun requestRoute(value: Navigator, destination: MapPrediction, generation: Int) {
        val waypoint = try { Waypoint.builder().setPlaceIdString(destination.placeId)
            .setTitle(destination.title).build() }
        catch (_: Waypoint.UnsupportedPlaceIdException) {
            _state.value = _state.value.copy(stage = MapStage.SELECTED, message = "This place cannot be routed")
            return
        }
        val options = RoutingOptions().travelMode(RoutingOptions.TravelMode.TWO_WHEELER)
        value.setDestination(waypoint, options).setOnResultListener { status ->
            if (generation != routeGeneration || _state.value.stage != MapStage.STARTING) return@setOnResultListener
            if (status == Navigator.RouteStatus.OK) {
                if (arrivalListener == null) {
                    arrivalListener = Navigator.ArrivalListener { event: ArrivalEvent ->
                        if (event.isFinalDestination) stopNavigation()
                    }.also(value::addArrivalListener)
                }
                val started = runCatching { value.startGuidance() }.isSuccess
                if (started) {
                    _state.value = _state.value.copy(stage = MapStage.NAVIGATING, message = null)
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
                    runCatching { value.clearDestinations() }
                    _state.value = _state.value.copy(stage = MapStage.SELECTED,
                        message = "Navigation could not start. Check location and try again")
                }
            } else {
                value.clearDestinations()
                _state.value = _state.value.copy(stage = MapStage.SELECTED, message = when (status) {
                    Navigator.RouteStatus.NO_ROUTE_FOUND -> "No route found for this destination"
                    Navigator.RouteStatus.NETWORK_ERROR -> "Route needs a connection"
                    Navigator.RouteStatus.QUOTA_CHECK_FAILED -> "Navigation quota unavailable"
                    Navigator.RouteStatus.LOCATION_DISABLED, Navigator.RouteStatus.LOCATION_UNKNOWN -> "Location unavailable"
                    else -> "Could not start navigation: $status"
                })
            }
        }
    }

    fun stopNavigation() {
        routeGeneration++
        navigator?.let { value ->
            arrivalListener?.let { runCatching { value.removeArrivalListener(it) } }
            progressListener?.let { runCatching { value.removeRemainingTimeOrDistanceChangedListener(it) } }
            runCatching { value.stopGuidance() }
            runCatching { value.clearDestinations() }
            runCatching { value.cleanup() }
        }
        arrivalListener = null
        progressListener = null
        navigator = null
        _state.value = GoogleMapsState(hasPreciseLocation = _state.value.hasPreciseLocation)
        recenter()
    }

    fun followNavigation() {
        if (_state.value.stage != MapStage.NAVIGATING || navigator == null) return
        map?.let { target -> runCatching {
            target.followMyLocation(GoogleMap.CameraPerspective.TILTED)
        } }
    }

    private fun updateRouteProgress(value: Navigator) {
        val estimate = runCatching { value.currentTimeAndDistance }.getOrNull() ?: return
        if (estimate.seconds < 0 || estimate.meters < 0 ||
            (estimate.seconds == 0 && estimate.meters == 0)) return
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
                        locationManager.getCurrentLocation(provider, signal, activity.mainExecutor) { fix ->
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
        target.animateCamera(CameraUpdateFactory.newLatLngZoom(
            LatLng(location.latitude, location.longitude), 15f))
        _state.value = _state.value.copy(message = null)
    }

    private fun enableLocationOnMap() {
        val target = map ?: return
        try {
            target.isMyLocationEnabled = _state.value.hasPreciseLocation
            target.uiSettings.isMyLocationButtonEnabled = false
        } catch (_: SecurityException) { /* Permission can be revoked while visible. */ }
    }

    private fun placesClient(): PlacesClient? {
        if (BuildConfig.PLACES_API_KEY.isBlank()) {
            _state.value = _state.value.copy(searching = false, message = "Places key is missing")
            return null
        }
        return try {
            if (!Places.isInitialized()) Places.initializeWithNewPlacesApiEnabled(
                activity.applicationContext, BuildConfig.PLACES_API_KEY)
            placesClient ?: Places.createClient(activity.applicationContext).also { placesClient = it }
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
        cancelSearch()
        cancelLocationLookup()
        routeGeneration++
        navigator?.let { value ->
            arrivalListener?.let { runCatching { value.removeArrivalListener(it) } }
            progressListener?.let { runCatching { value.removeRemainingTimeOrDistanceChangedListener(it) } }
            runCatching { value.stopGuidance() }
            runCatching { value.clearDestinations() }
            runCatching { value.cleanup() }
        }
        arrivalListener = null
        progressListener = null
        navigator = null
        map = null
        scope.coroutineContext[Job]?.cancel()
    }

    private companion object {
        const val LOCATION_ATTEMPT_TIMEOUT_MS = 8_000L
        const val LOCATION_CACHE_MAX_AGE_NANOS = 120_000_000_000L
        const val LOCATION_CACHE_NO_REFRESH_NANOS = 30_000_000_000L
    }
}
