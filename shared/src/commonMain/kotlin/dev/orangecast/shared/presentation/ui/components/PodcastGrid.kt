package dev.orangecast.shared.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.orangecast.shared.domain.model.Podcast

@Composable
fun PodcastGrid(
    podcasts: List<Podcast>,
    onPodcastClick: (Podcast) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp)
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(contentPadding),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        podcasts.chunked(2).forEach { rowPodcasts ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowPodcasts.forEach { podcast ->
                    PodcastCard(
                        podcast = podcast,
                        onClick = { onPodcastClick(podcast) },
                        layoutStyle = PodcastCardLayoutStyle.Grid,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(0.75f)
                    )
                }
                
                if (rowPodcasts.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}