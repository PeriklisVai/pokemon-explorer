package com.vai.pokemonexplorer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import com.vai.pokemonexplorer.ui.theme.PokemonExplorerTheme
import com.vai.pokemonexplorer.ui.navigation.AppNavigation


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        WindowCompat.getInsetsController(
            window,
            window.decorView
        ).isAppearanceLightStatusBars = true

        setContent {
            PokemonExplorerTheme(darkTheme = false) {
                AppNavigation()
            }
        }
    }
}
