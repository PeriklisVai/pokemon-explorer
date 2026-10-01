package com.vai.pokemonexplorer.data.repository

import com.vai.pokemonexplorer.data.remote.PokeApi
import com.vai.pokemonexplorer.data.remote.dto.PokemonDetailsDto
import com.vai.pokemonexplorer.data.remote.dto.TypeResponseDto

class PokemonRepository(
    private val api: PokeApi
) {

    suspend fun getPokemonByType(type: String): TypeResponseDto {
        return api.getPokemonByType(type)
    }

    suspend fun getPokemonDetails(url: String): PokemonDetailsDto {
        return api.getPokemonDetails(url)
    }
}