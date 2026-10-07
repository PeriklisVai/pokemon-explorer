package com.vai.pokemonexplorer.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Pokémon Explorer",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Column(
            modifier = Modifier.padding(top = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            TypeRow("Fire", "Water", onTypeClick)
            TypeRow("Grass", "Electric", onTypeClick)
            TypeRow("Dragon", "Psychic", onTypeClick)
            TypeRow("Ghost", "Dark", onTypeClick)
            TypeRow("Steel", "Fairy", onTypeClick)
        }

        if (!isInternetAvailable) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 64.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.WifiOff,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(32.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "No internet connection",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun TypeRow(
    firstType: String,
    secondType: String,
    onTypeClick: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TypeButton(
            type = firstType,
            onClick = {
                onTypeClick(firstType)
            },
            modifier = Modifier.weight(1f)
        )

        TypeButton(
            type = secondType,
            onClick = {
                onTypeClick(secondType)
            },
            modifier = Modifier.weight(1f)
        )
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
