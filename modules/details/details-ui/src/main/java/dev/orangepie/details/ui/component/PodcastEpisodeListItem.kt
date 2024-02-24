package dev.orangepie.details.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.orangecast.details.ui.R
import dev.orangepie.base.ui.theme.Color
import dev.orangepie.base.ui.theme.TextStyle
import dev.orangepie.details.ui.model.PodcastRSSFeedItemUIModel
import dev.orangepie.details.ui.model.PodcastRSSFeedItemUIState
import dev.orangepie.player.ui.components.PlayButton

@Composable
fun PodcastEpisodeListItem(
    item: PodcastRSSFeedItemUIModel,
    onPlayClick: (item: PodcastRSSFeedItemUIModel) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.Background,
                        Color.BackgroundBlack
                    ),
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .weight(3f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (!item.season.isNullOrEmpty() && !item.episode.isNullOrEmpty()) {
                        Text(
                            text = "S${item.season} E${item.episode}".uppercase(),
                            style = TextStyle.B5,
                            color = Color.White50
                        )
                        Image(
                            modifier = Modifier
                                .size(2.dp),
                            painter = painterResource(id = R.drawable.ic_dot),
                            contentDescription = null
                        )
                    }
                    if (!item.pubDate.isNullOrEmpty()) {
                        Text(
                            text = item.pubDate.uppercase(),
                            style = TextStyle.B5,
                            color = Color.White50
                        )
                    }
                }
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = item.title ?: "",
                    style = TextStyle.B2,
                    color = Color.White,
                    minLines = 2,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Image(
                        modifier = Modifier
                            .size(10.dp)
                            .alpha(.5f),
                        painter = painterResource(id = R.drawable.ic_clock_white),
                        contentDescription = null
                    )
                    Text(
                        text = item.itunesDuration?.uppercase() ?: "",
                        style = TextStyle.B5,
                        color = Color.White50
                    )
                }
            }
            Box(
                modifier = Modifier
                    .weight(1f),
                contentAlignment = Alignment.CenterEnd
            ) {
                PlayButton(
                    modifier = Modifier
                        .padding(start = 8.dp),
                    isLoading = item.state is PodcastRSSFeedItemUIState.Loading,
                    isPlaying = item.state is PodcastRSSFeedItemUIState.Playing,
                    onClick = { onPlayClick(item) }
                )
            }
        }
    }
}

@Preview
@Composable
fun PodcastEpisodeListItemPreview() {
    PodcastEpisodeListItem(
        item = PodcastRSSFeedItemUIModel(
            title = "Mark Cuban: “And for that reason I’m out” - Shark Tank Podcast - ABC Podcasts - ABC Audio - ABC News - ABC News",
            description = "Description",
            audio = "",
            link = "Link",
            pubDate = "02 Aug 2023",
            episode = "145",
            season = "2",
            itunesDuration = "02:58:45",
            itunesSummary = "iTunesSummary",
        ),
        onPlayClick = {},
    )
}

