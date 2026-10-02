package com.vai.pokemonexplorer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vai.pokemonexplorer.data.repository.PokemonRepository
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.vai.pokemonexplorer.ui.model.PokemonListItem

class PokemonListViewModel(
    private val repository: PokemonRepository
) : ViewModel() {

    var pokemonList by mutableStateOf<List<PokemonListItem>>(emptyList())
        private set

    fun loadPokemonByType(type: String) {
        viewModelScope.launch {

            val response = repository.getPokemonByType(
                type.lowercase()
            )

            pokemonList = response.pokemon
                .take(10)
                .map { item ->

                    val details = repository.getPokemonDetails(
                        item.pokemon.url
                    )

                    PokemonListItem(
                        name = item.pokemon.name,
                        imageUrl = details.sprites.front_default,
                        detailsUrl = item.pokemon.url
                    )
                }
        }
    }
}