package com.spicyfy.app.network

/**
 * Ponto único de configuração do backend.
 *
 * Quando o backend (conta, playlists, sincronização) estiver no ar,
 * troca só o BASE_URL aqui — nenhuma outra parte do app deveria
 * conhecer a URL diretamente.
 */
object ApiConfig {
    const val BASE_URL = "https://api.spicyfy.app/" // TODO: apontar pro backend real quando existir
}
