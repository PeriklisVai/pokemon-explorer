package com.vai.pokemonexplorer.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.scrollBy
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vai.pokemonexplorer.ui.components.ErrorContent
import com.vai.pokemonexplorer.ui.components.PokemonImage
import com.vai.pokemonexplorer.ui.model.ErrorUiState
import com.vai.pokemonexplorer.ui.model.PokemonListItem
import com.vai.pokemonexplorer.util.rememberIsInternetAvailable


@Composable
fun PokemonListScreen(
    type: String,
    pokemonList: List<PokemonListItem>,
    searchQuery: String,
    totalResultsCount: Int,
    errorState: ErrorUiState?,
    onRetry: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onPokemonClick: (String, String) -> Unit,
    hasMore: Boolean,
    isLoadingMore: Boolean,
    onLoadMore: () -> Unit
) {
    val isInternetAvailable = rememberIsInternetAvailable()
    val listState = rememberLazyListState()

    // Progress used to fill the load-more indicator up to its trigger threshold.
    var pullDistance by remember {
        mutableFloatStateOf(0f)
    }

    // Stores any extra drag after the indicator has already reached 100%.
    var overscrollDistance by remember {
        mutableFloatStateOf(0f)
    }

    var pendingReleaseOffset by remember {
        mutableFloatStateOf(0f)
    }

    val density = LocalDensity.current

    val loadThreshold = with(density) {
        190.dp.toPx()
    }

    val indicatorStartDistance = with(density) {
        24.dp.toPx()
    }

    // Total distance the user has pulled beyond the bottom of the list.
    val totalPullDistance = pullDistance + overscrollDistance

    val maxListPullDistance = with(density) {
        130.dp.toPx()
    }
    val listPullOffset = minOf(totalPullDistance, maxListPullDistance)

    val indicatorProgress = (
        (pullDistance - indicatorStartDistance) /
            (loadThreshold - indicatorStartDistance)
    ).coerceIn(0f, 1f)

    val indicatorTravelDistance = with(density) {
        12.dp.toPx()
    }

    LaunchedEffect(pokemonList.size) {
        if (pendingReleaseOffset > 0f) {
            listState.scrollBy(pendingReleaseOffset)

            pullDistance = 0f
            overscrollDistance = 0f
            pendingReleaseOffset = 0f
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()

            // Observe the user's vertical drag directly so the load-more indicator
            // reacts immediately in both directions.
            .pointerInput(hasMore, isLoadingMore, errorState) {

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
                            !isLoadingMore &&
                            errorState == null
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
                        !isLoadingMore &&
                        errorState == null
                    ) {
                        pendingReleaseOffset = minOf(
                            pullDistance + overscrollDistance,
                            maxListPullDistance
                        )
                        onLoadMore()
                    } else {
                        pullDistance = 0f
                        overscrollDistance = 0f
                    }
                }
            }
            .padding(24.dp)
    ) {

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "$type Pokémon",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            TextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = {
                    Text("Search Pokémon")
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(28.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF5F5F5),
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )

            if (pokemonList.isNotEmpty()) {
                Text(
                    text = "Showing ${pokemonList.size} of $totalResultsCount",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        if (
            pokemonList.isEmpty() &&
            isLoadingMore &&
            errorState == null
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(28.dp)
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text("Loading...")
            }
        }

        if (
            searchQuery.isNotBlank() &&
            pokemonList.isEmpty() &&
            !isLoadingMore &&
            errorState == null
        ) {
            Text(
                text = "No Pokémon found",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                textAlign = TextAlign.Center
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clipToBounds()
        ) {
            if (errorState != null) {
                ErrorContent(
                    title = errorState.title,
                    message = errorState.message,
                    onRetry = onRetry,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 8.dp)
                        .graphicsLayer {
                            translationY = -listPullOffset
                        }
                ) {
                    items(pokemonList) { pokemon ->
                        PokemonListItemRow(
                            pokemon = pokemon,
                            isInternetAvailable = isInternetAvailable,
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
                }

                if (
                    hasMore &&
                    pokemonList.isNotEmpty() &&
                    indicatorProgress > 0f
                ) {
                    LoadMoreIndicator(
                        progress = indicatorProgress,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .graphicsLayer {
                                val scale = 0.75f + (0.25f * indicatorProgress)
                                scaleX = scale
                                scaleY = scale

                                translationY =
                                    indicatorTravelDistance * (1f - indicatorProgress)

                                alpha = indicatorProgress
                            }
                    )
                }
            }
        }
    }
}


@Composable
private fun LoadMoreIndicator(
    progress: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier.size(32.dp)
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text = if (progress >= 1f) {
                "Release to load"
            } else {
                "Load more"
            }
        )
    }
}


@Composable
fun PokemonListItemRow(
    pokemon: PokemonListItem,
    isInternetAvailable: Boolean,
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

        PokemonImage(
            imageUrl = pokemon.imageUrl,
            contentDescription = pokemon.name,
            isInternetAvailable = isInternetAvailable,
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
