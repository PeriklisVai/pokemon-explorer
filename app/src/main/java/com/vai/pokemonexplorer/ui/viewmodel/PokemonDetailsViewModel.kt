package com.vai.pokemonexplorer.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vai.pokemonexplorer.data.remote.dto.PokemonDetailsDto
import com.vai.pokemonexplorer.data.repository.PokemonRepository
import kotlinx.coroutines.launch

class PokemonDetailsViewModel(
    private val repository: PokemonRepository
) : ViewModel() {

    var pokemonDetails by mutableStateOf<PokemonDetailsDto?>(null)
        private set

    fun loadPokemonDetails(detailsUrl: String) {
        viewModelScope.launch {
            pokemonDetails = repository.getPokemonDetails(detailsUrl)
        }
    }
}