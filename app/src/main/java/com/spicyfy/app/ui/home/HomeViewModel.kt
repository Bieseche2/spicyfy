package com.spicyfy.app.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.spicyfy.app.data.model.Playlist
import com.spicyfy.app.data.model.Track
import com.spicyfy.app.data.repository.PlaylistRepository
import com.spicyfy.app.data.repository.RecentlyPlayedRepository
import com.spicyfy.app.extractor.YoutubeMusicSource
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val recentlyPlayedRepository = RecentlyPlayedRepository(application)
    private val playlistRepository = PlaylistRepository(application)
    private val musicSource = YoutubeMusicSource()

    private val _recentTracks = MutableLiveData<List<Track>>(emptyList())
    val recentTracks: LiveData<List<Track>> = _recentTracks

    private val _playlists = MutableLiveData<List<Playlist>>(emptyList())
    val playlists: LiveData<List<Playlist>> = _playlists

    private val _recommended = MutableLiveData<List<Track>>(emptyList())
    val recommended: LiveData<List<Track>> = _recommended

    fun refresh() {
        val recent = recentlyPlayedRepository.getAll()
        _recentTracks.value = recent
        _playlists.value = playlistRepository.getAll()

        viewModelScope.launch {
            _recommended.value = runCatching {
                val seed = recent.firstOrNull()
                if (seed != null) {
                    musicSource.relatedTracks(seed.sourceVideoId)
                } else {
                    musicSource.genericRecommendations()
                }
            }.getOrDefault(emptyList())
        }
    }
}
