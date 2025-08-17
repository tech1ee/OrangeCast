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
import dev.orangecast.shared.domain.usecase.SearchPodcastsUseCase
import dev.orangecast.shared.domain.usecase.GetGenresUseCase
import dev.orangecast.shared.presentation.ui.components.PodcastCard
import dev.orangecast.shared.presentation.ui.components.PodcastCardShimmer
import dev.orangecast.shared.presentation.ui.components.PodcastCardLayoutStyle
import dev.orangecast.shared.presentation.ui.theme.OrangeCastColors
import kotlinx.coroutines.delay
import org.koin.compose.koinInject

enum class ContentType {
    POPULAR, RECOMMENDATIONS, NEW
}

@Composable
fun DiscoverScreen(
    onPodcastClick: (Podcast) -> Unit,
    searchUseCase: SearchPodcastsUseCase = koinInject(),
    @Suppress("UNUSED_PARAMETER") genresUseCase: GetGenresUseCase = koinInject()
) {
    var selectedContentType by remember { mutableStateOf(ContentType.POPULAR) }

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
            selectedTabIndex = selectedContentType.ordinal,
            modifier = Modifier.fillMaxWidth(),
            containerColor = OrangeCastColors.Light.Surface
        ) {
            ContentType.values().forEach { contentType ->
                Tab(
                    selected = selectedContentType == contentType,
                    onClick = { selectedContentType = contentType },
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
        
        when (selectedContentType) {
            ContentType.POPULAR -> PopularContent(onPodcastClick, searchUseCase)
            ContentType.RECOMMENDATIONS -> RecommendationsContent(onPodcastClick)
            ContentType.NEW -> NewContent(onPodcastClick)
        }
    }
}

@Composable
private fun PopularContent(
    onPodcastClick: (Podcast) -> Unit,
    searchUseCase: SearchPodcastsUseCase
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            GenreSection(
                title = "Health & Fitness",
                onPodcastClick = onPodcastClick,
                searchUseCase = searchUseCase
            )
        }
        
        item {
            GenreSection(
                title = "Business",
                onPodcastClick = onPodcastClick,
                searchUseCase = searchUseCase
            )
        }
        
        item {
            GenreSection(
                title = "Technology",
                onPodcastClick = onPodcastClick,
                searchUseCase = searchUseCase
            )
        }
    }
}

@Composable
private fun RecommendationsContent(onPodcastClick: (Podcast) -> Unit) {
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
                title = "Similar to Your Favorites",
                onPodcastClick = onPodcastClick
            )
        }
        
        item {
            GenreSection(
                title = "Trending in Your Interests",
                onPodcastClick = onPodcastClick
            )
        }
    }
}

@Composable
private fun NewContent(onPodcastClick: (Podcast) -> Unit) {
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
                title = "This Week's New Shows",
                onPodcastClick = onPodcastClick
            )
        }
        
        item {
            GenreSection(
                title = "Fresh Episodes",
                onPodcastClick = onPodcastClick
            )
        }
    }
}

@Composable
private fun GenreSection(
    title: String,
    onPodcastClick: (Podcast) -> Unit,
    searchUseCase: SearchPodcastsUseCase = koinInject()
) {
    var podcasts by remember { mutableStateOf<List<Podcast>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    
    LaunchedEffect(title) {
        try {
            val searchQuery = when (title) {
                "Health & Fitness" -> "health fitness"
                "Business" -> "business entrepreneurship"
                "Technology" -> "technology programming"
                else -> title.lowercase()
            }
            
            val result = searchUseCase(searchQuery)
            podcasts = result.getOrElse { emptyList() }.take(6)
        } catch (e: Exception) {
            podcasts = emptyList()
        } finally {
            isLoading = false
        }
    }
    
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