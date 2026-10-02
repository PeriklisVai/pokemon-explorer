package com.vai.pokemonexplorer.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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
import com.vai.pokemonexplorer.ui.viewmodel.PokemonListViewModel

@Composable
fun AppNavigation() {

    val repository = remember {
        PokemonRepository(RetrofitInstance.api)
    }

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
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

            LaunchedEffect(type) {
                viewModel.loadPokemonByType(type)
            }

            PokemonListScreen(
                type = type,
                pokemonList = viewModel.pokemonList,
                onPokemonClick = { detailsUrl ->
                    navController.navigate(
                        "pokemonDetails/${Uri.encode(detailsUrl)}"
                    )
                }
            )
        }

        composable("pokemonDetails/{detailsUrl}") { backStackEntry ->

            val detailsUrl = backStackEntry.arguments
                ?.getString("detailsUrl")
                ?.let { Uri.decode(it) }
                ?: ""

            PokemonDetailsScreen(
                detailsUrl = detailsUrl
            )
        }
    }
}