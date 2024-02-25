package dev.orangepie.podcasts.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import dev.orangepie.base.ui.animation.clickAnimatedScale
import dev.orangepie.base.ui.theme.Color
import dev.orangepie.base.ui.theme.TextStyle
import dev.orangepie.podcasts.ui.model.PodcastChannelUIModel
import dev.orangepie.genres.ui.model.PodcastGenreUiModel

@Composable
fun PodcastChannelListItem(
    model: PodcastChannelUIModel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .width(136.dp)
            .clickAnimatedScale(onClick = onClick),
        backgroundColor = Color.BackgroundDark,
        shape = RoundedCornerShape(16.dp),
        elevation = 8.dp
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            SubcomposeAsyncImage(
                modifier = Modifier
                    .padding(8.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                model = model.imagePreviewUrl,
                contentDescription = null
            )
            Text(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth(),
                text = model.title ?: "",
                style = TextStyle.B2.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = Color.White,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                minLines = 2,
                maxLines = 2
            )
            Text(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                text = model.genres.joinToString(", ") { it.name },
                style = TextStyle.overline,
                color = Color.White50,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    }
}

@Preview
@Composable
fun PodcastChannelListItemPreview() {
    PodcastChannelListItem(
        model = PodcastChannelUIModel(
            title = "The Joe Rogan Experience",
            imagePreviewUrl = "https://i.scdn.co/image/ab6761610000e5ebd5b7b3f3b2a3b7b2b2a3b7b2",
            imageFullUrl = null,
            genres = listOf(
                PodcastGenreUiModel(
                    id = 1303,
                    name = "Comedy",
                    parentId = null,
                ),
                PodcastGenreUiModel(
                    id = 1309,
                    name = "Society & Culture",
                    parentId = null,
                ),
                PodcastGenreUiModel(
                    id = 1321,
                    name = "News",
                    parentId = null,
                ),
                PodcastGenreUiModel(
                    id = 1333,
                    name = "Politics",
                    parentId = null,
                ),
            ),
            itunesId = 360084272
        ),
        onClick = {}
    )
}