package com.kapav.ridemode.core.model

import org.json.JSONArray
import org.json.JSONObject

enum class SpeedUnit(val id: String, val label: String, val factor: Double) {
    KMH("kmh", "km/h", 3.6), MPH("mph", "mph", 2.2369362920544);
    companion object { fun fromId(id: String?) = entries.firstOrNull { it.id == id } ?: KMH }
}

data class SpeedTileConfig(val units: String = "default", val showStatus: Boolean = true) {
    fun resolvedUnit(default: SpeedUnit) = if (units == "default") default else SpeedUnit.fromId(units)
}

data class SosFavorite(val id: String, val name: String, val number: String, val photo: String? = null) {
    val initials: String get() = name.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
        .take(2).joinToString("") { it.take(1).uppercase() }.ifEmpty { "?" }
}

data class SosTileConfig(val contacts: List<SosFavorite> = emptyList(), val directCall: Boolean = false)

internal fun validFavoriteNumber(number: String): Boolean =
    number.matches(Regex("^[+]?[0-9]{2,20}$"))

internal fun validFavorites(contacts: List<SosFavorite>): Boolean = contacts.size <= 6 &&
    contacts.map { it.id }.distinct().size == contacts.size &&
    contacts.map { it.number }.distinct().size == contacts.size &&
    contacts.all { it.id.isNotBlank() && it.name.isNotBlank() && it.name.length <= 80 && validFavoriteNumber(it.number) &&
        (it.photo == null || it.photo.length <= 32768) }

object WidgetConfigs {
    private fun json(raw: String) = runCatching { JSONObject(raw) }.getOrElse { JSONObject() }
    fun speed(raw: String): SpeedTileConfig = json(raw).let {
        SpeedTileConfig(it.optString("units", "default").takeIf { unit -> unit in listOf("default", "kmh", "mph") }
            ?: "default", it.optBoolean("showStatus", true))
    }
    fun speedJson(original: String, config: SpeedTileConfig): String = json(original)
        .put("units", config.units).put("showStatus", config.showStatus).toString()
    fun sos(raw: String): SosTileConfig {
        val root = json(raw)
        val array = root.optJSONArray("contacts") ?: JSONArray()
        val contacts = (0 until minOf(array.length(), 6)).mapNotNull { index ->
            array.optJSONObject(index)?.let { item ->
                SosFavorite(item.optString("id"), item.optString("name"), item.optString("number"),
                    item.optString("photo").takeIf { it.isNotBlank() && it.length <= 32768 })
            }
        }
        return SosTileConfig(if (validFavorites(contacts)) contacts else emptyList(), root.optBoolean("directCall", false))
    }
    fun sosJson(original: String, config: SosTileConfig): String {
        require(validFavorites(config.contacts))
        val contacts = JSONArray()
        config.contacts.forEach { contact -> contacts.put(JSONObject().put("id", contact.id)
            .put("name", contact.name).put("number", contact.number).put("photo", contact.photo)) }
        return json(original).put("contacts", contacts).put("directCall", config.directCall).toString()
    }
}
