package com.vai.pokemonexplorer.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

@Composable
fun rememberIsInternetAvailable(): Boolean {
    val context = LocalContext.current

    val connectivityManager = remember(context) {
        context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
    }

    var isInternetAvailable by remember(connectivityManager) {
        mutableStateOf(
            connectivityManager.hasInternetConnection()
        )
    }

    DisposableEffect(connectivityManager) {
        val networkCallback =
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    isInternetAvailable =
                        connectivityManager.hasInternetConnection()
                }

                override fun onLost(network: Network) {
                    isInternetAvailable = false
                }

                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities
                ) {
                    isInternetAvailable =
                        networkCapabilities.hasCapability(
                            NetworkCapabilities.NET_CAPABILITY_INTERNET
                        ) && networkCapabilities.hasCapability(
                            NetworkCapabilities.NET_CAPABILITY_VALIDATED
                        )
                }
            }

        connectivityManager.registerDefaultNetworkCallback(
            networkCallback
        )

        onDispose {
            connectivityManager.unregisterNetworkCallback(
                networkCallback
            )
        }
    }

    return isInternetAvailable
}

private fun ConnectivityManager.hasInternetConnection(): Boolean {
    val network = activeNetwork ?: return false
    val capabilities = getNetworkCapabilities(network) ?: return false

    return capabilities.hasCapability(
        NetworkCapabilities.NET_CAPABILITY_INTERNET
    ) && capabilities.hasCapability(
        NetworkCapabilities.NET_CAPABILITY_VALIDATED
    )
}
