package com.vai.pokemonexplorer.ui.navigation

import android.net.Uri
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vai.pokemonexplorer.ui.screens.HomeScreen
import com.vai.pokemonexplorer.ui.screens.PokemonListScreen
import com.vai.pokemonexplorer.data.remote.RetrofitInstance
import com.vai.pokemonexplorer.data.repository.PokemonRepository
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.vai.pokemonexplorer.ui.screens.PokemonDetailsScreen
import com.vai.pokemonexplorer.ui.viewmodel.PokemonDetailsViewModel
import com.vai.pokemonexplorer.ui.viewmodel.PokemonListViewModel
import com.vai.pokemonexplorer.util.rememberIsInternetAvailable

@Composable
fun AppNavigation() {

    val repository = remember {
        PokemonRepository(RetrofitInstance.api)
    }

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home",

        // Fade in the destination screen during normal navigation.
        enterTransition = {
            fadeIn(
                animationSpec = tween(200)
            )
        },

        // Fade out the current screen during normal navigation.
        exitTransition = {
            fadeOut(
                animationSpec = tween(200)
            )
        },

        // Fade the previous screen back in when navigating back.
        popEnterTransition = {
            fadeIn(
                animationSpec = tween(200)
            )
        },

        // Fade out the current screen when it is removed from the back stack.
        popExitTransition = {
            fadeOut(
                animationSpec = tween(200)
            )
        },

        // Handle Android predictive-back gesture without the default scale effect.
        predictivePopEnterTransition = {
            fadeIn()
        },
        predictivePopExitTransition = {
            fadeOut()
        }
    ) {

        composable("home") {
            HomeScreen(
                onTypeClick = { type ->
                    navController.navigate("pokemonList/$type")
                }
            )
        }

        composable("pokemonList/{type}") { backStackEntry ->

            val type = backStackEntry.arguments
                ?.getString("type")
                ?: ""

            val viewModel: PokemonListViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        PokemonListViewModel(repository)
                    }
                }
            )

            val isInternetAvailable = rememberIsInternetAvailable()

            DisposableEffect(backStackEntry, isInternetAvailable, viewModel) {
                val observer = LifecycleEventObserver { _, event ->
                    if (
                        event == Lifecycle.Event.ON_RESUME &&
                        isInternetAvailable
                    ) {
                        viewModel.retryMissingImages()
                    }
                }

                backStackEntry.lifecycle.addObserver(observer)

                onDispose {
                    backStackEntry.lifecycle.removeObserver(observer)
                }
            }

            LaunchedEffect(type) {
                viewModel.loadPokemonByType(type)
            }

            PokemonListScreen(
                type = type,
                pokemonList = viewModel.pokemonList,
                searchQuery = viewModel.searchQuery,
                totalResultsCount = viewModel.totalResultsCount,
                errorState = viewModel.errorState,
                onRetry = {
                    viewModel.retryLoadPokemon(type)
                },
                onSearchQueryChange = viewModel::onSearchQueryChange,
                onPokemonClick = { detailsUrl, type ->
                    navController.navigate(
                        "pokemonDetails/$type/${Uri.encode(detailsUrl)}"
                    )
                },
                hasMore = viewModel.hasMore,
                isLoadingMore = viewModel.isLoadingMore,
                onLoadMore = {
                    viewModel.loadNextPokemon()
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable("pokemonDetails/{type}/{detailsUrl}") { backStackEntry ->

            val type = backStackEntry.arguments
                ?.getString("type")
                ?: ""

            val detailsUrl = backStackEntry.arguments
                ?.getString("detailsUrl")
                ?.let { Uri.decode(it) }
                ?: ""

            val viewModel: PokemonDetailsViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        PokemonDetailsViewModel(repository)
                    }
                }
            )

            LaunchedEffect(detailsUrl) {
                viewModel.loadPokemonDetails(detailsUrl)
            }

            PokemonDetailsScreen(
                pokemonDetails = viewModel.pokemonDetails,
                errorState = viewModel.errorState,
                onRetry = {
                    viewModel.loadPokemonDetails(detailsUrl)
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
