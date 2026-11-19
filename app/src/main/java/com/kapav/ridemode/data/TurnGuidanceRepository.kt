package com.kapav.ridemode.data

import android.app.Notification
import android.app.PendingIntent
import android.os.Bundle
import android.service.notification.StatusBarNotification
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class GuidanceAccess { UNAVAILABLE, LISTENING, INACTIVE, CURRENT, STALE }
enum class Maneuver { LEFT, RIGHT, SLIGHT_LEFT, SLIGHT_RIGHT, STRAIGHT, U_TURN, ROUNDABOUT }

data class TurnGuidanceState(
    val access: GuidanceAccess = GuidanceAccess.UNAVAILABLE,
    val instruction: String = "",
    val maneuver: Maneuver? = null,
    val distance: String? = null,
    val eta: String? = null,
    val remaining: String? = null,
    val destination: String? = null,
    val details: List<String> = emptyList(),
    val observedAtMs: Long = 0L,
    val openIntent: PendingIntent? = null
)

private const val MAPS_PACKAGE = "com.google.android.apps.maps"
private const val CUE_TTL_MS = 2 * 60 * 1000L
private const val MAX_TEXT_LENGTH = 180

/** Maps provides no public turn-by-turn feed. Only a recognizable active notification is shown. */
@Singleton
class TurnGuidanceRepository @Inject constructor(
    private val media: MediaSessionRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val active = LinkedHashMap<String, TurnGuidanceState>()
    private val _state = MutableStateFlow(TurnGuidanceState())
    val state = _state.asStateFlow()
    private var expiry: Job? = null
    private var connected = false
    private var seenCue = false

    fun refreshAccess() {
        if (!media.hasAccess()) clear()
        else if (!connected) _state.value = TurnGuidanceState(GuidanceAccess.LISTENING)
    }

    fun connected(notifications: Array<out StatusBarNotification>) {
        connected = true
        seenCue = false
        active.clear()
        val now = System.currentTimeMillis()
        notifications.filter { it.packageName == MAPS_PACKAGE }.forEach { sbn ->
            parse(sbn, sbn.postTime.coerceAtMost(now))?.let { active[sbn.key] = it; seenCue = true }
        }
        publish(now)
    }

    fun posted(sbn: StatusBarNotification) {
        if (sbn.packageName != MAPS_PACKAGE) return
        if (!media.hasAccess()) { clear(); return }
        connected = true
        active.remove(sbn.key)
        parse(sbn, System.currentTimeMillis())?.let { active[sbn.key] = it; seenCue = true }
        if (active.isEmpty()) seenCue = false
        publish()
    }

    fun removed(key: String) {
        if (!media.hasAccess()) { clear(); return }
        if (active.remove(key) != null) {
            if (active.isEmpty()) seenCue = false
            publish()
        }
    }

    fun clear() {
        expiry?.cancel()
        active.clear()
        connected = false
        seenCue = false
        _state.value = TurnGuidanceState()
    }

    private fun publish(now: Long = System.currentTimeMillis()) {
        expiry?.cancel()
        active.entries.removeAll { now - it.value.observedAtMs >= CUE_TTL_MS }
        val cue = active.values.maxByOrNull { it.observedAtMs }
        if (cue == null) {
            _state.value = TurnGuidanceState(if (seenCue) GuidanceAccess.STALE else GuidanceAccess.INACTIVE)
            return
        }
        seenCue = true
        _state.value = cue
        expiry = scope.launch {
            delay((cue.observedAtMs + CUE_TTL_MS - now).coerceAtLeast(1L))
            publish()
        }
    }

    private fun parse(sbn: StatusBarNotification, observedAtMs: Long): TurnGuidanceState? {
        if (sbn.notification.flags and Notification.FLAG_GROUP_SUMMARY != 0) return null
        // An ordinary Maps suggestion/search alert must never become a riding direction.
        if (!sbn.isOngoing) return null
        val extras = sbn.notification.extras ?: return null
        val title = extras.safeText(Notification.EXTRA_TITLE)
        val text = extras.safeText(Notification.EXTRA_TEXT)
        val bigText = extras.safeText(Notification.EXTRA_BIG_TEXT)
        val subText = extras.safeText(Notification.EXTRA_SUB_TEXT)
        val infoText = extras.safeText(Notification.EXTRA_INFO_TEXT)
        val summary = extras.safeText(Notification.EXTRA_SUMMARY_TEXT)
        val lines = runCatching { extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES) }
            .getOrNull().orEmpty().mapNotNull { it?.toString()?.clean() }
        val ticker = sbn.notification.tickerText?.toString()?.clean()
        val all = (listOfNotNull(title, text, bigText, subText, infoText, summary, ticker) + lines).distinct()
        val instruction = all.firstOrNull { maneuverOf(it) != null } ?: return null
        val maneuver = maneuverOf(instruction) ?: return null
        val remainder = all.filterNot { it == instruction || it.equals("Google Maps", true) }
        val distance = Regex("(?i)^(?:in\\s+)?(\\d[\\d,.]*\\s*(?:m|km|ft|mi|metres?|meters?|miles?|feet))\\b")
            .find(instruction)?.groupValues?.get(1)
            ?: Regex("(?i)\\bin\\s+(\\d[\\d,.]*\\s*(?:m|km|ft|mi|metres?|meters?|miles?|feet))\\b")
                .find(instruction)?.groupValues?.get(1)
            ?: remainder.firstNotNullOfOrNull { Regex("(?i)\\b(?:next turn|in)\\s+(\\d[\\d,.]*\\s*(?:m|km|ft|mi))\\b")
                .find(it)?.groupValues?.get(1) }
        val eta = remainder.firstNotNullOfOrNull { Regex("(?i)\\bETA[:\\s]+([^·|,]+)").find(it)
            ?.groupValues?.get(1)?.trim() }
        val remaining = remainder.firstOrNull { it.contains("remaining", true) ||
            Regex("(?i)\\b\\d+\\s*(?:min|mins|hours?|hr|hrs)\\b").containsMatchIn(it) }
        val destination = remainder.firstNotNullOfOrNull { Regex("(?i)^(?:to|destination)[:\\s]+(.+)")
            .find(it)?.groupValues?.get(1)?.trim() }
        return TurnGuidanceState(GuidanceAccess.CURRENT, instruction, maneuver, distance,
            eta, remaining, destination, remainder.take(8), observedAtMs,
            sbn.notification.contentIntent)
    }
}

