package com.qrmailer.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.qrmailer.data.models.SendHistoryEntry

private const val PREFS_NAME = "qrmailer_history"
private const val KEY_ENTRIES = "send_history_entries"
private const val MAX_ENTRIES = 100

/**
 * Persists send history in SharedPreferences (JSON array).
 * Newest first; capped at [MAX_ENTRIES].
 */
class SendHistoryRepository(private val context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()
    private val type = object : TypeToken<List<SendHistoryEntry>>() {}.type

    fun add(entry: SendHistoryEntry) {
        val list = getAll().toMutableList()
        list.add(0, entry)
        val trimmed = list.take(MAX_ENTRIES)
        prefs.edit().putString(KEY_ENTRIES, gson.toJson(trimmed)).apply()
    }

    fun getAll(): List<SendHistoryEntry> {
        val json = prefs.getString(KEY_ENTRIES, null) ?: return emptyList()
        return try {
            gson.fromJson(json, type) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun clear() {
        prefs.edit().remove(KEY_ENTRIES).apply()
    }
}
