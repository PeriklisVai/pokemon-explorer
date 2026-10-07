package com.vai.pokemonexplorer.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vai.pokemonexplorer.ui.theme.PokemonExplorerTheme
import com.vai.pokemonexplorer.ui.theme.getTypeColor
import com.vai.pokemonexplorer.util.rememberIsInternetAvailable

@Composable
fun HomeScreen(
    onTypeClick: (String) -> Unit
) {
    val isInternetAvailable = rememberIsInternetAvailable()
    val isLandscape =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Pokémon Explorer",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        if (isLandscape) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                // First column
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TypeButton(
                        type = "Fire",
                        onClick = { onTypeClick("Fire") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    TypeButton(
                        type = "Water",
                        onClick = { onTypeClick("Water") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    TypeButton(
                        type = "Grass",
                        onClick = { onTypeClick("Grass") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    TypeButton(
                        type = "Electric",
                        onClick = { onTypeClick("Electric") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Second column
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TypeButton(
                        type = "Dragon",
                        onClick = { onTypeClick("Dragon") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    TypeButton(
                        type = "Psychic",
                        onClick = { onTypeClick("Psychic") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    TypeButton(
                        type = "Ghost",
                        onClick = { onTypeClick("Ghost") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    TypeButton(
                        type = "Dark",
                        onClick = { onTypeClick("Dark") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Third column
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TypeButton(
                        type = "Steel",
                        onClick = { onTypeClick("Steel") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    TypeButton(
                        type = "Fairy",
                        onClick = { onTypeClick("Fairy") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (!isInternetAvailable) {
                        OfflineStatus(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 32.dp),
                            iconSize = 28.dp,
                            textSize = 16.sp,
                            spacing = 8.dp
                        )
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier.padding(top = 32.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TypeRow(
                    types = listOf("Fire", "Water"),
                    onTypeClick = onTypeClick
                )

                TypeRow(
                    types = listOf("Grass", "Electric"),
                    onTypeClick = onTypeClick
                )

                TypeRow(
                    types = listOf("Dragon", "Psychic"),
                    onTypeClick = onTypeClick
                )

                TypeRow(
                    types = listOf("Ghost", "Dark"),
                    onTypeClick = onTypeClick
                )

                TypeRow(
                    types = listOf("Steel", "Fairy"),
                    onTypeClick = onTypeClick
                )
            }
        }

        if (!isLandscape && !isInternetAvailable) {
            OfflineStatus(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 64.dp)
            )
        }
    }
}

@Composable
private fun OfflineStatus(
    modifier: Modifier = Modifier,
    iconSize: Dp = 32.dp,
    textSize: TextUnit = 18.sp,
    spacing: Dp = 10.dp
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.WifiOff,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(iconSize)
        )

        Spacer(
            modifier = Modifier.height(spacing)
        )

        Text(
            text = "No internet connection",
            color = MaterialTheme.colorScheme.error,
            fontSize = textSize,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun TypeRow(
    types: List<String>,
    onTypeClick: (String) -> Unit
) {
    val columns =
        if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE) 4 else 2

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        types.forEach { type ->
            TypeButton(
                type = type,
                onClick = {
                    onTypeClick(type)
                },
                modifier = Modifier.weight(1f)
            )
        }

        repeat(maxOf(0, columns - types.size)) {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun TypeButton(
    type: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
        border = BorderStroke(
            1.dp,
            Color.LightGray.copy(alpha = 0.4f)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
        ) {
            Text(
                text = type.uppercase(),
                modifier = Modifier.align(Alignment.Center),
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp)
                    .fillMaxWidth(0.82f)
                    .height(4.dp)
                    .background(
                        color = getTypeColor(type),
                        shape = RoundedCornerShape(50)
                    )
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    PokemonExplorerTheme {
        HomeScreen(
            onTypeClick = { }
        )
    }
}
