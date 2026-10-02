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
import com.vai.pokemonexplorer.ui.model.PokemonListItem
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.size
import coil3.compose.AsyncImage


@Composable
fun PokemonListScreen(
    type: String,
    pokemonList: List<PokemonListItem>,
    onPokemonClick: (String) -> Unit
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
            items(pokemonList) { pokemon ->
                PokemonListItemRow(
                    pokemon = pokemon,
                    onClick = {
                        onPokemonClick(pokemon.detailsUrl)
                    },
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun PokemonListItemRow(
    pokemon: PokemonListItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color.LightGray,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable {
                onClick()
            }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        AsyncImage(
            model = pokemon.imageUrl,
            contentDescription = pokemon.name,
            modifier = Modifier.size(72.dp)
        )

        Text(
            text = pokemon.name,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun PokemonListScreenPreview() {
    PokemonExplorerTheme {
        PokemonListScreen(
            type = "Fire",
            pokemonList = listOf(
                PokemonListItem(
                    name = "charmander",
                    imageUrl = null,
                    detailsUrl = "https://pokeapi.co/api/v2/pokemon/4/"
                ),
                PokemonListItem(
                    name = "charmeleon",
                    imageUrl = null,
                    detailsUrl = "https://pokeapi.co/api/v2/pokemon/5/"
                ),
                PokemonListItem(
                    name = "charizard",
                    imageUrl = null,
                    detailsUrl = "https://pokeapi.co/api/v2/pokemon/6/"
                )
            ),
            onPokemonClick = { }
        )
    }
}