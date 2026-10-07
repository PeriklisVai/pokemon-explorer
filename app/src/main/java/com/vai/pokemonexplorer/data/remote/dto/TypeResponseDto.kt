package com.vai.pokemonexplorer.data.remote.dto

data class TypeResponseDto(
    val pokemon: List<TypePokemonDto>
)

data class TypePokemonDto(
    val pokemon: PokemonReferenceDto
)

data class PokemonReferenceDto(
    val name: String,
    val url: String
)
