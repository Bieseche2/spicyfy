package com.spicyfy.app.player

import android.app.Application
import android.content.ComponentName
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.spicyfy.app.data.model.Track

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    private var controller: MediaController? = null

    private val _currentTrack = MutableLiveData<Track?>(null)
    val currentTrack: LiveData<Track?> = _currentTrack

    private val _isPlaying = MutableLiveData(false)
    val isPlaying: LiveData<Boolean> = _isPlaying

    private val _progressPercent = MutableLiveData(0)
    val progressPercent: LiveData<Int> = _progressPercent

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _isPlaying.value = isPlaying
        }
    }

    init {
        val context = getApplication<Application>()
        val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture.addListener(
            {
                controller = controllerFuture.get().also { it.addListener(playerListener) }
            },
            MoreExecutors.directExecutor()
        )
    }

    fun play(track: Track) {
        val mediaItem = MediaItem.Builder()
            .setMediaId(track.sourceVideoId)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(track.title)
                    .setArtist(track.artist)
                    .apply { track.coverUrl?.let { setArtworkUri(it.toUri()) } }
                    .build()
            )
            .build()

        _currentTrack.value = track
        controller?.apply {
            setMediaItem(mediaItem)
            prepare()
            play()
        }
    }

    fun togglePlayPause() {
        controller?.let { if (it.isPlaying) it.pause() else it.play() }
    }

    override fun onCleared() {
        controller?.release()
        controller = null
        super.onCleared()
    }
}
