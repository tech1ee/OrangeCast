@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package dev.orangecast.shared.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.orangecast.shared.domain.model.Podcast

/**
 * Unified podcast card component that can be used across different screens
 * Supports both grid layout (square) and list layout (horizontal)
 */
@Composable
fun PodcastCard(
    podcast: Podcast,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    layoutStyle: PodcastCardLayoutStyle = PodcastCardLayoutStyle.Grid
) {
    when (layoutStyle) {
        PodcastCardLayoutStyle.Grid -> {
            GridPodcastCard(
                podcast = podcast,
                onClick = onClick,
                modifier = modifier
            )
        }
        PodcastCardLayoutStyle.List -> {
            ListPodcastCard(
                podcast = podcast,
                onClick = onClick,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun GridPodcastCard(
    podcast: Podcast,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(232.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            AsyncImage(
                url = podcast.imageUrl,
                contentDescription = podcast.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                contentScale = ContentScale.Crop
            )
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = podcast.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = podcast.author,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ListPodcastCard(
    podcast: Podcast,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        AsyncImage(
            url = podcast.imageUrl,
            contentDescription = podcast.title,
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = podcast.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Text(
                text = podcast.author,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            if (podcast.description.isNotBlank()) {
                Text(
                    text = podcast.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Shimmer version of the podcast card for loading states
 */
@Composable
fun PodcastCardShimmer(
    layoutStyle: PodcastCardLayoutStyle = PodcastCardLayoutStyle.Grid,
    modifier: Modifier = Modifier
) {
    when (layoutStyle) {
        PodcastCardLayoutStyle.Grid -> {
            GridPodcastCardShimmer(modifier)
        }
        PodcastCardLayoutStyle.List -> {
            ListPodcastCardShimmer(modifier)
        }
    }
}

@Composable
private fun GridPodcastCardShimmer(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.8f)
            .clip(RoundedCornerShape(12.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                .shimmerEffect()
        )
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(14.dp)
                    .shimmerEffect()
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(12.dp)
                    .shimmerEffect()
            )
        }
    }
}

@Composable
private fun ListPodcastCardShimmer(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(8.dp))
                .shimmerEffect()
        )
        
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(18.dp)
                    .shimmerEffect()
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(14.dp)
                    .shimmerEffect()
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .shimmerEffect()
            )
        }
    }
}

@Composable
private fun Modifier.shimmerEffect(): Modifier {
    return this.then(
        Modifier.background(
            brush = ShimmerBrush(),
            shape = RoundedCornerShape(4.dp)
        )
    )
}

enum class PodcastCardLayoutStyle {
    Grid,
    List
}