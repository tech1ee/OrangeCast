@file:OptIn(ExperimentalMaterial3Api::class)

package dev.orangecast.shared.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.orangecast.shared.domain.model.Podcast
import dev.orangecast.shared.domain.model.GenreSection
import dev.orangecast.shared.presentation.ui.components.PodcastCard
import dev.orangecast.shared.presentation.ui.components.PodcastCardShimmer
import dev.orangecast.shared.presentation.ui.components.PodcastCardLayoutStyle
import dev.orangecast.shared.presentation.ui.theme.OrangeCastColors
import dev.orangecast.shared.presentation.viewmodel.DiscoverViewModel
import dev.orangecast.shared.presentation.viewmodel.ContentType
import dev.orangecast.shared.presentation.viewmodel.DiscoverUiState
import org.koin.compose.koinInject

@Composable
fun DiscoverScreen(
    onPodcastClick: (Podcast) -> Unit,
    viewModel: DiscoverViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Search podcasts...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        TabRow(
            selectedTabIndex = uiState.selectedContentType.ordinal,
            modifier = Modifier.fillMaxWidth(),
            containerColor = OrangeCastColors.Light.Surface
        ) {
            ContentType.values().forEach { contentType ->
                Tab(
                    selected = uiState.selectedContentType == contentType,
                    onClick = { viewModel.selectContentType(contentType) },
                    text = {
                        Text(
                            text = when (contentType) {
                                ContentType.POPULAR -> "Popular"
                                ContentType.RECOMMENDATIONS -> "For You"
                                ContentType.NEW -> "New"
                            }
                        )
                    }
                )
            }
        }
        
        uiState.errorMessage?.let { errorMessage ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Error loading content",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    TextButton(
                        onClick = { viewModel.retry() },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("Retry")
                    }
                }
            }
        }
        
        when (uiState.selectedContentType) {
            ContentType.POPULAR -> PopularContent(uiState, onPodcastClick)
            ContentType.RECOMMENDATIONS -> RecommendationsContent(uiState, onPodcastClick)
            ContentType.NEW -> NewContent(uiState, onPodcastClick)
        }
    }
}

@Composable
private fun PopularContent(
    uiState: DiscoverUiState,
    onPodcastClick: (Podcast) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        if (uiState.featuredPodcasts.isNotEmpty()) {
            item {
                GenreSection(
                    title = "Featured",
                    podcasts = uiState.featuredPodcasts,
                    isLoading = uiState.isLoading,
                    onPodcastClick = onPodcastClick
                )
            }
        }
        
        // Dynamic genre sections from API
        items(uiState.genreSections) { genreSection ->
            GenreSection(
                title = genreSection.genre.name,
                podcasts = genreSection.podcasts,
                isLoading = uiState.isLoading,
                onPodcastClick = onPodcastClick
            )
        }
    }
}

@Composable
private fun RecommendationsContent(
    uiState: DiscoverUiState,
    onPodcastClick: (Podcast) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Text(
                text = "Recommended for You",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        item {
            GenreSection(
                title = "Recommended for You",
                podcasts = uiState.featuredPodcasts,
                isLoading = uiState.isLoading,
                onPodcastClick = onPodcastClick
            )
        }
    }
}

@Composable
private fun NewContent(
    uiState: DiscoverUiState,
    onPodcastClick: (Podcast) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Text(
                text = "New Releases",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        item {
            GenreSection(
                title = "New Releases",
                podcasts = uiState.featuredPodcasts,
                isLoading = uiState.isLoading,
                onPodcastClick = onPodcastClick
            )
        }
    }
}

@Composable
private fun GenreSection(
    title: String,
    podcasts: List<Podcast>,
    isLoading: Boolean,
    onPodcastClick: (Podcast) -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            if (isLoading) {
                items(3) {
                    PodcastCardShimmer(
                        layoutStyle = PodcastCardLayoutStyle.Grid,
                        modifier = Modifier.width(160.dp)
                    )
                }
            } else if (podcasts.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .width(160.dp)
                            .height(200.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No content available",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(podcasts) { podcast ->
                    PodcastCard(
                        podcast = podcast,
                        onClick = { onPodcastClick(podcast) },
                        layoutStyle = PodcastCardLayoutStyle.Grid,
                        modifier = Modifier.width(160.dp)
                    )
                }
            }
        }
    }
}