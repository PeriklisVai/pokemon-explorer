package com.vai.pokemonexplorer.data.remote

import com.vai.pokemonexplorer.data.remote.dto.PokemonDetailsDto
import com.vai.pokemonexplorer.data.remote.dto.TypeResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

interface PokeApi {

    @GET("type/{type}")
    suspend fun getPokemonByType(
        @Path("type") type: String
    ): TypeResponseDto

    @GET
    suspend fun getPokemonDetails(
        @Url url: String
    ): PokemonDetailsDto
}