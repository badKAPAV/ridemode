package com.kapav.ridemode.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import androidx.room.withTransaction
import com.kapav.ridemode.core.database.PageWithWidgets
import com.kapav.ridemode.core.database.RideDatabase
import com.kapav.ridemode.core.database.RidePage
import com.kapav.ridemode.core.database.WidgetInstance
import com.kapav.ridemode.core.model.GridLayout
import com.kapav.ridemode.core.model.GridRect
import com.kapav.ridemode.core.model.RideGridConstraints
import com.kapav.ridemode.core.model.WidgetRegistry
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first

private val Context.ridePreferences by preferencesDataStore("ride_preferences")

enum class OledSaverStyle { CLASSIC, ROW }

data class RidePreferences(
    val keepAwake: Boolean = true,
    val saverEnabled: Boolean = false,
    val saverDelaySeconds: Int = 30,
    val saverStyle: OledSaverStyle = OledSaverStyle.CLASSIC,
    val outlineText: Boolean = true,
    val reduceBrightness: Boolean = false,
    val popupEnabled: Boolean = true,
    val privacyMode: Boolean = false,
    val reducedMotion: Boolean = false,
    val rideStartAnimation: Boolean = true,
    val rowHeightDp: Int = RideGridConstraints.DEFAULT_ROW_HEIGHT_DP
)

class RideRepository private constructor(private val context: Context) {
    private val database = Room.databaseBuilder(context, RideDatabase::class.java, "ride_layout.db").build()
    private val dao = database.dao()
    fun gridSize(rowHeightDp: Int) = RideGridConstraints.size(context, rowHeightDp)
    fun maxRows(rowHeightDp: Int): Int = gridSize(rowHeightDp).rows
    fun maxColumns(rowHeightDp: Int): Int = gridSize(rowHeightDp).columns
    val pages: Flow<List<PageWithWidgets>> = dao.observePages()

    private object Keys {
        val keepAwake = booleanPreferencesKey("keep_awake")
        val saverEnabled = booleanPreferencesKey("saver_enabled")
        val saverDelay = intPreferencesKey("saver_delay_seconds")
        val saverStyle = stringPreferencesKey("saver_style")
        val outlineText = booleanPreferencesKey("outline_text")
        val reduceBrightness = booleanPreferencesKey("reduce_brightness")
        val popupEnabled = booleanPreferencesKey("popup_enabled")
        val privacyMode = booleanPreferencesKey("privacy_mode")
        val reducedMotion = booleanPreferencesKey("reduced_motion")
        val rideStartAnimation = booleanPreferencesKey("ride_start_animation")
        val rowHeight = intPreferencesKey("row_height_dp")
    }

    val preferences: Flow<RidePreferences> = context.ridePreferences.data.map { p ->
        RidePreferences(
            keepAwake = p[Keys.keepAwake] ?: true,
            saverEnabled = p[Keys.saverEnabled] ?: false,
            saverDelaySeconds = p[Keys.saverDelay] ?: 30,
            saverStyle = p[Keys.saverStyle]?.let { saved ->
                OledSaverStyle.entries.firstOrNull { it.name == saved }
            } ?: OledSaverStyle.CLASSIC,
            outlineText = p[Keys.outlineText] ?: true,
            reduceBrightness = p[Keys.reduceBrightness] ?: false,
            popupEnabled = p[Keys.popupEnabled] ?: true,
            privacyMode = p[Keys.privacyMode] ?: false,
            reducedMotion = p[Keys.reducedMotion] ?: false,
            rideStartAnimation = p[Keys.rideStartAnimation] ?: true,
            rowHeightDp = p[Keys.rowHeight]?.takeIf { it in RideGridConstraints.ALLOWED_ROW_HEIGHT_DP }
                ?: RideGridConstraints.DEFAULT_ROW_HEIGHT_DP
        )
    }

