package com.spicyfy.app.player

import android.app.Application
import android.content.ComponentName
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
import com.spicyfy.app.data.repository.DownloadRepository
import com.spicyfy.app.data.repository.RecentlyPlayedRepository
import com.spicyfy.app.extractor.YoutubeMusicSource
import com.spicyfy.app.lyrics.LyricLine
import com.spicyfy.app.lyrics.LyricsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    private var controller: MediaController? = null
    private val lyricsRepository = LyricsRepository()
    private val musicSource = YoutubeMusicSource()
    private val downloadRepository = DownloadRepository(application)
    private val trackDownloader = TrackDownloader(application)
    private val recentlyPlayedRepository = RecentlyPlayedRepository(application)

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

    private val _isDownloaded = MutableLiveData(false)
    val isDownloaded: LiveData<Boolean> = _isDownloaded

    private val _isDownloading = MutableLiveData(false)
    val isDownloading: LiveData<Boolean> = _isDownloading

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
            onTrackChanged(track)
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == Player.STATE_ENDED) {
                maybeAutoplay()
            }
        }
    }

    init {
        val context = getApplication<Application>()
        val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture.addListener(
            {
                controller = controllerFuture.get().also { it.addListener(playerListener) }
                startProgressLoop()

                pendingPlay?.let { (tracks, index) ->
                    pendingPlay = null
                    playInternal(tracks, index)
                }
            },
            MoreExecutors.directExecutor()
        )
    }

    fun play(tracks: List<Track>, startIndex: Int = 0) {
        if (tracks.isEmpty()) return
        if (controller == null) {
            pendingPlay = tracks to startIndex
            return
        }
        playInternal(tracks, startIndex)
    }

    fun play(track: Track) = play(listOf(track), 0)

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

    fun toggleDownload() {
        val track = _currentTrack.value ?: return
        if (_isDownloaded.value == true) {
            downloadRepository.delete(track.sourceVideoId)
            _isDownloaded.value = false
            return
        }
        if (_isDownloading.value == true) return

        _isDownloading.value = true
        viewModelScope.launch {
            val success = trackDownloader.download(track)
            _isDownloading.value = false
            _isDownloaded.value = success
        }
    }

    private fun playInternal(tracks: List<Track>, startIndex: Int) {
        val safeIndex = startIndex.coerceIn(0, tracks.lastIndex)
        val mediaItems = tracks.map { it.toMediaItem() }
        onTrackChanged(tracks[safeIndex])

        controller?.apply {
            setMediaItems(mediaItems, safeIndex, 0L)
            prepare()
            play()
        }
    }

    private fun onTrackChanged(track: Track) {
        _currentTrack.value = track
        _isDownloaded.value = downloadRepository.isDownloaded(track.sourceVideoId)
        fetchLyricsFor(track)
        recentlyPlayedRepository.record(track)
    }

    private fun maybeAutoplay() {
        val c = controller ?: return
        if (c.hasNextMediaItem()) return
        val lastVideoId = c.currentMediaItem?.mediaId ?: return
        viewModelScope.launch {
            val related = runCatching { musicSource.relatedTracks(lastVideoId) }.getOrDefault(emptyList())
            if (related.isNotEmpty()) {
                playInternal(related, 0)
            }
        }
    }

    private fun Track.toMediaItem(): MediaItem =
        MediaItem.Builder()
            .setMediaId(sourceVideoId)
            .setUri("spicyfy://$sourceVideoId")
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
