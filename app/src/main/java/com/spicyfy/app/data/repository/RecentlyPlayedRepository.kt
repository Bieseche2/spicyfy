package com.spicyfy.app.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.spicyfy.app.data.model.Track

class RecentlyPlayedRepository(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences("spicyfy_recent", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun getAll(): List<Track> {
        val json = prefs.getString(KEY, null) ?: return emptyList()
        val type = object : TypeToken<List<Track>>() {}.type
        return runCatching { gson.fromJson<List<Track>>(json, type) }.getOrDefault(emptyList())
    }

    fun record(track: Track) {
        val updated = listOf(track) + getAll().filterNot { it.id == track.id }
        prefs.edit().putString(KEY, gson.toJson(updated.take(MAX_ITEMS))).apply()
    }

    companion object {
        private const val KEY = "recent_tracks_json"
        private const val MAX_ITEMS = 20
    }
}
