package com.example.orangecast.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.orangecast.R
import com.example.orangecast.ui.discover.DiscoverViewModel

@Composable
fun Home() {
    val viewModel: DiscoverViewModel = hiltViewModel()
    val viewState = viewModel.state.collectAsState()
    Surface(Modifier.fillMaxSize()) {
        CategoryOfChannelsRow(
            title = stringResource(R.string.popular),
            channels = viewState.value.bestPodcasts,
            onSubscribeClicked = {}
        )
    }
}