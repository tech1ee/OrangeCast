package dev.orangecast.shared.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.orangecast.shared.domain.model.Podcast
import dev.orangecast.shared.presentation.ui.components.PodcastCard
import dev.orangecast.shared.presentation.ui.components.PodcastCardShimmer
import dev.orangecast.shared.presentation.ui.components.PodcastCardLayoutStyle

@Composable
fun PodcastListScreen(
    podcasts: List<Podcast>,
    isLoading: Boolean = false,
    error: String? = null,
    onPodcastClick: (Podcast) -> Unit = {},
    onRetryClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "OrangeCast",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(16.dp)
        )
        
        when {
            error != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Error loading podcasts",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Button(onClick = onRetryClick) {
                        Text("Retry")
                    }
                }
            }
            isLoading -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(8) {
                        PodcastCardShimmer(layoutStyle = PodcastCardLayoutStyle.List)
                    }
                }
            }
            podcasts.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No podcasts found",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(podcasts) { podcast ->
                        PodcastCard(
                            podcast = podcast,
                            onClick = { onPodcastClick(podcast) },
                            layoutStyle = PodcastCardLayoutStyle.List
                        )
                    }
                }
            }
        }
    }
}