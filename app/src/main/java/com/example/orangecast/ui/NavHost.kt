package com.example.orangecast.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.orangecast.R
import com.example.orangecast.ui.discover.DiscoverViewModel

@Composable
fun Home(
    appState: AppState = rememberAppState()
) {
    NavHost(
        navController = appState.navController,
        startDestination = Screen.Home.route
    ) {
        val viewModel: DiscoverViewModel = hiltViewModel()
        val viewState = viewModel.state.collectAsState()
        composable(Screen.Home.route) { backStackEntry ->
            Surface(Modifier.fillMaxSize()) {
                CategoryOfChannelsRow(
                    title = stringResource(R.string.popular),
                    channels = viewState.value.bestPodcasts,
                    onChannelClicked = { channelId ->
                        appState.navigateToChannel(channelId, backStackEntry)
                    },
                    onSubscribeClicked = {}
                )
            }
        }
        composable(Screen.Channel.route) { backStackEntry ->

        }
    }
}