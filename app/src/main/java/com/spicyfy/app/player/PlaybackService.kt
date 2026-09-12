package com.spicyfy.app.player

import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.ResolvingDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.spicyfy.app.extractor.YoutubeMusicSource
import kotlinx.coroutines.runBlocking
import java.io.IOException

@OptIn(UnstableApi::class)
class PlaybackService : MediaSessionService() {

    private var mediaSession: MediaSession? = null
    private val musicSource = YoutubeMusicSource()

    override fun onCreate() {
        super.onCreate()

        val resolver = ResolvingDataSource.Resolver { dataSpec ->
            val videoId = dataSpec.uri.host
                ?: throw IOException("URI de mídia inválida: ${dataSpec.uri}")
            val audioUrl = runBlocking { musicSource.resolveAudioStreamUrl(videoId) }
                ?: throw IOException("Não foi possível resolver o áudio de $videoId")
            dataSpec.withUri(Uri.parse(audioUrl))
        }

        val dataSourceFactory = DataSource.Factory {
            ResolvingDataSource(DefaultHttpDataSource.Factory().createDataSource(), resolver)
        }

        val player = ExoPlayer.Builder(this)
            .setMediaSourceFactory(DefaultMediaSourceFactory(dataSourceFactory))
            .build()

        mediaSession = MediaSession.Builder(this, player).build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? =
        mediaSession

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }
}
