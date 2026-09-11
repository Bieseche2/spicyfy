package com.spicyfy.app.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.spicyfy.app.data.model.Playlist
import com.spicyfy.app.data.model.Track
import java.util.UUID

class PlaylistRepository(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences("spicyfy_playlists", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun getAll(): List<Playlist> {
        val json = prefs.getString(KEY_PLAYLISTS, null) ?: return emptyList()
        val type = object : TypeToken<List<Playlist>>() {}.type
        return runCatching { gson.fromJson<List<Playlist>>(json, type) }.getOrDefault(emptyList())
    }

    fun create(name: String): Playlist {
        val playlist = Playlist(id = UUID.randomUUID().toString(), name = name)
        saveAll(getAll() + playlist)
        return playlist
    }

    fun addTrack(playlistId: String, track: Track) {
        val updated = getAll().map { playlist ->
            if (playlist.id == playlistId && playlist.tracks.none { it.id == track.id }) {
                playlist.copy(tracks = playlist.tracks + track)
            } else {
                playlist
            }
        }
        saveAll(updated)
    }

    fun removeTrack(playlistId: String, trackId: String) {
        val updated = getAll().map { playlist ->
            if (playlist.id == playlistId) {
                playlist.copy(tracks = playlist.tracks.filterNot { it.id == trackId })
            } else {
                playlist
            }
        }
        saveAll(updated)
    }

    fun delete(playlistId: String) {
        saveAll(getAll().filterNot { it.id == playlistId })
    }

    private fun saveAll(playlists: List<Playlist>) {
        prefs.edit().putString(KEY_PLAYLISTS, gson.toJson(playlists)).apply()
    }

    companion object {
        private const val KEY_PLAYLISTS = "playlists_json"
    }
}
