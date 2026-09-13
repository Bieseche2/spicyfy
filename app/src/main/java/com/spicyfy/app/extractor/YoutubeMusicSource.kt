package com.spicyfy.app.extractor

import com.spicyfy.app.data.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.stream.StreamInfo
import org.schabi.newpipe.extractor.stream.StreamInfoItem

class YoutubeMusicSource {

    private val youtube = ServiceList.YouTube

    suspend fun search(query: String): List<Track> = withContext(Dispatchers.IO) {
        val handler = youtube.searchQHFactory.fromQuery(query, listOf("music_songs"), "")
        val extractor = youtube.getSearchExtractor(handler)
        extractor.fetchPage()

        extractor.initialPage.items
            .filterIsInstance<StreamInfoItem>()
            .mapNotNull { it.toTrackOrNull() }
    }

    suspend fun resolveAudioStreamUrl(videoId: String): String? = withContext(Dispatchers.IO) {
        val info = StreamInfo.getInfo(youtube, watchUrlFor(videoId))
        info.audioStreams
            .maxByOrNull { it.averageBitrate }
            ?.content
    }

    suspend fun relatedTracks(videoId: String): List<Track> = withContext(Dispatchers.IO) {
        val info = StreamInfo.getInfo(youtube, watchUrlFor(videoId))
        info.relatedItems
            .filterIsInstance<StreamInfoItem>()
            .mapNotNull { it.toTrackOrNull() }
    }

    suspend fun genericRecommendations(): List<Track> = withContext(Dispatchers.IO) {
        val seeds = listOf("músicas mais tocadas 2026", "top hits internacional", "as melhores do momento")
        search(seeds.random())
    }

    private fun watchUrlFor(videoId: String) = "https://www.youtube.com/watch?v=$videoId"

    private fun StreamInfoItem.toTrackOrNull(): Track? {
        val videoId = extractVideoId(url) ?: return null
        return Track(
            id = videoId,
            title = name,
            artist = uploaderName ?: "",
            coverUrl = thumbnails.firstOrNull()?.url,
            durationMs = duration * 1000,
            sourceVideoId = videoId
        )
    }

    private fun extractVideoId(videoUrl: String): String? {
        return videoUrl.substringAfter("v=", missingDelimiterValue = "")
            .substringBefore("&")
            .ifEmpty { null }
    }
}
