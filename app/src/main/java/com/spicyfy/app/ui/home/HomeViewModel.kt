package com.spicyfy.app.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.spicyfy.app.R
import com.spicyfy.app.data.model.Playlist
import com.spicyfy.app.data.model.Track
import com.spicyfy.app.data.repository.PlaylistRepository
import com.spicyfy.app.data.repository.RecentlyPlayedRepository
import com.spicyfy.app.extractor.YoutubeMusicSource
import kotlinx.coroutines.launch

data class RecommendationRow(val title: String, val tracks: List<Track>)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val recentlyPlayedRepository = RecentlyPlayedRepository(application)
    private val playlistRepository = PlaylistRepository(application)
    private val musicSource = YoutubeMusicSource()

    private val _recentTracks = MutableLiveData<List<Track>>(emptyList())
    val recentTracks: LiveData<List<Track>> = _recentTracks

    private val _playlists = MutableLiveData<List<Playlist>>(emptyList())
    val playlists: LiveData<List<Playlist>> = _playlists

    private val _recommendationRows = MutableLiveData<List<RecommendationRow>>(emptyList())
    val recommendationRows: LiveData<List<RecommendationRow>> = _recommendationRows

    fun refresh() {
        val recent = recentlyPlayedRepository.getAll()
        _recentTracks.value = recent
        _playlists.value = playlistRepository.getAll()

        viewModelScope.launch {
            val rows = mutableListOf<RecommendationRow>()
            val context = getApplication<Application>()

            if (recent.isEmpty()) {
                val generic = runCatching { musicSource.genericRecommendations() }.getOrDefault(emptyList())
                if (generic.isNotEmpty()) {
                    rows.add(RecommendationRow(context.getString(R.string.home_section_recommended), generic))
                }
            } else {
                recent.take(3).forEach { seed ->
                    val related = runCatching { musicSource.relatedTracks(seed.sourceVideoId) }.getOrDefault(emptyList())
                    if (related.isNotEmpty()) {
                        rows.add(RecommendationRow(context.getString(R.string.home_based_on, seed.title), related))
                    }
                }
            }

            _recommendationRows.value = rows
        }
    }
}