    suspend fun setSaverEnabled(value: Boolean) = context.ridePreferences.edit { it[Keys.saverEnabled] = value }
    suspend fun setKeepAwake(value: Boolean) = context.ridePreferences.edit { it[Keys.keepAwake] = value }
    suspend fun setSaverDelay(seconds: Int) {
        require(seconds in listOf(15, 30, 60, 120, 300))
        context.ridePreferences.edit { it[Keys.saverDelay] = seconds }
    }
    suspend fun setSaverStyle(style: OledSaverStyle) =
        context.ridePreferences.edit { it[Keys.saverStyle] = style.name }
    suspend fun setOutlineText(value: Boolean) = context.ridePreferences.edit { it[Keys.outlineText] = value }
    suspend fun setReduceBrightness(value: Boolean) = context.ridePreferences.edit { it[Keys.reduceBrightness] = value }
    suspend fun setReducedMotion(value: Boolean) = context.ridePreferences.edit { it[Keys.reducedMotion] = value }
    suspend fun setPrivacyMode(value: Boolean) = context.ridePreferences.edit { it[Keys.privacyMode] = value }
    suspend fun setRideStartAnimation(value: Boolean) =
        context.ridePreferences.edit { it[Keys.rideStartAnimation] = value }
    suspend fun readPreferences(): RidePreferences = preferences.first()
    suspend fun setRowHeight(value: Int): Boolean {
        require(value in RideGridConstraints.ALLOWED_ROW_HEIGHT_DP)
        val grid = gridSize(value)
        if (dao.pages().any { page -> dao.widgets(page.id).any {
            it.y + it.height > grid.rows || it.x + it.width > grid.columns
        } }) return false
        context.ridePreferences.edit { it[Keys.rowHeight] = value }
        return true
    }

    suspend fun ensureSeeded() = database.withTransaction {
        val grid = gridSize(preferences.first().rowHeightDp)
        val rows = grid.rows
        if (dao.pageCount() != 0) {
            convertLegacyWeatherWidgets()
            repairLegacySeedLayouts(rows, grid.columns)
            repairSavedBounds(grid)
            return@withTransaction
        }
        templates.forEachIndexed { index, template ->
            val pageId = UUID.nameUUIDFromBytes("default:${template.first}".toByteArray()).toString()
            dao.putPage(RidePage(pageId, template.first, index, true))
            val placements = templatePlacements(template.first, template.second, rows, grid.columns)
            template.second.forEachIndexed { widgetIndex, type ->
                val definition = requireNotNull(WidgetRegistry.get(type))
                val rect = placements[widgetIndex] ?: return@forEachIndexed
                dao.putWidget(WidgetInstance(
                    id = UUID.nameUUIDFromBytes("$pageId:$widgetIndex".toByteArray()).toString(),
                    pageId = pageId, typeId = type,
                    x = rect.x, y = rect.y, width = rect.width, height = rect.height,
                    minWidth = definition.minWidth, minHeight = definition.minHeight,
                    oledSupported = definition.oledSupported
                ))
            }
        }
    }

    /** Resize/reposition only overflowed saved widgets; leave unfit records intact for manual repair. */
    private suspend fun repairSavedBounds(grid: RideGridConstraints.GridSize) {
        dao.pages().forEach { page ->
            val widgets = dao.widgets(page.id)
            val occupied = widgets.filter { widget ->
                val rect = widget.rect()
                WidgetRegistry.valid(widget.typeId, rect, grid.columns) &&
                    GridLayout.issues(rect, maxRows = grid.rows,
                        maxColumns = grid.columns).isEmpty()
            }.map { it.rect() }.toMutableList()
            widgets.filter { it.x + it.width > grid.columns || it.y + it.height > grid.rows }
                .forEach { widget ->
                    val fitted = WidgetRegistry.fittedPlacement(widget.typeId, widget.rect(), occupied, grid)
                    if (fitted != null) {
                        dao.putWidget(widget.copy(x = fitted.x, y = fitted.y,
                            width = fitted.width, height = fitted.height))
                        occupied += fitted
                    }
                }
        }
    }

    /** Keep every saved placement and ID while retiring the separate weather type. */
    private suspend fun convertLegacyWeatherWidgets() {
        dao.pages().forEach { page ->
            dao.widgets(page.id).filter { it.typeId == "weather" }.forEach { widget ->
                dao.putWidget(widget.copy(typeId = "clock", minHeight = 2))
            }
        }
    }

