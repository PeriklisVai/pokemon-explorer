package com.vai.pokemonexplorer.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.vai.pokemonexplorer.ui.model.PokemonListItem


@Composable
fun PokemonListScreen(
    type: String,
    pokemonList: List<PokemonListItem>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onPokemonClick: (String, String) -> Unit,
    hasMore: Boolean,
    isLoadingMore: Boolean,
    onLoadMore: () -> Unit
) {
    val listState = rememberLazyListState()

    // Progress used to fill the load-more indicator up to its trigger threshold.
    var pullDistance by remember {
        mutableFloatStateOf(0f)
    }

    // Stores any extra drag after the indicator has already reached 100%.
    var overscrollDistance by remember {
        mutableFloatStateOf(0f)
    }

    val density = LocalDensity.current

    val loadThreshold = with(density) {
        110.dp.toPx()
    }

    val loadProgress =
        (pullDistance / loadThreshold).coerceIn(0f, 1f)

    Column(
        modifier = Modifier
            .fillMaxSize()

            // Observe the user's vertical drag directly so the load-more indicator
            // reacts immediately in both directions.
            .pointerInput(hasMore, isLoadingMore) {

                // Handle one complete touch gesture at a time:
                // finger down -> drag -> finger release.
                awaitEachGesture {

                    // Wait until the user touches the screen.
                    // We also accept touches already seen by another scrollable component,
                    // because the LazyColumn participates in the same gesture.
                    awaitFirstDown(
                        requireUnconsumed = false
                    )

                    do {
                        // Read pointer movement before the LazyColumn fully processes it,
                        // so the custom pull-up interaction feels responsive.
                        val event = awaitPointerEvent(
                            pass = PointerEventPass.Initial
                        )

                        val change = event.changes.first()

                        // Vertical movement since the previous pointer event.
                        // Negative = finger moved up.
                        // Positive = finger moved down.
                        val deltaY = change.positionChange().y

                        // Start filling the load-more progress only when:
                        // - the user is dragging upward,
                        // - the list is already at the bottom,
                        // - more results exist,
                        // - and no load-more request is currently running.
                        if (
                            deltaY < 0 &&
                            !listState.canScrollForward &&
                            hasMore &&
                            !isLoadingMore
                        ) {
                            val dragAmount = -deltaY

                            // Distance still required for the indicator to reach 100%.
                            val remainingToThreshold =
                                loadThreshold - pullDistance

                            if (remainingToThreshold > 0f) {

                                // Use only the part of the drag needed to fill the indicator.
                                val usedForProgress = minOf(
                                    dragAmount,
                                    remainingToThreshold
                                )

                                pullDistance += usedForProgress

                                // If the user keeps dragging after reaching 100%,
                                // store that extra distance separately.
                                val extra =
                                    dragAmount - usedForProgress

                                if (extra > 0f) {
                                    overscrollDistance += extra
                                }

                            } else {
                                // Indicator is already full, so all further upward drag
                                // is treated as overscroll.
                                overscrollDistance += dragAmount
                            }

                            // Prevent the same movement from also affecting the list.
                            change.consume()
                        }

                        // If the user reverses direction, first cancel any extra overscroll.
                        // The visible indicator only starts decreasing after the finger
                        // returns to the point where the indicator originally reached 100%.
                        else if (
                            deltaY > 0 &&
                            (
                                    pullDistance > 0f ||
                                            overscrollDistance > 0f
                                    )
                        ) {
                            var reverseAmount = deltaY

                            if (overscrollDistance > 0f) {

                                val usedForOverscroll = minOf(
                                    reverseAmount,
                                    overscrollDistance
                                )

                                overscrollDistance -= usedForOverscroll
                                reverseAmount -= usedForOverscroll
                            }

                            // Once all overscroll has been cancelled,
                            // use the remaining reverse movement to empty the indicator.
                            if (
                                reverseAmount > 0f &&
                                pullDistance > 0f
                            ) {
                                pullDistance = (
                                        pullDistance - reverseAmount
                                        ).coerceAtLeast(0f)
                            }

                            change.consume()
                        }

                    } while (
                    // Keep processing pointer movement until the finger is released.
                        event.changes.any { it.pressed }
                    )

                    // Trigger the next page only if the user releases
                    // while the indicator is still at the load threshold.
                    if (
                        pullDistance >= loadThreshold &&
                        hasMore &&
                        !isLoadingMore
                    ) {
                        onLoadMore()
                    }

                    // Reset the gesture state after every release,
                    // regardless of whether loading was triggered.
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

        TextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = {
                Text("Search Pokémon")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )

        if (
            searchQuery.isNotBlank() &&
            pokemonList.isEmpty() &&
            !isLoadingMore
        ) {
            Text(
                text = "No Pokémon found",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                textAlign = TextAlign.Center
            )
        }

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
                        onPokemonClick(
                            pokemon.detailsUrl,
                            type
                        )
                    },
                    modifier = Modifier.padding(
                        vertical = 6.dp
                    )
                )
            }

            if (hasMore) {
                item {
                    LoadMoreIndicator(
                        progress = loadProgress,
                        isLoading = isLoadingMore
                    )
                }
            }
        }
    }
}


@Composable
private fun LoadMoreIndicator(
    progress: Float,
    isLoading: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = 8.dp,
                bottom = 16.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        CircularProgressIndicator(
            progress = {
                if (isLoading) 1f else progress
            },
            modifier = Modifier.size(32.dp)
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text = when {
                isLoading -> "Loading..."
                progress >= 1f -> "Release to load"
                else -> "Load more"
            }
        )
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
            .clickable(
                onClick = onClick
            )
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
            modifier = Modifier.padding(
                start = 16.dp
            )
        )
    }
}
