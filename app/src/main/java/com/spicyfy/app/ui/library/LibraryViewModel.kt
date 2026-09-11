package com.spicyfy.app.ui.library

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.spicyfy.app.data.model.Playlist
import com.spicyfy.app.data.model.Track
import com.spicyfy.app.data.repository.PlaylistRepository

class LibraryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PlaylistRepository(application)

    private val _playlists = MutableLiveData<List<Playlist>>(emptyList())
    val playlists: LiveData<List<Playlist>> = _playlists

    init {
        refresh()
    }

    fun refresh() {
        _playlists.value = repository.getAll()
    }

    fun createPlaylist(name: String) {
        if (name.isBlank()) return
        repository.create(name.trim())
        refresh()
    }

    fun addTrackToPlaylist(playlistId: String, track: Track) {
        repository.addTrack(playlistId, track)
        refresh()
    }

    fun deletePlaylist(playlistId: String) {
        repository.delete(playlistId)
        refresh()
    }
}
