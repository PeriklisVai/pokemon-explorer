package com.vai.pokemonexplorer.ui.screens

import android.content.res.Configuration
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.platform.LocalConfiguration
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
    val isLandscape =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
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

                if (isLandscape) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .offset(y = (-16).dp),
                        horizontalArrangement = Arrangement.spacedBy(32.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PokemonImageCard(
                            pokemonDetails = pokemonDetails,
                            isInternetAvailable = isInternetAvailable,
                            modifier = Modifier.weight(1f)
                        )

                        // Right side: name, types and stats
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .height(240.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(
                                modifier = Modifier.height(6.dp)
                            )

                            PokemonIdentity(
                                pokemonDetails = pokemonDetails
                            )

                            Spacer(
                                modifier = Modifier.weight(1f)
                            )

                            PokemonStats(
                                hp = hp,
                                attack = attack,
                                defense = defense
                            )

                            Spacer(
                                modifier = Modifier.height(18.dp)
                            )
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = (-12).dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(
                            modifier = Modifier.height(32.dp)
                        )

                        PokemonImageCard(
                            pokemonDetails = pokemonDetails,
                            isInternetAvailable = isInternetAvailable
                        )

                        Spacer(
                            modifier = Modifier.height(24.dp)
                        )

                        PokemonIdentity(
                            pokemonDetails = pokemonDetails
                        )

                        Spacer(
                            modifier = Modifier.height(20.dp)
                        )

                        PokemonStats(
                            hp = hp,
                            attack = attack,
                            defense = defense
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PokemonImageCard(
    pokemonDetails: PokemonDetailsDto,
    isInternetAvailable: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
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
}

@Composable
private fun PokemonIdentity(
    pokemonDetails: PokemonDetailsDto
) {
    val firstType = pokemonDetails.types
        .firstOrNull { it.slot == 1 }
        ?.type
        ?.name

    val secondType = pokemonDetails.types
        .firstOrNull { it.slot == 2 }
        ?.type
        ?.name

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
}

@Composable
private fun PokemonStats(
    hp: Int?,
    attack: Int?,
    defense: Int?
) {
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
