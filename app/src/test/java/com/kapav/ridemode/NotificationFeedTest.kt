package com.kapav.ridemode

import android.app.NotificationManager
import com.kapav.ridemode.data.NotificationEntry
import com.kapav.ridemode.data.visibleNotificationFeed
import org.junit.Assert.assertEquals
import org.junit.Test

class NotificationFeedTest {
    @Test fun messagesLeadAndOnlyTenRecentEligibleItemsRemain() {
        val high = (1..12).map { index ->
            NotificationEntry("alert$index", "App", "body", index.toLong(), false,
                NotificationManager.IMPORTANCE_HIGH)
        }
        val message = NotificationEntry("message", "Alex", "Hi", 1L, true,
            NotificationManager.IMPORTANCE_LOW)
        val low = NotificationEntry("low", "App", "quiet", 100L, false,
            NotificationManager.IMPORTANCE_LOW)
        val result = visibleNotificationFeed(high + message + low)
        assertEquals(10, result.size)
        assertEquals("message", result.first().key)
        assertEquals("alert12", result[1].key)
        assertEquals(false, result.any { it.key == "low" })
    }
}
