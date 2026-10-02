package com.vai.pokemonexplorer.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vai.pokemonexplorer.ui.theme.PokemonExplorerTheme

@Composable
fun PokemonListScreen(
    type: String,
    pokemonNames: List<String>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "$type Pokémon",
            fontSize = 28.sp
        )

        LazyColumn(
            modifier = Modifier.padding(top = 16.dp)
        ) {
            items(pokemonNames) { pokemonName ->
                Text(
                    text = pokemonName,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PokemonListScreenPreview() {
    PokemonExplorerTheme {
        PokemonListScreen(
            type = "Fire",
            pokemonNames = listOf(
                "charmander",
                "charmeleon",
                "charizard"
            )
        )
    }
}