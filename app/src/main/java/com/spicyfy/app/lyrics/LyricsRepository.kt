package com.spicyfy.app.lyrics

/**
 * Linha de letra sincronizada com um timestamp em milissegundos.
 */
data class LyricLine(val timeMs: Long, val text: String)

/**
 * Busca letras sincronizadas (LRC) por artista/título/duração.
 * Fonte planejada: API pública do LRCLIB. Se não achar letra sincronizada,
 * cair pra letra estática (sem timestamps) como fallback.
 */
class LyricsRepository {

    suspend fun fetchSyncedLyrics(artist: String, title: String, durationMs: Long): List<LyricLine>? {
        // TODO: chamar a API do LRCLIB e parsear o formato LRC
        return null
    }
}
