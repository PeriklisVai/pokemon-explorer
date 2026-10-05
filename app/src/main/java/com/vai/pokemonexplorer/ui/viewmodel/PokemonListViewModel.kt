package com.vai.pokemonexplorer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vai.pokemonexplorer.data.repository.PokemonRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.vai.pokemonexplorer.data.remote.dto.TypePokemonDto
import com.vai.pokemonexplorer.ui.model.ErrorUiState
import com.vai.pokemonexplorer.ui.model.PokemonListItem
import java.io.IOException
import java.net.SocketTimeoutException
import retrofit2.HttpException

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

    var errorState by mutableStateOf<ErrorUiState?>(null)
        private set

    private var allPokemonResults = emptyList<TypePokemonDto>()

    private var filteredPokemonResults = emptyList<TypePokemonDto>()

    val totalResultsCount: Int
        get() = filteredPokemonResults.size

    private var loadedCount = 0

    private var searchJob: Job? = null

    private var loadJob: Job? = null

    private var retryMissingImagesJob: Job? = null

    private var loadedType: String? = null

    fun loadPokemonByType(type: String) {
        if (loadedType == type) return

        errorState = null
        isLoadingMore = true

        viewModelScope.launch {
            var requestSucceeded = false

            try {
                val response = repository.getPokemonByType(
                    type.lowercase()
                )

                allPokemonResults = response.pokemon
                filteredPokemonResults = allPokemonResults
                searchQuery = ""
                loadedCount = 0
                pokemonList = emptyList()
                hasMore = true

                loadedType = type
                requestSucceeded = true
            } catch (_: SocketTimeoutException) {
                errorState = ErrorUiState(
                    title = "Request timed out",
                    message = "The request took too long. Please try again."
                )
            } catch (_: IOException) {
                errorState = ErrorUiState(
                    title = "No internet connection",
                    message = "Check your internet connection and try again."
                )
            } catch (_: HttpException) {
                errorState = ErrorUiState(
                    title = "Unable to load Pokémon",
                    message = "Something went wrong while contacting PokéAPI. Please try again."
                )
            } finally {
                isLoadingMore = false
            }

            if (requestSucceeded) {
                loadNextPokemon()
            }
        }
    }

    fun retryLoadPokemon(type: String) {
        errorState = null
        loadPokemonByType(type)
    }

    fun onSearchQueryChange(query: String) {
        searchQuery = query

        retryMissingImagesJob?.cancel()
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
            val nextPokemonBatch = filteredPokemonResults
                .drop(loadedCount)
                .take(10)

            try {
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
            } finally {
                isLoadingMore = false
            }

            nextPokemonBatch.forEach { item ->
                try {
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
                } catch (exception: CancellationException) {
                    throw exception
                } catch (_: Exception) {
                    // Keep the Pokémon row visible when its sprite request fails.
                }
            }
        }
    }

    fun retryMissingImages() {
        if (pokemonList.none { it.imageUrl == null }) return
        if (retryMissingImagesJob?.isActive == true) return

        retryMissingImagesJob = viewModelScope.launch {
            // Let an existing batch finish its sprite requests before retrying failures.
            loadJob?.join()

            pokemonList.filter { it.imageUrl == null }.forEach { item ->
                try {
                    val imageUrl = repository.getPokemonDetails(
                        item.detailsUrl
                    ).sprites.front_default ?: return@forEach

                    pokemonList = pokemonList.map { pokemon ->
                        if (
                            pokemon.detailsUrl == item.detailsUrl &&
                            pokemon.imageUrl == null
                        ) {
                            pokemon.copy(imageUrl = imageUrl)
                        } else {
                            pokemon
                        }
                    }
                } catch (exception: CancellationException) {
                    throw exception
                } catch (_: Exception) {
                    // Keep the row without an image so it can be retried later.
                }
            }
        }
    }
}
