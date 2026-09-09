package com.spicyfy.app.player

import androidx.media3.session.MediaSessionService

/**
 * Serviço em primeiro plano que mantém a música tocando com a tela desligada
 * e expõe os controles pra notificação/tela de bloqueio via Media3.
 */
class PlaybackService : MediaSessionService() {
    // TODO: criar o ExoPlayer + MediaSession aqui e conectar ao YoutubeMusicSource
    // pra resolver a stream de áudio antes de tocar cada faixa
}
