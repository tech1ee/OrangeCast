package dev.orangepie.podcasts.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import dev.orangepie.base.ui.theme.Color
import dev.orangepie.base.ui.theme.TextStyle
import dev.orangepie.podcasts.ui.model.PodcastChannelUIModel

@Composable
fun PodcastChannelListItem(
    model: PodcastChannelUIModel,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            SubcomposeAsyncImage(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape),
                model = model.imagePreviewUrl,
                contentDescription = null
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = model.title ?: "",
                    style = TextStyle.B1
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = model.genre ?: "",
                    style = TextStyle.B2,
                    color = Color.Grey1
                )
            }
        }
        Divider(
            modifier = Modifier
                .height(1.dp)
                .padding(horizontal = 24.dp),
            color = Color.Grey
        )
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
            genre = "Comedy",
            itunesId = 360084272
        ),
        onClick = {}
    )
}