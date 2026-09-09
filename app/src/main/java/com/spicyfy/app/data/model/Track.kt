package com.spicyfy.app.data.model

/**
 * Representa uma música dentro do app, independente da fonte de áudio.
 */
data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val coverUrl: String?,
    val durationMs: Long,
    val sourceVideoId: String
)