    private suspend fun repairLegacySeedLayouts(rows: Int, columns: Int) {
        templates.forEach { template ->
            val id = UUID.nameUUIDFromBytes("default:${template.first}".toByteArray()).toString()
            val widgets = dao.widgets(id)
            if (widgets.size != template.second.size) return@forEach
            val stillOriginal = widgets.all { widget ->
                val index = template.second.indices.firstOrNull { index ->
                    widget.id == UUID.nameUUIDFromBytes("$id:$index".toByteArray()).toString()
                } ?: return@all false
                widget.typeId == template.second[index] && widget.x == 0 && widget.y == index * 3 &&
                    widget.width == requireNotNull(WidgetRegistry.get(widget.typeId)).defaultWidth &&
                    widget.height == if (widget.typeId == "music") 3
                        else requireNotNull(WidgetRegistry.get(widget.typeId)).defaultHeight
            }
            if (!stillOriginal) return@forEach
            val packed = templatePlacements(template.first, template.second, rows, columns)
            widgets.forEach { widget ->
                val index = template.second.indices.first { index ->
                    widget.id == UUID.nameUUIDFromBytes("$id:$index".toByteArray()).toString()
                }
                val rect = packed[index] ?: return@forEach
                dao.putWidget(widget.copy(x = rect.x, y = rect.y,
                    width = rect.width, height = rect.height,
                    minWidth = requireNotNull(WidgetRegistry.get(widget.typeId)).minWidth,
                    minHeight = requireNotNull(WidgetRegistry.get(widget.typeId)).minHeight))
            }
        }
    }

    suspend fun addPage(name: String = "New page") = database.withTransaction {
        val order = (dao.pages().maxOfOrNull { it.sortOrder } ?: -1) + 1
        val id = UUID.randomUUID().toString()
        dao.putPage(RidePage(id, name.trim().ifEmpty { "New page" }, order, true))
        id
    }

    suspend fun renamePage(id: String, name: String) = database.withTransaction {
        val page = dao.pages().firstOrNull { it.id == id } ?: return@withTransaction
        dao.putPage(page.copy(name = name.trim().take(40).ifEmpty { page.name }))
    }

    suspend fun setPageEnabled(id: String, enabled: Boolean) = database.withTransaction {
        val pages = dao.pages()
        if (!enabled && pages.count { it.enabled } <= 1) return@withTransaction
        pages.firstOrNull { it.id == id }?.let { dao.putPage(it.copy(enabled = enabled)) }
    }

    suspend fun deletePage(id: String) = database.withTransaction {
        val pages = dao.pages()
        if (pages.size <= 1) return@withTransaction
        val deleting = pages.firstOrNull { it.id == id } ?: return@withTransaction
        if (deleting.enabled && pages.none { it.id != id && it.enabled }) {
            pages.first { it.id != id }.let { dao.putPage(it.copy(enabled = true)) }
        }
        dao.deletePage(id)
        normalizeOrder()
    }

    suspend fun duplicatePage(id: String) = database.withTransaction {
        val page = dao.pages().firstOrNull { it.id == id } ?: return@withTransaction null
        val newId = UUID.randomUUID().toString()
        dao.putPage(page.copy(id = newId, name = "${page.name} copy", sortOrder = dao.pages().size))
        dao.widgets(id).forEach { dao.putWidget(it.copy(id = UUID.randomUUID().toString(), pageId = newId)) }
        newId
    }

    suspend fun shiftPage(id: String, delta: Int) = database.withTransaction {
        val pages = dao.pages()
        val old = pages.indexOfFirst { it.id == id }
        if (old < 0 || old + delta !in pages.indices) return@withTransaction
        val reordered = pages.toMutableList()
        val item = reordered.removeAt(old)
        reordered.add(old + delta, item)
        reordered.forEachIndexed { index, page -> dao.putPage(page.copy(sortOrder = index)) }
    }

    suspend fun movePage(id: String, targetIndex: Int) = database.withTransaction {
        val pages = dao.pages()
        val oldIndex = pages.indexOfFirst { it.id == id }
        if (oldIndex < 0 || targetIndex !in pages.indices || oldIndex == targetIndex) return@withTransaction
        val reordered = pages.toMutableList()
        reordered.add(targetIndex, reordered.removeAt(oldIndex))
        reordered.forEachIndexed { index, page -> dao.putPage(page.copy(sortOrder = index)) }
    }

