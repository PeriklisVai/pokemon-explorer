package com.vai.pokemonexplorer.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import coil3.compose.AsyncImage

@Composable
fun PokemonImage(
    imageUrl: String?,
    contentDescription: String?,
    isInternetAvailable: Boolean,
    modifier: Modifier = Modifier
) {
    var showErrorIcon by remember(contentDescription) {
        mutableStateOf(false)
    }

    // Keep the error visible until the sprite actually loads.
    LaunchedEffect(isInternetAvailable, imageUrl) {
        if (!isInternetAvailable && imageUrl == null) {
            showErrorIcon = true
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (imageUrl != null) {
            AsyncImage(
                model = imageUrl,
                contentDescription = contentDescription,
                modifier = Modifier.matchParentSize(),
                onSuccess = {
                    showErrorIcon = false
                },
                onError = {
                    showErrorIcon = true
                }
            )
        }

        if (showErrorIcon) {
            Icon(
                imageVector = Icons.Outlined.ErrorOutline,
                contentDescription = "Image unavailable",
                tint = Color.Gray,
                modifier = Modifier.matchParentSize()
            )
        }
    }
}
