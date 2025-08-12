package dev.orangecast.shared.presentation

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.orangecast.shared.presentation.ui.screens.PodcastListScreen
import dev.orangecast.shared.presentation.viewmodel.PodcastListViewModel
import org.koin.compose.koinInject

@Composable
fun OrangeCastApp() {
    MaterialTheme {
        val viewModel: PodcastListViewModel = koinInject()
        val uiState by viewModel.uiState.collectAsState()
        
        PodcastListScreen(
            podcasts = uiState.podcasts,
            isLoading = uiState.isLoading,
            onPodcastClick = { _ ->
                
            }
        )
    }
}