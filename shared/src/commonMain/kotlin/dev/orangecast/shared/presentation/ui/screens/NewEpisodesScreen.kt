package dev.orangecast.shared.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import dev.orangecast.shared.presentation.ui.theme.OrangeCastColors
import dev.orangecast.shared.presentation.ui.components.ShimmerBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.orangecast.shared.domain.model.PodcastEpisode
import dev.orangecast.shared.presentation.ui.components.AsyncImage
import dev.orangecast.shared.presentation.viewmodel.NewEpisodesViewModel
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.koinInject

@Composable
fun NewEpisodesScreen() {
    val viewModel: NewEpisodesViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when {
        uiState.isLoading -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "New Episodes",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                
                items(5) {
                    ShimmerEpisodeCard()
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
                        text = uiState.error ?: "Unknown error",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                    Button(
                        onClick = { viewModel.onRetry() }
                    ) {
                        Text("Retry")
                    }
                }
            }
        }
        
        uiState.episodes.isEmpty() -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "No new episodes from your subscriptions",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                    Button(
                        onClick = { viewModel.refreshEpisodes() },
                        enabled = !uiState.isRefreshing
                    ) {
                        Text(if (uiState.isRefreshing) "Refreshing..." else "Refresh Episodes")
                    }
                }
            }
        }
        
        else -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "New Episodes",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                items(uiState.episodes) { episode ->
                    EpisodeCard(
                        episode = episode,
                        onPlayClick = { }
                    )
                }
            }
        }
        }
    }
}

@Composable
private fun EpisodeCard(
    episode: PodcastEpisode,
    onPlayClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPlayClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                url = episode.thumbnailUrl,
                contentDescription = episode.podcastTitle,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = episode.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Black
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatDate(episode.publishedAt),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        text = formatDuration(episode.duration),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                Text(
                    text = episode.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        OrangeCastColors.PrimaryOrange,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable { onPlayClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val instant = Instant.fromEpochMilliseconds(timestamp)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${localDateTime.dayOfMonth}/${localDateTime.monthNumber}/${localDateTime.year}"
}

private fun formatDuration(durationSeconds: Long): String {
    if (durationSeconds == 0L) return "Unknown"
    val hours = durationSeconds / 3600
    val minutes = (durationSeconds % 3600) / 60
    return if (hours > 0) {
        "${hours}h ${minutes}m"
    } else {
        "${minutes}m"
    }
}

@Composable
private fun ShimmerEpisodeCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ShimmerBox(
                modifier = Modifier
                    .size(80.dp),
                shape = RoundedCornerShape(8.dp)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .size(height = 20.dp, width = 200.dp),
                    shape = RoundedCornerShape(4.dp)
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ShimmerBox(
                        modifier = Modifier
                            .size(height = 14.dp, width = 60.dp),
                        shape = RoundedCornerShape(4.dp)
                    )
                    ShimmerBox(
                        modifier = Modifier
                            .size(height = 14.dp, width = 40.dp),
                        shape = RoundedCornerShape(4.dp)
                    )
                }
                
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    repeat(2) {
                        ShimmerBox(
                            modifier = Modifier
                                .fillMaxWidth(if (it == 1) 0.7f else 1f)
                                .size(height = 14.dp, width = 200.dp),
                            shape = RoundedCornerShape(4.dp)
                        )
                    }
                }
            }

            ShimmerBox(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}