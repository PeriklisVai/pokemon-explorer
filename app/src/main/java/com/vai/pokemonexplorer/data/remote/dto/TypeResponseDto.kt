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

data class PokemonDetailsDto(
    val name: String,
    val sprites: SpritesDto,
    val stats: List<StatDto>
)

data class SpritesDto(
    val front_default: String?
)

data class StatDto(
    val base_stat: Int,
    val stat: StatNameDto
)

data class StatNameDto(
    val name: String
)