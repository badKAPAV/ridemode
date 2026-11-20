package com.kapav.ridemode

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.kapav.ridemode.core.database.*
import com.kapav.ridemode.core.model.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WidgetConfigPersistenceTest {
    @Test fun contactSnapshotsRemainIndependentAcrossWidgetsPagesAndReopen() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "widget-config-test.db"
        context.deleteDatabase(name)
        val alice = SosFavorite("alice", "Alice", "+1234567890")
        val bob = SosFavorite("bob", "Bob", "+9876543210")
        val initial = WidgetConfigs.sosJson("{}", SosTileConfig(listOf(alice)))
        try {
            val db = Room.databaseBuilder(context, RideDatabase::class.java, name).build()
            try {
                val dao = db.dao()
                dao.putPage(RidePage("one", "One", 0, true))
                dao.putPage(RidePage("two", "Two", 1, true))
                val first = WidgetInstance("a", "one", "sos_contacts", 0, 0, 2, 2, 2, 2, true, configJson = initial)
                dao.putWidget(first)
                dao.putWidget(first.copy(id = "b", x = 2))
                dao.putWidget(first.copy(id = "c", pageId = "two"))
                dao.putWidget(first.copy(configJson = WidgetConfigs.sosJson(initial, SosTileConfig(listOf(bob), true))))
            } finally { db.close() }
            val reopened = Room.databaseBuilder(context, RideDatabase::class.java, name).build()
            try {
                val one = reopened.dao().widgets("one").associateBy { it.id }
                assertEquals(listOf(bob), WidgetConfigs.sos(one.getValue("a").configJson).contacts)
                assertTrue(WidgetConfigs.sos(one.getValue("a").configJson).directCall)
                assertEquals(listOf(alice), WidgetConfigs.sos(one.getValue("b").configJson).contacts)
                assertEquals(listOf(alice), WidgetConfigs.sos(reopened.dao().widgets("two").single().configJson).contacts)
                assertFalse(WidgetConfigs.sos(one.getValue("b").configJson).directCall)
                val speed = WidgetConfigs.speedJson("{}", SpeedTileConfig("mph", false))
                assertEquals(SpeedTileConfig("mph", false), WidgetConfigs.speed(speed))
            } finally { reopened.close() }
        } finally { context.deleteDatabase(name) }
    }
}
