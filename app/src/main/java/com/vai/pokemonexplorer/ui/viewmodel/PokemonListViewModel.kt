package com.vai.pokemonexplorer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vai.pokemonexplorer.data.repository.PokemonRepository
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class PokemonListViewModel(
    private val repository: PokemonRepository
) : ViewModel() {

    var pokemonNames by mutableStateOf<List<String>>(emptyList())
        private set

    fun loadPokemonByType(type: String) {
        viewModelScope.launch {

            val response = repository.getPokemonByType(
                type.lowercase()
            )

            pokemonNames = response.pokemon
                .take(10)
                .map { it.pokemon.name }
        }
    }
}