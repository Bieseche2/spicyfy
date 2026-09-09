package com.spicyfy.app.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Activity única do app (arquitetura single-activity).
 * Hospeda a navegação entre Home, Buscar, Tocando agora e Biblioteca.
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: setContentView + configurar NavController entre ui/home, ui/search, ui/nowplaying, ui/library
    }
}
