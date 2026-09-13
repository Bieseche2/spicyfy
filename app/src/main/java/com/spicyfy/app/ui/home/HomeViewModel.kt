package com.spicyfy.app.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.spicyfy.app.data.model.Playlist
import com.spicyfy.app.data.model.Track
import com.spicyfy.app.data.repository.PlaylistRepository
import com.spicyfy.app.data.repository.RecentlyPlayedRepository

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val recentlyPlayedRepository = RecentlyPlayedRepository(application)
    private val playlistRepository = PlaylistRepository(application)

    private val _recentTracks = MutableLiveData<List<Track>>(emptyList())
    val recentTracks: LiveData<List<Track>> = _recentTracks

    private val _playlists = MutableLiveData<List<Playlist>>(emptyList())
    val playlists: LiveData<List<Playlist>> = _playlists

    fun refresh() {
        _recentTracks.value = recentlyPlayedRepository.getAll()
        _playlists.value = playlistRepository.getAll()
    }
}
