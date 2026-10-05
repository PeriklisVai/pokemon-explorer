package com.vai.pokemonexplorer.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vai.pokemonexplorer.data.remote.dto.PokemonDetailsDto
import com.vai.pokemonexplorer.data.repository.PokemonRepository
import com.vai.pokemonexplorer.ui.model.ErrorUiState
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.SocketTimeoutException
import retrofit2.HttpException

class PokemonDetailsViewModel(
    private val repository: PokemonRepository
) : ViewModel() {

    var pokemonDetails by mutableStateOf<PokemonDetailsDto?>(null)
        private set

    var errorState by mutableStateOf<ErrorUiState?>(null)
        private set

    fun loadPokemonDetails(detailsUrl: String) {
        errorState = null

        viewModelScope.launch {
            try {
                pokemonDetails = repository.getPokemonDetails(detailsUrl)
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
            }
        }
    }
}
