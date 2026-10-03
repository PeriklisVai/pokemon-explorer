package com.vai.pokemonexplorer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vai.pokemonexplorer.data.repository.PokemonRepository
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.vai.pokemonexplorer.data.remote.dto.TypePokemonDto
import com.vai.pokemonexplorer.ui.model.PokemonListItem

class PokemonListViewModel(
    private val repository: PokemonRepository
) : ViewModel() {

    var pokemonList by mutableStateOf<List<PokemonListItem>>(emptyList())
        private set

    var isLoadingMore by mutableStateOf(false)
        private set

    var hasMore by mutableStateOf(true)
        private set

    private var allPokemon = emptyList<TypePokemonDto>()

    private var loadedCount = 0

    fun loadPokemonByType(type: String) {
        viewModelScope.launch {

            val response = repository.getPokemonByType(
                type.lowercase()
            )

            allPokemon = response.pokemon
            loadedCount = 0
            pokemonList = emptyList()
            hasMore = true

            loadNextPokemon()
        }
    }

    fun loadNextPokemon() {
        if (isLoadingMore || !hasMore) return

        isLoadingMore = true

        viewModelScope.launch {
            try {
                val nextPokemon = allPokemon
                    .drop(loadedCount)
                    .take(10)

                val newItems = nextPokemon.map { item ->
                    val details = repository.getPokemonDetails(
                        item.pokemon.url
                    )

                    PokemonListItem(
                        name = item.pokemon.name,
                        imageUrl = details.sprites.front_default,
                        detailsUrl = item.pokemon.url
                    )
                }

                pokemonList = pokemonList + newItems
                loadedCount += newItems.size
                hasMore = loadedCount < allPokemon.size
            } finally {
                isLoadingMore = false
            }
        }
    }
}
