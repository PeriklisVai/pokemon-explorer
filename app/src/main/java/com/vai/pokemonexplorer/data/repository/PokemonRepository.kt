package com.vai.pokemonexplorer.data.repository

import com.vai.pokemonexplorer.data.remote.PokeApi
import com.vai.pokemonexplorer.data.remote.dto.PokemonDetailsDto
import com.vai.pokemonexplorer.data.remote.dto.TypeResponseDto

class PokemonRepository(
    private val api: PokeApi
) {

    private val pokemonDetailsCache = mutableMapOf<String, PokemonDetailsDto>()

    suspend fun getPokemonByType(type: String): TypeResponseDto {
        return api.getPokemonByType(type)
    }

    suspend fun getPokemonDetails(url: String): PokemonDetailsDto {
        pokemonDetailsCache[url]?.let { return it }

        val details = api.getPokemonDetails(url)
        pokemonDetailsCache[url] = details
        return details
    }
}
