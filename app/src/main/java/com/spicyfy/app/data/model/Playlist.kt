package com.spicyfy.app.data.model

data class Playlist(
    val id: String,
    val name: String,
    val tracks: List<Track> = emptyList()
)
