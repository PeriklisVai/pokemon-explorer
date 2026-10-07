package com.vai.pokemonexplorer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vai.pokemonexplorer.data.remote.dto.PokemonDetailsDto
import com.vai.pokemonexplorer.data.remote.dto.PokemonTypeDto
import com.vai.pokemonexplorer.data.remote.dto.PokemonTypeSlotDto
import com.vai.pokemonexplorer.data.remote.dto.SpritesDto
import com.vai.pokemonexplorer.data.remote.dto.StatDto
import com.vai.pokemonexplorer.data.remote.dto.StatNameDto
import com.vai.pokemonexplorer.ui.components.ErrorContent
import com.vai.pokemonexplorer.ui.components.PokemonImage
import com.vai.pokemonexplorer.ui.components.ScreenTopBar
import com.vai.pokemonexplorer.ui.model.ErrorUiState
import com.vai.pokemonexplorer.ui.theme.PokemonExplorerTheme
import com.vai.pokemonexplorer.ui.theme.getTypeColor
import com.vai.pokemonexplorer.util.rememberIsInternetAvailable

@Composable
fun PokemonDetailsScreen(
    pokemonDetails: PokemonDetailsDto?,
    errorState: ErrorUiState?,
    onRetry: () -> Unit,
    onBackClick: () -> Unit
) {
    val isInternetAvailable = rememberIsInternetAvailable()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Top bar
        ScreenTopBar(
            title = pokemonDetails
                ?.name
                ?.replaceFirstChar { it.uppercase() }
                ?: "Pokémon Details",
            onBackClick = onBackClick
        )

        when {
            errorState != null -> {
                ErrorContent(
                    title = errorState.title,
                    message = errorState.message,
                    onRetry = onRetry,
                    modifier = Modifier.weight(1f)
                )
            }

            pokemonDetails == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            else -> {
                val hp = pokemonDetails.stats
                    .find { it.stat.name == "hp" }
                    ?.base_stat

                val attack = pokemonDetails.stats
                    .find { it.stat.name == "attack" }
                    ?.base_stat

                val defense = pokemonDetails.stats
                    .find { it.stat.name == "defense" }
                    ?.base_stat

                val firstType = pokemonDetails.types
                    .firstOrNull { it.slot == 1 }
                    ?.type
                    ?.name

                val secondType = pokemonDetails.types
                    .firstOrNull { it.slot == 2 }
                    ?.type
                    ?.name

                Spacer(
                    modifier = Modifier.height(32.dp)
                )

                // Pokemon sprite
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .background(
                            color = Color(0xFFFDFDFD),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .border(
                            width = 2.dp,
                            color = Color(0xFFBDBDBD),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    PokemonImage(
                        imageUrl = pokemonDetails.sprites.front_default,
                        contentDescription = pokemonDetails.name,
                        isInternetAvailable = isInternetAvailable,
                        modifier = Modifier.size(190.dp)
                    )
                }

                Spacer(
                    modifier = Modifier.height(24.dp)
                )

                // Pokemon Name
                Text(
                    text = pokemonDetails.name.uppercase(),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.padding(top = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    firstType?.let { type ->
                        PokemonTypeBadge(type = type)
                    }

                    secondType?.let { type ->
                        PokemonTypeBadge(type = type)
                    }
                }

                //type-badge
                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                // Stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "HP",
                        value = hp,
                        modifier = Modifier.weight(1f)
                    )

                    StatCard(
                        title = "ATK",
                        value = attack,
                        modifier = Modifier.weight(1f)
                    )

                    StatCard(
                        title = "DEF",
                        value = defense,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun PokemonTypeBadge(
    type: String
) {
    Surface(
        color = getTypeColor(type),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = type.uppercase(),
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 4.dp
            )
        )
    }
}

@Composable
fun StatCard(
    title: String,
    value: Int?,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = value?.toString() ?: "-",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PokemonDetailsScreenPreview() {
    PokemonExplorerTheme {
        PokemonDetailsScreen(
            pokemonDetails = PokemonDetailsDto(
                name = "blastoise",
                sprites = SpritesDto(
                    front_default = null
                ),
                stats = listOf(
                    StatDto(
                        base_stat = 79,
                        stat = StatNameDto(
                            name = "hp"
                        )
                    ),
                    StatDto(
                        base_stat = 83,
                        stat = StatNameDto(
                            name = "attack"
                        )
                    ),
                    StatDto(
                        base_stat = 100,
                        stat = StatNameDto(
                            name = "defense"
                        )
                    )
                ),
                types = listOf(
                    PokemonTypeSlotDto(
                        slot = 1,
                        type = PokemonTypeDto(name = "water")
                    )
                )
            ),
            errorState = null,
            onRetry = { },
            onBackClick = { }
        )
    }
}
