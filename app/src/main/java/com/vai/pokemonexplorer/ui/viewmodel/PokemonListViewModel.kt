package com.vai.pokemonexplorer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vai.pokemonexplorer.data.repository.PokemonRepository
import kotlinx.coroutines.Job
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

    var searchQuery by mutableStateOf("")
        private set

    var isLoadingMore by mutableStateOf(false)
        private set

    var hasMore by mutableStateOf(true)
        private set

    private var allPokemonResults = emptyList<TypePokemonDto>()

    private var filteredPokemonResults = emptyList<TypePokemonDto>()

    private var loadedCount = 0

    private var searchJob: Job? = null

    private var loadJob: Job? = null

    private var loadedType: String? = null

    fun loadPokemonByType(type: String) {
        if (loadedType == type) return

        loadedType = type

        viewModelScope.launch {

            val response = repository.getPokemonByType(
                type.lowercase()
            )

            allPokemonResults = response.pokemon
            filteredPokemonResults = allPokemonResults
            searchQuery = ""
            loadedCount = 0
            pokemonList = emptyList()
            hasMore = true

            loadNextPokemon()
        }
    }

    fun onSearchQueryChange(query: String) {
        searchQuery = query

        val previousLoadJob = loadJob
        previousLoadJob?.cancel()
        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            previousLoadJob?.join()

            filteredPokemonResults =
                if (query.isBlank()) {
                    allPokemonResults
                } else {
                    allPokemonResults.filter { item ->
                        item.pokemon.name.startsWith(
                            query,
                            ignoreCase = true
                        )
                    }
                }

            loadedCount = 0
            pokemonList = emptyList()
            hasMore = filteredPokemonResults.isNotEmpty()

            if (filteredPokemonResults.isNotEmpty()) {
                loadNextPokemon()
            }
        }
    }

    fun loadNextPokemon() {
        if (isLoadingMore || !hasMore) return

        isLoadingMore = true

        loadJob = viewModelScope.launch {
            try {
                val nextPokemonBatch = filteredPokemonResults
                    .drop(loadedCount)
                    .take(10)

                val newPokemonItems = nextPokemonBatch.map { item ->
                    PokemonListItem(
                        name = item.pokemon.name,
                        imageUrl = null,
                        detailsUrl = item.pokemon.url
                    )
                }

                pokemonList = pokemonList + newPokemonItems
                loadedCount += newPokemonItems.size
                hasMore = loadedCount < filteredPokemonResults.size

                nextPokemonBatch.forEach { item ->
                    val details = repository.getPokemonDetails(
                        item.pokemon.url
                    )

                    pokemonList = pokemonList.map { pokemon ->
                        if (pokemon.detailsUrl == item.pokemon.url) {
                            pokemon.copy(
                                imageUrl = details.sprites.front_default
                            )
                        } else {
                            pokemon
                        }
                    }
                }
            } finally {
                isLoadingMore = false
            }
        }
    }
}
