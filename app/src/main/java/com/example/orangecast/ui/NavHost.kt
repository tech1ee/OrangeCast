package com.example.orangecast.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.example.orangecast.ui.discover.DiscoverViewModel

@Composable
fun Home(
    viewModel: DiscoverViewModel
) {
    val viewState = viewModel.state.collectAsState()
    Surface(Modifier.fillMaxSize()) {
        ChannelsByCategoryColumn(
            channels = viewState.value.bestPodcasts,
            {}
        )
    }
}