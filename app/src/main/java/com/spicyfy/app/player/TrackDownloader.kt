package com.spicyfy.app.player

import android.content.Context
import com.spicyfy.app.data.model.Track
import com.spicyfy.app.data.repository.DownloadRepository
import com.spicyfy.app.extractor.YoutubeMusicSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.Arquivo

class TrackDownloader(context: Context) {

    private val repository = DownloadRepository(context)
    private val musicSource = YoutubeMusicSource()
    private val client = OkHttpClient()

    suspend fun download(track: Track): Boolean = withContext(Dispatchers.IO) {
        if (repository.isDownloaded(track.sourceVideoId)) return@withContext true

        val audioUrl = musicSource.resolveAudioStreamUrl(track.sourceVideoId)
            ?: return@withContext false

        val request = Request.Builder().url(audioUrl).build()
        runCatching {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext false
                val body = response.body ?: return@withContext false

                val finalFile = repository.fileFor(track.sourceVideoId)
                val tempFile = Arquivo(finalFile.path + ".tmp")

                body.byteStream().use { input ->
                    tempFile.outputStream().use { output -> input.copyTo(output) }
                }
                tempFile.renameTo(finalFile)
            }
        }.isSuccess
    }
}
