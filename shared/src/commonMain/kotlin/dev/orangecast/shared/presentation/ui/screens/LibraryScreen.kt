package dev.orangecast.shared.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import dev.orangecast.shared.presentation.ui.components.ShimmerBox
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LibraryBooks
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import dev.orangecast.shared.presentation.ui.theme.OrangeCastColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.orangecast.shared.domain.model.Podcast
import dev.orangecast.shared.presentation.ui.components.PodcastGrid
import dev.orangecast.shared.presentation.ui.components.ShimmerPodcastCard
import dev.orangecast.shared.presentation.viewmodel.LibraryViewModel
import org.koin.compose.koinInject

@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel = koinInject(),
    onPodcastClick: (Podcast) -> Unit = {},
    isLoading: Boolean = false
) {
    val subscribedPodcasts by viewModel.subscribedPodcasts.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = "Library",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        when {
            isLoading -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(6) {
                        ShimmerPodcastCard()
                    }
                }
            }
            subscribedPodcasts.isEmpty() -> {
                EmptyLibraryState()
            }
            else -> {
                PodcastGrid(
                    podcasts = subscribedPodcasts,
                    onPodcastClick = onPodcastClick,
                    contentPadding = PaddingValues(vertical = 8.dp, horizontal = 0.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptyLibraryState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.LibraryBooks,
                contentDescription = "Library",
                modifier = Modifier.size(64.dp),
                tint = OrangeCastColors.PrimaryOrange
            )
            
            Text(
                text = "Your Library",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Text(
                text = "Subscribe to podcasts to build your personal library. Go to Discover to find podcasts you love!",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(0.9f)
            )
            
        }
    }
}

@Composable
private fun ShimmerPodcastListItem() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ShimmerBox(
                modifier = Modifier.size(60.dp),
                shape = RoundedCornerShape(8.dp)
            )
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .size(height = 18.dp, width = 150.dp),
                    shape = RoundedCornerShape(4.dp)
                )
                
                ShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .size(height = 14.dp, width = 100.dp),
                    shape = RoundedCornerShape(4.dp)
                )
            }
        }
    }
}