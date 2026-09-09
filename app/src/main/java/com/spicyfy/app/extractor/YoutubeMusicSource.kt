package com.spicyfy.app.extractor

import com.spicyfy.app.data.model.Track

/**
 * Camada responsável por falar com o NewPipeExtractor:
 * - buscar músicas/artistas por texto
 * - resolver a URL de áudio de um vídeo específico
 * - extrair metadados (título, artista, capa, duração)
 *
 * Nenhuma tela deve chamar o NewPipeExtractor diretamente — tudo passa por aqui,
 * assim trocar de fonte de áudio no futuro não afeta o resto do app.
 */
class YoutubeMusicSource {

    suspend fun search(query: String): List<Track> {
        // TODO: usar NewPipeExtractor pra pesquisar e mapear resultados pra Track
        return emptyList()
    }

    suspend fun resolveAudioStreamUrl(videoId: String): String? {
        // TODO: resolver a melhor stream de áudio disponível pro vídeo
        return null
    }
}
