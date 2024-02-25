package dev.orangepie.player.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.orangecast.player.ui.R
import dev.orangepie.base.ui.theme.Color
import dev.orangepie.base.ui.theme.TextStyle
import dev.orangepie.player.ui.model.PodcastEpisodePlayerUiModel

@Composable
fun PlayerBottomPanel(
    item: PodcastEpisodePlayerUiModel,
    onPlayClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        backgroundColor = Color.BackgroundBlack,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            LinearProgressIndicator(
                progress = .5f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = Color.Orange,
                strokeCap = StrokeCap.Round
            )
            Row(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
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
                        minLines = 1,
                        maxLines = 1,
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
                Spacer(modifier = Modifier.width(4.dp))
                PlayButton(
                    modifier = Modifier
                        .padding(8.dp),
                    isLoading = false,
                    isPlaying = false,
                    onClick = onPlayClick
                )
            }
        }
    }
}

@Preview
@Composable
fun PlayerBottomPanelPreview() {
    PlayerBottomPanel(
        item = PodcastEpisodePlayerUiModel(
            title = "Mark Cuban: “And for that reason I’m out” - Shark Tank Podcast - ABC Podcasts - ABC Audio",
            episode = "2",
            season = "4",
            pubDate = "22.09.2023",
            itunesDuration = "02:34:12"
        ),
        onPlayClick = {}
    )
}