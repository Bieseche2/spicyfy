package com.spicyfy.app.data.model

/**
 * Playlist criada pelo usuário. Fica salva localmente e, quando o backend
 * estiver de pé, sincronizada pela PlaylistRepository.
 */
data class Playlist(
    val id: String,
    val name: String,
    val trackIds: List<String>,
    val coverUrl: String? = null
)