    suspend fun resetPage(id: String) = database.withTransaction {
        val page = dao.pages().firstOrNull { it.id == id } ?: return@withTransaction
        val template = templates.firstOrNull {
            UUID.nameUUIDFromBytes("default:${it.first}".toByteArray()).toString() == id
        }
        dao.clearWidgets(id)
        if (template == null) return@withTransaction
        val grid = gridSize(preferences.first().rowHeightDp)
        val placements = templatePlacements(template.first, template.second, grid.rows, grid.columns)
        template.second.forEachIndexed { index, type ->
            val def = requireNotNull(WidgetRegistry.get(type))
            val rect = placements[index] ?: return@forEachIndexed
            dao.putWidget(WidgetInstance(UUID.randomUUID().toString(), id, type,
                rect.x, rect.y, rect.width, rect.height, def.minWidth, def.minHeight, def.oledSupported))
        }
    }

    suspend fun addWidget(pageId: String, type: String): Boolean = database.withTransaction {
        val def = WidgetRegistry.get(type) ?: return@withTransaction false
        if (dao.pages().none { it.id == pageId }) return@withTransaction false
        val existing = dao.widgets(pageId).map { it.rect() }
        val grid = gridSize(preferences.first().rowHeightDp)
        val maxRows = grid.rows
        val maxColumns = grid.columns
        val sizes = (def.minWidth..def.maxWidth.coerceAtMost(maxColumns)).flatMap { width ->
            (def.minHeight..def.maxHeight.coerceAtMost(maxRows)).map { height -> width to height }
        }.sortedWith(compareBy<Pair<Int, Int>> { it.first * it.second }
            .thenBy { it.second }.thenBy { it.first })
        val spot = sizes.firstNotNullOfOrNull { (width, height) ->
            (0..maxRows - height).firstNotNullOfOrNull { y ->
                (0..maxColumns - width).firstNotNullOfOrNull { x ->
                    GridRect(x, y, width, height).takeIf {
                        WidgetRegistry.valid(type, it, maxColumns) &&
                            GridLayout.issues(it, existing, maxRows, maxColumns).isEmpty()
                    }
                }
            }
        } ?: return@withTransaction false
        dao.putWidget(WidgetInstance(UUID.randomUUID().toString(), pageId, type,
            spot.x, spot.y, spot.width, spot.height, def.minWidth, def.minHeight, def.oledSupported))
        true
    }

    suspend fun placeWidget(id: String, proposed: GridRect): Boolean = database.withTransaction {
        val grid = gridSize(preferences.first().rowHeightDp)
        val maxRows = grid.rows
        val maxColumns = grid.columns
        val pages = dao.pages()
        val widget = pages.firstNotNullOfOrNull { page -> dao.widgets(page.id).firstOrNull { it.id == id } }
            ?: return@withTransaction false
        val others = dao.widgets(widget.pageId).filter { it.id != id }
        if (!WidgetRegistry.valid(widget.typeId, proposed, maxColumns) ||
            GridLayout.issues(proposed, maxRows = maxRows, maxColumns = maxColumns).isNotEmpty()) return@withTransaction false
        val overlaps = others.filter { GridLayout.issues(proposed, listOf(it.rect()), maxRows, maxColumns)
            .contains(com.kapav.ridemode.core.model.GridIssue.OVERLAP) }
        if (overlaps.size > 1) return@withTransaction false
        if (overlaps.size == 1) {
            val displaced = overlaps.single()
            val destination = GridLayout.displacedPlacement(widget.rect(), proposed, displaced.rect(),
                others.filter { it.id != displaced.id }.map { it.rect() }, maxRows, maxColumns)
                ?: return@withTransaction false
            dao.putWidget(displaced.copy(x = destination.x, y = destination.y))
        }
        dao.putWidget(widget.copy(x = proposed.x, y = proposed.y,
            width = proposed.width, height = proposed.height))
        true
    }

