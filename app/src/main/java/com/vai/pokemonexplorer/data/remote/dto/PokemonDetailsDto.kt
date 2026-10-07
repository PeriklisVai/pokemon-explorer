package com.vai.pokemonexplorer.data.remote.dto

data class PokemonDetailsDto(
    val name: String,
    val sprites: SpritesDto,
    val stats: List<StatDto>,
    val types: List<PokemonTypeSlotDto>
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

data class PokemonTypeSlotDto(
    val slot: Int,
    val type: PokemonTypeDto
)

data class PokemonTypeDto(
    val name: String
)
