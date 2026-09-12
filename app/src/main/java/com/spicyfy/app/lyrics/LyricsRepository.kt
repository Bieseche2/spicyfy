package com.spicyfy.app.lyrics

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit
import kotlin.math.abs

data class LyricLine(val timeMs: Long, val text: String)

data class LyricsResult(
    val synced: List<LyricLine>?,
    val plain: String?
)

private data class LrcLibItem(
    val trackName: String?,
    val artistName: String?,
    val duration: Double?,
    val instrumental: Boolean = false,
    val plainLyrics: String?,
    val syncedLyrics: String?
)

private interface LrcLibApi {
    @GET("api/search")
    suspend fun search(
        @Query("track_name") trackName: String,
        @Query("artist_name") artistName: String
    ): List<LrcLibItem>
}

class LyricsRepository {

    private val api: LrcLibApi = Retrofit.Builder()
        .baseUrl("https://lrclib.net/")
        .client(OkHttpClient.Builder().readTimeout(15, TimeUnit.SECONDS).build())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(LrcLibApi::class.java)

    suspend fun fetchLyrics(artist: String, title: String, durationMs: Long): LyricsResult? =
        withContext(Dispatchers.IO) {
            val cleanTitle = cleanTrackTitle(title)
            val cleanArtist = cleanArtistName(artist)

            val results = runCatching { api.search(trackName = cleanTitle, artistName = cleanArtist) }
                .getOrNull()
                ?.filterNot { it.instrumental }
                ?: return@withContext null

            if (results.isEmpty()) return@withContext null

            val durationSec = durationMs / 1000.0
            val best = results
                .sortedWith(
                    compareByDescending<LrcLibItem> { it.syncedLyrics != null }
                        .thenBy { abs((it.duration ?: durationSec) - durationSec) }
                )
                .firstOrNull() ?: return@withContext null

            LyricsResult(
                synced = best.syncedLyrics?.let { parseLrc(it) },
                plain = best.plainLyrics
            )
        }

    private fun cleanTrackTitle(raw: String): String {
        return raw
            .replace(Regex("""[(\[][^)\]]*(official|audio|video|lyrics?|visualizer|hd|4k|remaster\w*)[^)\]]*[)\]]""", RegexOption.IGNORE_CASE), "")
            .replace(Regex("""\b(feat\.?|ft\.?)\s.+$""", RegexOption.IGNORE_CASE), "")
            .replace(Regex("""\s{2,}"""), " ")
            .trim()
    }

    private fun cleanArtistName(raw: String): String {
        return raw.removeSuffix(" - Topic").trim()
    }

    private fun parseLrc(lrc: String): List<LyricLine> {
        val lineRegex = Regex("""\[(\d{1,2}):(\d{2}(?:\.\d{1,3})?)]([^\n\[]*)""")
        return lineRegex.findAll(lrc).mapNotNull { match ->
            val (minutes, seconds, text) = match.destructured
            val timeMs = ((minutes.toLong() * 60 + seconds.toDouble()) * 1000).toLong()
            val trimmed = text.trim()
            if (trimmed.isEmpty()) null else LyricLine(timeMs, trimmed)
        }.sortedBy { it.timeMs }.toList()
    }
}
