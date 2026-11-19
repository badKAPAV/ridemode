package com.kapav.ridemode.data

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Icon
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class NotificationAccess { REQUIRED, CONNECTING, READY }

data class NotificationEntry(
    val key: String,
    val sender: String,
    val body: String,
    val postedAtMs: Long,
    val message: Boolean,
    val importance: Int,
    val avatar: Bitmap? = null
)

data class NotificationFeedState(
    val access: NotificationAccess = NotificationAccess.REQUIRED,
    val entries: List<NotificationEntry> = emptyList()
)

internal fun visibleNotificationFeed(entries: Collection<NotificationEntry>): List<NotificationEntry> =
    entries.filter { it.message || it.importance >= NotificationManager.IMPORTANCE_HIGH }
        .sortedWith(compareByDescending<NotificationEntry> { it.message }
            .thenByDescending { it.postedAtMs })
        .take(10)

/** In-memory, bounded view of active notifications. Removed/revoked content is discarded. */
@Singleton
class NotificationFeedRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val media: MediaSessionRepository
) {
    private val active = LinkedHashMap<String, NotificationEntry>()
    private val _state = MutableStateFlow(NotificationFeedState())
    val state = _state.asStateFlow()

    fun refreshAccess() {
        if (!media.hasAccess()) clear()
        else if (_state.value.access == NotificationAccess.REQUIRED) {
            _state.value = NotificationFeedState(NotificationAccess.CONNECTING)
        }
    }

    fun connected(notifications: Array<out StatusBarNotification>, ranking: NotificationListenerService.RankingMap?) {
        active.clear()
        notifications.sortedByDescending { it.postTime }.take(50).forEach { notification ->
            parse(notification, ranking)?.let { active[it.key] = it }
        }
        trimAndPublish()
    }

    fun posted(notification: StatusBarNotification, ranking: NotificationListenerService.RankingMap?) {
        if (!media.hasAccess()) { clear(); return }
        active.remove(notification.key)
        parse(notification, ranking)?.let { active[it.key] = it }
        trimAndPublish()
    }

    fun removed(key: String) {
        if (!media.hasAccess()) { clear(); return }
        active.remove(key)
        trimAndPublish()
    }

    fun rankingChanged(ranking: NotificationListenerService.RankingMap?) {
        if (!media.hasAccess()) { clear(); return }
        if (ranking != null) {
            active.replaceAll { key, value -> value.copy(importance = importance(key, ranking)) }
        }
        trimAndPublish()
    }

    fun clear() {
        active.clear()
        _state.value = NotificationFeedState()
    }

    private fun trimAndPublish() {
        if (active.size > 50) {
            val keep = active.values.sortedByDescending { it.postedAtMs }.take(50).map { it.key }.toSet()
            active.keys.retainAll(keep)
        }
        _state.value = NotificationFeedState(NotificationAccess.READY, visibleNotificationFeed(active.values))
    }

    private fun parse(sbn: StatusBarNotification,
        ranking: NotificationListenerService.RankingMap?): NotificationEntry? {
        if (sbn.packageName == context.packageName) return null
        val notification = sbn.notification
        if (notification.flags and Notification.FLAG_GROUP_SUMMARY != 0 ||
            notification.flags and Notification.FLAG_ONGOING_EVENT != 0 ||
            notification.category == Notification.CATEGORY_TRANSPORT ||
            notification.category == Notification.CATEGORY_SERVICE ||
            notification.category == Notification.CATEGORY_CALL) return null
        val extras = notification.extras ?: return null
        val message = notification.category == Notification.CATEGORY_MESSAGE ||
            extras.containsKey(Notification.EXTRA_MESSAGES)
        val priority = importance(sbn.key, ranking)
        val visibilityOverride = if (Build.VERSION.SDK_INT >= 31) ranking?.let {
            val value = NotificationListenerService.Ranking()
            if (it.getRanking(sbn.key, value)) value.lockscreenVisibilityOverride else null
        } else null
        val privateContent = notification.visibility == Notification.VISIBILITY_SECRET ||
            visibilityOverride == Notification.VISIBILITY_SECRET
        val latest = if (message && Build.VERSION.SDK_INT >= 30) runCatching {
            Notification.MessagingStyle.Message.getMessagesFromBundleArray(
                extras.getParcelableArray(Notification.EXTRA_MESSAGES)).lastOrNull()
        }.getOrNull() else null
        val personName = if (Build.VERSION.SDK_INT >= 28) latest?.senderPerson?.name else null
        val personIcon = if (Build.VERSION.SDK_INT >= 28) latest?.senderPerson?.icon as? Icon else null
        val sender = if (privateContent) "Private notification" else
            (personName ?: extras.getCharSequence(Notification.EXTRA_TITLE))
                ?.toString()?.trim()?.take(80)?.takeIf { it.isNotBlank() }
                ?: sbn.packageName.substringAfterLast('.')
        val text = if (privateContent) "Content hidden" else
            (latest?.text ?: extras.getCharSequence(Notification.EXTRA_BIG_TEXT)
                ?: extras.getCharSequence(Notification.EXTRA_TEXT))
                ?.toString()?.trim()?.take(240)?.takeIf { it.isNotBlank() }
                ?: "Notification"
        val avatar = if (privateContent) null else smallAvatar(
            personIcon ?: notification.getLargeIcon())
        return NotificationEntry(sbn.key, sender, text, sbn.postTime, message, priority, avatar)
    }

    private fun importance(key: String, ranking: NotificationListenerService.RankingMap?): Int {
        if (ranking == null) return NotificationManager.IMPORTANCE_DEFAULT
        val result = NotificationListenerService.Ranking()
        return if (ranking.getRanking(key, result)) result.importance else NotificationManager.IMPORTANCE_DEFAULT
    }

    private fun smallAvatar(icon: Icon?): Bitmap? = runCatching {
        val drawable = icon?.loadDrawable(context) ?: return@runCatching null
        val image = Bitmap.createBitmap(64, 64, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(image)
        drawable.setBounds(0, 0, 64, 64)
        drawable.draw(canvas)
        image
    }.getOrNull()
}
