@file:OptIn(ExperimentalMaterial3Api::class)

package dev.orangecast.shared.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.GetApp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.orangecast.shared.domain.model.Podcast
import dev.orangecast.shared.presentation.ui.components.PodcastCard
import dev.orangecast.shared.presentation.ui.components.PodcastCardShimmer
import dev.orangecast.shared.presentation.ui.components.PodcastCardLayoutStyle
import dev.orangecast.shared.presentation.ui.theme.OrangeCastColors
import dev.orangecast.shared.presentation.viewmodel.LibraryViewModel
import org.koin.compose.koinInject

@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel = koinInject(),
    onPodcastClick: (Podcast) -> Unit = {},
    onDownloadsClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header with actions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "My Library",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = onDownloadsClick) {
                    Icon(
                        imageVector = Icons.Default.GetApp,
                        contentDescription = "Downloads",
                        tint = OrangeCastColors.primary
                    )
                }
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = OrangeCastColors.primary
                    )
                }
            }
        }
        
        when {
            uiState.isLoading -> {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 160.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(8) {
                        PodcastCardShimmer(layoutStyle = PodcastCardLayoutStyle.Grid)
                    }
                }
            }
            
            uiState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Failed to load library",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = uiState.error ?: "Unknown error",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(
                            onClick = { viewModel.onRetry() }
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }
            
            uiState.subscribedPodcasts.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LibraryBooks,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Text(
                            text = "Your subscribed podcasts will appear here",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Subscribe to podcasts from the Discover tab to build your library",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(
                            onClick = { /* Navigate to discover */ },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = OrangeCastColors.primary
                            )
                        ) {
                            Text("Discover Podcasts")
                        }
                    }
                }
            }
            
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 160.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.subscribedPodcasts) { podcast ->
                        PodcastCard(
                            podcast = podcast,
                            onClick = { onPodcastClick(podcast) },
                            layoutStyle = PodcastCardLayoutStyle.Grid
                        )
                    }
                }
            }
        }
    }
}