private fun Bundle.safeText(key: String): String? =
    runCatching { getCharSequence(key)?.toString()?.clean() }.getOrNull()

private fun String.clean(): String? = trim().replace(Regex("\\s+"), " ")
    .take(MAX_TEXT_LENGTH).takeIf { it.isNotBlank() }

internal fun maneuverOf(text: String): Maneuver? {
    val normalized = text.lowercase()
    return when {
        Regex("\\b(u[- ]?turn|make a u[- ]?turn)\\b").containsMatchIn(normalized) -> Maneuver.U_TURN
        "roundabout" in normalized -> Maneuver.ROUNDABOUT
        Regex("\\b(slight|bear|keep) left\\b").containsMatchIn(normalized) -> Maneuver.SLIGHT_LEFT
        Regex("\\b(slight|bear|keep) right\\b").containsMatchIn(normalized) -> Maneuver.SLIGHT_RIGHT
        Regex("\\b(turn|take|exit) left\\b").containsMatchIn(normalized) -> Maneuver.LEFT
        Regex("\\b(turn|take|exit) right\\b").containsMatchIn(normalized) -> Maneuver.RIGHT
        Regex("\\b(continue|head|go|keep) (straight|north|south|east|west)\\b").containsMatchIn(normalized) -> Maneuver.STRAIGHT
        else -> null
    }
}
