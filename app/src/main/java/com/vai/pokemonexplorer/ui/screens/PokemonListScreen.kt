package com.vai.pokemonexplorer.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vai.pokemonexplorer.ui.model.PokemonListItem
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.size
import coil3.compose.AsyncImage


@Composable
fun PokemonListScreen(
    type: String,
    pokemonList: List<PokemonListItem>,
    onPokemonClick: (String, String) -> Unit,
    hasMore: Boolean,
    isLoadingMore: Boolean,
    onLoadMore: () -> Unit
) {
    val listState = rememberLazyListState()

    var pullDistance by remember {
        mutableFloatStateOf(0f)
    }

    var overscrollDistance by remember {
        mutableFloatStateOf(0f)
    }

    val density = LocalDensity.current
    val loadThreshold = with(density) {
        110.dp.toPx()
    }
    val loadProgress = (pullDistance / loadThreshold).coerceIn(0f, 1f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(hasMore, isLoadingMore) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)

                    do {
                        val event = awaitPointerEvent(pass = PointerEventPass.Initial)
                        val change = event.changes.first()
                        val deltaY = change.positionChange().y

                        if (
                            deltaY < 0 &&
                            !listState.canScrollForward &&
                            hasMore &&
                            !isLoadingMore
                        ) {
                            val dragAmount = -deltaY
                            val remainingToThreshold = loadThreshold - pullDistance

                            if (remainingToThreshold > 0f) {
                                val usedForProgress = minOf(dragAmount, remainingToThreshold)
                                pullDistance += usedForProgress

                                val extra = dragAmount - usedForProgress
                                if (extra > 0f) {
                                    overscrollDistance += extra
                                }
                            } else {
                                overscrollDistance += dragAmount
                            }

                            change.consume()
                        } else if (
                            deltaY > 0 &&
                            (pullDistance > 0f || overscrollDistance > 0f)
                        ) {
                            var reverseAmount = deltaY

                            if (overscrollDistance > 0f) {
                                val usedForOverscroll = minOf(reverseAmount, overscrollDistance)
                                overscrollDistance -= usedForOverscroll
                                reverseAmount -= usedForOverscroll
                            }

                            if (reverseAmount > 0f && pullDistance > 0f) {
                                pullDistance = (
                                    pullDistance - reverseAmount
                                ).coerceAtLeast(0f)
                            }

                            change.consume()
                        }
                    } while (event.changes.any { it.pressed })

                    if (
                        pullDistance >= loadThreshold &&
                        hasMore &&
                        !isLoadingMore
                    ) {
                        onLoadMore()
                    }

                    pullDistance = 0f
                    overscrollDistance = 0f
                }
            }
            .padding(24.dp)
    ) {
        Text(
            text = "$type Pokémon",
            fontSize = 28.sp
        )

        LazyColumn(
            state = listState,
            modifier = Modifier
                .padding(top = 16.dp)
                .weight(1f)
        ) {
            items(pokemonList) { pokemon ->
                PokemonListItemRow(
                    pokemon = pokemon,
                    onClick = {
                        onPokemonClick(pokemon.detailsUrl, type)
                    },
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }

            if (hasMore) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            progress = {
                                if (isLoadingMore) 1f else loadProgress
                            },
                            modifier = Modifier.size(32.dp)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = when {
                                isLoadingMore -> "Loading..."
                                loadProgress >= 1f -> "Release to load"
                                else -> "Load more"
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PokemonListItemRow(
    pokemon: PokemonListItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color.LightGray,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable {
                onClick()
            }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        AsyncImage(
            model = pokemon.imageUrl,
            contentDescription = pokemon.name,
            modifier = Modifier.size(72.dp)
        )

        Text(
            text = pokemon.name,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}
