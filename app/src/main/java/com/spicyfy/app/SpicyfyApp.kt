package com.spicyfy.app

import android.app.Application

/**
 * Classe Application do Spicyfy.
 * Ponto único pra inicializar dependências globais (rede, player, locale) no futuro.
 */
class SpicyfyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // TODO: inicializar container de injeção de dependência (Hilt/Koin) quando o projeto crescer
    }
}
