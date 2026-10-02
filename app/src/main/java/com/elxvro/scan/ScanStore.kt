package com.elxvro.scan

import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

data class ScanItem(
    val id: String,
    val value: String,
    val format: String,
    val kind: String,
    val time: Long,
    val favorite: Boolean
)

class ScanStore(private val prefs: SharedPreferences) {
    private val key = "scan_history_v1"

    fun list(): MutableList<ScanItem> {
        val raw = prefs.getString(key, "[]") ?: "[]"
        return runCatching {
            val array = JSONArray(raw)
            MutableList(array.length()) { index ->
                val o = array.getJSONObject(index)
                ScanItem(
                    id = o.optString("id"),
                    value = o.optString("value"),
                    format = o.optString("format"),
                    kind = o.optString("kind"),
                    time = o.optLong("time"),
                    favorite = o.optBoolean("favorite", false)
                )
            }
        }.getOrElse { mutableListOf() }
    }

    fun add(value: String, format: String, kind: String): ScanItem {
        val items = list()
        val item = ScanItem(UUID.randomUUID().toString(), value, format, kind, System.currentTimeMillis(), false)
        items.add(0, item)
        save(items.take(500))
        return item
    }

    fun toggleFavorite(id: String) {
        save(HistoryLogic.toggleFavorite(list(), id))
    }

    fun setFavorite(ids: Set<String>, favorite: Boolean) {
        if (ids.isEmpty()) return
        save(list().map { item -> if (item.id in ids) item.copy(favorite = favorite) else item })
    }

    fun delete(id: String) {
        save(HistoryLogic.delete(list(), id))
    }

    fun delete(ids: Set<String>) {
        if (ids.isEmpty()) return
        save(list().filterNot { it.id in ids })
    }

    fun clear() = prefs.edit().remove(key).apply()

    private fun save(items: List<ScanItem>) {
        val array = JSONArray()
        items.forEach { item ->
            array.put(JSONObject().apply {
                put("id", item.id)
                put("value", item.value)
                put("format", item.format)
                put("kind", item.kind)
                put("time", item.time)
                put("favorite", item.favorite)
            })
        }
        prefs.edit().putString(key, array.toString()).apply()
    }
}
