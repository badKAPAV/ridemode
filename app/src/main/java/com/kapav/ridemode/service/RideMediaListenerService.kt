package com.kapav.ridemode.service

import android.service.notification.NotificationListenerService
import com.kapav.ridemode.data.MediaSessionRepository
import com.kapav.ridemode.data.NotificationFeedRepository
import com.kapav.ridemode.data.TurnGuidanceRepository
import android.service.notification.StatusBarNotification
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/** Shares one user-granted listener for media sessions and the bounded notification feed. */
@AndroidEntryPoint
class RideMediaListenerService : NotificationListenerService() {
    @Inject lateinit var media: MediaSessionRepository
    @Inject lateinit var feed: NotificationFeedRepository
    @Inject lateinit var guidance: TurnGuidanceRepository

    override fun onListenerConnected() {
        super.onListenerConnected()
        media.refresh()
        runCatching { feed.connected(activeNotifications.orEmpty(), currentRanking) }
            .onFailure { feed.clear() }
        runCatching { guidance.connected(activeNotifications.orEmpty()) }
            .onFailure { guidance.clear() }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification,
        rankingMap: RankingMap) {
        feed.posted(sbn, rankingMap)
        guidance.posted(sbn)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification,
        rankingMap: RankingMap) {
        feed.removed(sbn.key)
        guidance.removed(sbn.key)
    }

    override fun onNotificationRankingUpdate(rankingMap: RankingMap) {
        feed.rankingChanged(rankingMap)
    }

    override fun onListenerDisconnected() {
        media.disconnect()
        feed.clear()
        guidance.clear()
        super.onListenerDisconnected()
    }
}
