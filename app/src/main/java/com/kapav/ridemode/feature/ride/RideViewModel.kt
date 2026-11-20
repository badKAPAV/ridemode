package com.kapav.ridemode.feature.ride

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kapav.ridemode.core.model.GridRect
import com.kapav.ridemode.data.SpeedRepository
import com.kapav.ridemode.core.model.SpeedUnit
import com.kapav.ridemode.data.RidePreferences
import com.kapav.ridemode.data.OledSaverStyle
import com.kapav.ridemode.data.MediaSessionRepository
import com.kapav.ridemode.data.MusicAction
import com.kapav.ridemode.data.VolumeRepository
import com.kapav.ridemode.data.WeatherRepository
import com.kapav.ridemode.data.NotificationFeedRepository
import com.kapav.ridemode.data.CallHistoryRepository
import com.kapav.ridemode.data.TurnGuidanceRepository
import com.kapav.ridemode.data.RideRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@HiltViewModel
class RideViewModel @Inject constructor(
    private val repository: RideRepository,
    private val media: MediaSessionRepository,
    private val volumeRepository: VolumeRepository,
    private val weatherRepository: WeatherRepository,
    private val notificationFeedRepository: NotificationFeedRepository,
    private val callHistoryRepository: CallHistoryRepository,
    private val speedRepository: SpeedRepository,
    private val turnGuidanceRepository: TurnGuidanceRepository
) : ViewModel() {
    val speed = speedRepository.state
    private var speedForeground = false
    private var speedEnabled = false
    fun setSpeedForeground(value: Boolean) { speedForeground = value; updateSpeed() }
    fun setSpeedEnabled(value: Boolean) { speedEnabled = value; updateSpeed() }
    private fun updateSpeed() { if (speedForeground && speedEnabled) speedRepository.start() else speedRepository.stop() }
    fun refreshSpeed() = speedRepository.refresh()
    suspend fun updateWidgetConfig(id: String, json: String) = repository.updateWidgetConfig(id, json)
    suspend fun setSpeedUnit(value: SpeedUnit) = repository.setSpeedUnit(value)
    val music = media.state
    fun refreshMusic() = media.refresh()
    fun controlMusic(action: MusicAction) = media.perform(action)
    val volume = volumeRepository.state
    fun refreshVolume() = volumeRepository.refresh()
    fun setVolume(fraction: Float) = volumeRepository.setFraction(fraction)
    val weather = weatherRepository.state
    fun startWeather(owner: String) = weatherRepository.start(owner)
    fun stopWeather(owner: String) = weatherRepository.stop(owner)
    fun refreshWeather() = weatherRepository.refresh()
    val notifications = notificationFeedRepository.state
    fun refreshNotifications() = notificationFeedRepository.refreshAccess()
    val guidance = turnGuidanceRepository.state
    fun refreshGuidance() = turnGuidanceRepository.refreshAccess()
    val calls = callHistoryRepository.state
    fun startCalls(owner: String) = callHistoryRepository.start(owner)
    fun stopCalls(owner: String) = callHistoryRepository.stop(owner)
    fun refreshCalls() = callHistoryRepository.refresh()
    val maxRows = repository.preferences.map { repository.maxRows(it.rowHeightDp) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000),
            repository.maxRows(RidePreferences().rowHeightDp))
    val maxColumns = repository.preferences.map { repository.maxColumns(it.rowHeightDp) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000),
            repository.maxColumns(RidePreferences().rowHeightDp))
    val pages = repository.pages.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val preferences = repository.preferences.stateIn(viewModelScope,
        SharingStarted.WhileSubscribed(5_000), RidePreferences())

    init { viewModelScope.launch { repository.ensureSeeded() } }

    suspend fun addPage(name: String = "New page") = repository.addPage(name)
    suspend fun renamePage(id: String, name: String) = repository.renamePage(id, name)
    suspend fun setPageEnabled(id: String, enabled: Boolean) = repository.setPageEnabled(id, enabled)
    suspend fun deletePage(id: String) = repository.deletePage(id)
    suspend fun duplicatePage(id: String) = repository.duplicatePage(id)
    suspend fun shiftPage(id: String, delta: Int) = repository.shiftPage(id, delta)
    suspend fun movePage(id: String, targetIndex: Int) = repository.movePage(id, targetIndex)
    suspend fun resetPage(id: String) = repository.resetPage(id)
    suspend fun addWidget(pageId: String, type: String) = repository.addWidget(pageId, type)
    suspend fun placeWidget(id: String, proposed: GridRect) = repository.placeWidget(id, proposed)
    suspend fun rotateWidget(id: String) = repository.rotateWidget(id)
    suspend fun fitWidget(id: String) = repository.fitWidget(id)
    suspend fun removeWidget(id: String) = repository.removeWidget(id)
    suspend fun duplicateWidget(id: String) = repository.duplicateWidget(id)
    suspend fun setWidgetVisible(id: String, visible: Boolean) = repository.setWidgetVisible(id, visible)
    suspend fun setSaverEnabled(value: Boolean) = repository.setSaverEnabled(value)
    suspend fun setKeepAwake(value: Boolean) = repository.setKeepAwake(value)
    suspend fun setSaverDelay(seconds: Int) = repository.setSaverDelay(seconds)
    suspend fun setSaverStyle(style: OledSaverStyle) = repository.setSaverStyle(style)
    suspend fun setOutlineText(value: Boolean) = repository.setOutlineText(value)
    suspend fun setReduceBrightness(value: Boolean) = repository.setReduceBrightness(value)
    suspend fun setReducedMotion(value: Boolean) = repository.setReducedMotion(value)
    suspend fun setPrivacyMode(value: Boolean) = repository.setPrivacyMode(value)
    suspend fun setShowMapsShortcut(value: Boolean) = repository.setShowMapsShortcut(value)
    suspend fun setRideStartAnimation(value: Boolean) = repository.setRideStartAnimation(value)
    suspend fun readPreferences() = repository.readPreferences()
    suspend fun setRowHeight(value: Int) = repository.setRowHeight(value)
}
