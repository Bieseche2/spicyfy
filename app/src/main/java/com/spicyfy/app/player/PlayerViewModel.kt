package com.spicyfy.app.player

import android.app.Application
import android.content.ComponentName
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.spicyfy.app.data.model.Track
import com.spicyfy.app.lyrics.LyricLine
import com.spicyfy.app.lyrics.LyricsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "PlayerViewModel"

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    private var controller: MediaController? = null
    private val lyricsRepository = LyricsRepository()

    private var pendingPlay: Pair<List<Track>, Int>? = null

    private val _currentTrack = MutableLiveData<Track?>(null)
    val currentTrack: LiveData<Track?> = _currentTrack

    private val _isPlaying = MutableLiveData(false)
    val isPlaying: LiveData<Boolean> = _isPlaying

    private val _progressPercent = MutableLiveData(0)
    val progressPercent: LiveData<Int> = _progressPercent

    private val _positionMs = MutableLiveData(0L)
    val positionMs: LiveData<Long> = _positionMs

    private val _positionText = MutableLiveData("0:00")
    val positionText: LiveData<String> = _positionText

    private val _durationText = MutableLiveData("0:00")
    val durationText: LiveData<String> = _durationText

    private val _lyrics = MutableLiveData<List<LyricLine>?>(null)
    val lyrics: LiveData<List<LyricLine>?> = _lyrics

    private val _isReady = MutableLiveData(false)
    val isReady: LiveData<Boolean> = _isReady

    private var progressJob: Job? = null
    private var lyricsJob: Job? = null

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _isPlaying.value = isPlaying
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            val item = mediaItem ?: return
            val metadata = item.mediaMetadata
            val track = Track(
                id = item.mediaId,
                title = metadata.title?.toString().orEmpty(),
                artist = metadata.artist?.toString().orEmpty(),
                coverUrl = metadata.artworkUri?.toString(),
                durationMs = controller?.duration?.coerceAtLeast(0) ?: 0L,
                sourceVideoId = item.mediaId
            )
            _currentTrack.value = track
            fetchLyricsFor(track)
        }

        override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
            Log.e(TAG, "Erro de reprodução: ${error.errorCodeName} - ${error.message}", error)
        }
    }

    init {
        val context = getApplication<Application>()
        val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture.addListener(
            {
                try {
                    controller = controllerFuture.get().also { it.addListener(playerListener) }
                    _isReady.value = true
                    startProgressLoop()
                    Log.d(TAG, "MediaController conectado com sucesso")

                    pendingPlay?.let { (tracks, index) ->
                        Log.d(TAG, "Executando play() pendente pra: ${tracks.getOrNull(index)?.title}")
                        pendingPlay = null
                        playInternal(tracks, index)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Falha ao conectar o MediaController", e)
                }
            },
            MoreExecutors.directExecutor()
        )
    }

    fun play(tracks: List<Track>, startIndex: Int = 0) {
        if (tracks.isEmpty()) return
        if (controller == null) {
            Log.d(TAG, "Controller ainda não pronto, guardando pedido de play()")
            pendingPlay = tracks to startIndex
            return
        }
        playInternal(tracks, startIndex)
    }

    fun play(track: Track) = play(listOf(track), 0)

    private fun playInternal(tracks: List<Track>, startIndex: Int) {
        val safeIndex = startIndex.coerceIn(0, tracks.lastIndex)
        val mediaItems = tracks.map { it.toMediaItem() }
        val track = tracks[safeIndex]

        _currentTrack.value = track
        fetchLyricsFor(track)

        controller?.apply {
            setMediaItems(mediaItems, safeIndex, 0L)
            prepare()
            play()
        }
    }

    fun togglePlayPause() {
        controller?.let { if (it.isPlaying) it.pause() else it.play() }
    }

    fun nextTrack() {
        controller?.seekToNext()
    }

    fun previousTrack() {
        controller?.seekToPrevious()
    }

    fun seekToPercent(percent: Int) {
        val c = controller ?: return
        val duration = c.duration
        if (duration > 0) {
            c.seekTo((duration * percent / 100).coerceIn(0, duration))
        }
    }

    private fun Track.toMediaItem(): MediaItem =
        MediaItem.Builder()
            .setMediaId(sourceVideoId)
            .setUri("spicyfy://$sourceVideoId") // placeholder — resolvido de verdade no PlaybackService
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(title)
                    .setArtist(artist)
                    .apply { coverUrl?.let { setArtworkUri(it.toUri()) } }
                    .build()
            )
            .build()

    private fun fetchLyricsFor(track: Track) {
        lyricsJob?.cancel()
        _lyrics.value = null
        lyricsJob = viewModelScope.launch {
            val result = runCatching {
                lyricsRepository.fetchLyrics(track.artist, track.title, track.durationMs)
            }.getOrNull()
            _lyrics.value = result?.synced
        }
    }

    private fun startProgressLoop() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (true) {
                val c = controller
                if (c != null && c.duration > 0) {
                    val position = c.currentPosition.coerceIn(0, c.duration)
                    _progressPercent.value = ((position * 100) / c.duration).toInt()
                    _positionMs.value = position
                    _positionText.value = formatTime(position)
                    _durationText.value = formatTime(c.duration)
                }
                delay(500)
            }
        }
    }

    private fun formatTime(ms: Long): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "%d:%02d".format(minutes, seconds)
    }

    override fun onCleared() {
        progressJob?.cancel()
        lyricsJob?.cancel()
        controller?.release()
        controller = null
        super.onCleared()
    }
}
