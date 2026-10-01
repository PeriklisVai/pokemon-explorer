package com.vai.pokemonexplorer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.vai.pokemonexplorer.ui.theme.PokemonExplorerTheme
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.vai.pokemonexplorer.data.remote.RetrofitInstance
import com.vai.pokemonexplorer.data.repository.PokemonRepository
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = PokemonRepository(RetrofitInstance.api)

        lifecycleScope.launch {
            try {
                val response = repository.getPokemonByType("fire")

                val firstPokemon = response.pokemon.first()

                val details = repository.getPokemonDetails(
                    firstPokemon.pokemon.url
                )

                val hp = details.stats.find { it.stat.name == "hp" }?.base_stat
                val attack = details.stats.find { it.stat.name == "attack" }?.base_stat
                val defense = details.stats.find { it.stat.name == "defense" }?.base_stat

                Log.d("PokemonAPI", "Name: ${details.name}")
                Log.d("PokemonAPI", "Image: ${details.sprites.front_default}")
                Log.d("PokemonAPI", "HP: $hp")
                Log.d("PokemonAPI", "Attack: $attack")
                Log.d("PokemonAPI", "Defense: $defense")

            } catch (e: Exception) {
                Log.e("PokemonAPI", "Error fetching Pokemon", e)
            }
        }

        setContent {
            PokemonExplorerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PokemonExplorerTheme {
        Greeting("Android")
    }
}