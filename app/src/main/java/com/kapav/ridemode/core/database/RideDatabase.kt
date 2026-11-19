package com.kapav.ridemode.core.database

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Relation
import androidx.room.RoomDatabase
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "ride_pages")
data class RidePage(
    @PrimaryKey val id: String,
    val name: String,
    @androidx.room.ColumnInfo(name = "sort_order") val sortOrder: Int,
    val enabled: Boolean
)

@Entity(
    tableName = "widget_instances",
    foreignKeys = [ForeignKey(
        entity = RidePage::class,
        parentColumns = ["id"], childColumns = ["page_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("page_id")]
)
data class WidgetInstance(
    @PrimaryKey val id: String,
    @androidx.room.ColumnInfo(name = "page_id") val pageId: String,
    @androidx.room.ColumnInfo(name = "type_id") val typeId: String,
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int,
    @androidx.room.ColumnInfo(name = "min_width") val minWidth: Int,
    @androidx.room.ColumnInfo(name = "min_height") val minHeight: Int,
    @androidx.room.ColumnInfo(name = "oled_supported") val oledSupported: Boolean,
    @androidx.room.ColumnInfo(name = "config_json") val configJson: String = "{}",
    val visible: Boolean = true
)

data class PageWithWidgets(
    @Embedded val page: RidePage,
    @Relation(parentColumn = "id", entityColumn = "page_id")
    val widgets: List<WidgetInstance>
)

@Dao
interface RideDao {
    @Transaction
    @Query("SELECT * FROM ride_pages ORDER BY sort_order, id")
    fun observePages(): Flow<List<PageWithWidgets>>

    @Query("SELECT COUNT(*) FROM ride_pages")
    suspend fun pageCount(): Int

    @Query("SELECT * FROM ride_pages ORDER BY sort_order, id")
    suspend fun pages(): List<RidePage>

    @Query("SELECT * FROM widget_instances WHERE page_id = :pageId")
    suspend fun widgets(pageId: String): List<WidgetInstance>

    @Upsert
    suspend fun putPage(page: RidePage)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun putWidget(widget: WidgetInstance)

    @Query("DELETE FROM ride_pages WHERE id = :id")
    suspend fun deletePage(id: String)

    @Query("DELETE FROM widget_instances WHERE id = :id")
    suspend fun deleteWidget(id: String)

    @Query("DELETE FROM widget_instances WHERE page_id = :pageId")
    suspend fun clearWidgets(pageId: String)
}

@Database(entities = [RidePage::class, WidgetInstance::class], version = 1, exportSchema = true)
abstract class RideDatabase : RoomDatabase() {
    abstract fun dao(): RideDao
}