    suspend fun rotateWidget(id: String): Boolean = database.withTransaction {
        val widget = dao.pages().firstNotNullOfOrNull { page ->
            dao.widgets(page.id).firstOrNull { it.id == id }
        } ?: return@withTransaction false
        val others = dao.widgets(widget.pageId).filter { it.id != id }.map { it.rect() }
        val grid = gridSize(preferences.first().rowHeightDp)
        val rotated = WidgetRegistry.rotatedPlacement(widget.typeId, widget.rect(), others,
            grid.rows, grid.columns)
            ?: return@withTransaction false
        dao.putWidget(widget.copy(x = rotated.x, y = rotated.y,
            width = rotated.width, height = rotated.height))
        true
    }

    suspend fun fitWidget(id: String): Boolean = database.withTransaction {
        val widget = dao.pages().firstNotNullOfOrNull { page ->
            dao.widgets(page.id).firstOrNull { it.id == id }
        } ?: return@withTransaction false
        val grid = gridSize(preferences.first().rowHeightDp)
        val others = dao.widgets(widget.pageId).filter { it.id != id }.map { it.rect() }
        val fitted = WidgetRegistry.fittedPlacement(widget.typeId, widget.rect(), others, grid)
            ?: return@withTransaction false
        dao.putWidget(widget.copy(x = fitted.x, y = fitted.y,
            width = fitted.width, height = fitted.height))
        true
    }

    suspend fun removeWidget(id: String) = database.withTransaction { dao.deleteWidget(id) }

    suspend fun setWidgetVisible(id: String, visible: Boolean) = database.withTransaction {
        val widget = dao.pages().firstNotNullOfOrNull { page ->
            dao.widgets(page.id).firstOrNull { it.id == id }
        } ?: return@withTransaction
        dao.putWidget(widget.copy(visible = visible))
    }

    suspend fun duplicateWidget(id: String): Boolean = database.withTransaction {
        val grid = gridSize(preferences.first().rowHeightDp)
        val maxRows = grid.rows
        val maxColumns = grid.columns
        val original = dao.pages().firstNotNullOfOrNull { page ->
            dao.widgets(page.id).firstOrNull { it.id == id }
        } ?: return@withTransaction false
        val existing = dao.widgets(original.pageId).map { it.rect() }
        val spot = (0..(maxRows - original.height)).firstNotNullOfOrNull { y ->
            (0..(maxColumns - original.width)).firstNotNullOfOrNull { x ->
                GridRect(x, y, original.width, original.height)
                    .takeIf { GridLayout.issues(it, existing, maxRows, maxColumns).isEmpty() }
            }
        } ?: return@withTransaction false
        dao.putWidget(original.copy(id = UUID.randomUUID().toString(), x = spot.x, y = spot.y))
        true
    }

    private suspend fun normalizeOrder() {
        dao.pages().forEachIndexed { index, page -> dao.putPage(page.copy(sortOrder = index)) }
    }

    companion object {
        @Volatile private var instance: RideRepository? = null
        fun get(context: Context): RideRepository = instance ?: synchronized(this) {
            instance ?: RideRepository(context.applicationContext).also { instance = it }
        }

        private val templates = listOf(
            "Music" to listOf("music", "volume"),
            "SOS / Favorites" to listOf("sos_contacts"),
            "Clock & Weather" to listOf("clock", "battery"),
            "Calls" to listOf("calls"),
            "Notifications" to listOf("notifications"),
            "Touring custom" to listOf("music", "clock", "volume", "sos_contacts", "maps_shortcut", "assistant")
        )

        private fun templatePlacements(name: String, types: List<String>, maxRows: Int,
            maxColumns: Int): List<GridRect?> {
            val placed = mutableListOf<GridRect>()
            return types.map { type ->
                val def = requireNotNull(WidgetRegistry.get(type))
                val height = if (name == "Touring custom" && type == "sos_contacts") 2 else def.defaultHeight
                val spot = (0..(maxRows - height)).firstNotNullOfOrNull { y ->
                    (0..(maxColumns - def.defaultWidth)).firstNotNullOfOrNull { x ->
                        GridRect(x, y, def.defaultWidth, height)
                            .takeIf { GridLayout.issues(it, placed, maxRows, maxColumns).isEmpty() }
                    }
                }
                if (spot != null) placed += spot
                spot
            }
        }
    }
}

fun WidgetInstance.rect() = GridRect(x, y, width, height)
