package com.kapav.ridemode

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.kapav.ridemode.core.database.RideDatabase
import com.kapav.ridemode.core.database.RidePage
import com.kapav.ridemode.core.database.WidgetInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RideDatabaseTest {
    @Test
    fun pageUpdateKeepsWidgetsAndDeleteCascades() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val db = Room.inMemoryDatabaseBuilder(context, RideDatabase::class.java).build()
        try {
            val dao = db.dao()
            dao.putPage(RidePage("page", "Original", 0, true))
            dao.putWidget(WidgetInstance("widget", "page", "clock", 0, 0, 4, 2, 2, 1, true))
            dao.putPage(RidePage("page", "Renamed", 0, true))
            val page = dao.observePages().first().single()
            assertEquals("Renamed", page.page.name)
            assertEquals("widget", page.widgets.single().id)
            dao.deletePage("page")
            assertTrue(dao.observePages().first().isEmpty())
            assertTrue(dao.widgets("page").isEmpty())
        } finally {
            db.close()
        }
    }

    @Test
    fun savedLayoutSurvivesDatabaseReopen() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "layout-reopen-test.db"
        context.deleteDatabase(name)
        try {
            val firstDb: RideDatabase = Room.databaseBuilder(context, RideDatabase::class.java, name).build()
            try {
                firstDb.dao().putPage(RidePage("saved", "Saved page", 0, true))
                firstDb.dao().putWidget(WidgetInstance("clock-1", "saved", "clock", 4, 2, 4, 2, 2, 1, true))
            } finally { firstDb.close() }
            val reopenedDb: RideDatabase = Room.databaseBuilder(context, RideDatabase::class.java, name).build()
            try {
                val page = reopenedDb.dao().observePages().first().single()
                assertEquals("Saved page", page.page.name)
                assertEquals(4, page.widgets.single().x)
                assertEquals(2, page.widgets.single().y)
            } finally { reopenedDb.close() }
        } finally {
            context.deleteDatabase(name)
        }
    }
}
