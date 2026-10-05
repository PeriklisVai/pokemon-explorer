package com.vai.pokemonexplorer.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.WifiOff,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(28.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

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
fun TypeButton(
    type: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = getTypeColor(type)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = type.uppercase(),
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
fun getTypeColor(type: String): Color {

    return when (type.lowercase()) {

        "fire" -> Color(0xFFF08030)
        "water" -> Color(0xFF6890F0)
        "grass" -> Color(0xFF78C850)
        "electric" -> Color(0xFFF8D030)
        "dragon" -> Color(0xFF7038F8)
        "psychic" -> Color(0xFFF85888)
        "ghost" -> Color(0xFF705898)
        "dark" -> Color(0xFF705848)
        "steel" -> Color(0xFFB8B8D0)
        "fairy" -> Color(0xFFEE99AC)

        else -> Color.Gray
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
