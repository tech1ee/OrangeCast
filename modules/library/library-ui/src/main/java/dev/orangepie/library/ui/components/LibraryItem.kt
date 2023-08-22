package dev.orangepie.library.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
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
import dev.orangepie.base.ui.theme.Color
import dev.orangepie.base.ui.theme.TextStyle
import dev.orangepie.podcasts.ui.model.PodcastChannelUIModel

@Composable
fun LibraryItem(
    model: PodcastChannelUIModel,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SubcomposeAsyncImage(
                modifier = Modifier
                    .sizeIn(minWidth = 80.dp, minHeight = 80.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                model = model.imagePreviewUrl,
                contentDescription = null
            )
            Text(
                modifier = Modifier
                    .padding(top = 8.dp),
                text = model.title ?: "",
                style = TextStyle.B5.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = Color.White,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                minLines = 2,
                maxLines = 2
            )
        }
    }
}

@Preview
@Composable
fun LibraryItemPreview() {
    LibraryItem(
        model = PodcastChannelUIModel(
            itunesId = 0,
            title = "The Joe Rogan Experience",
            imagePreviewUrl = "https://is1-ssl.mzstatic.com/image/thumb/Podcasts113/v4/4a/0e/1a/4a0e1a4e-0e1e-4e1e-4b0e-2b4b1e1b4b1e/mza_11780582780580588606.jpg/200x200bb.png",
            imageFullUrl = "https://is1-ssl.mzstatic.com/image/thumb/Podcasts113/v4/4a/0e/1a/4a0e1a4e-0e1e-4e1e-4b0e-2b4b1e1b4b1e/mza_11780582780580588606.jpg/200x200bb.png",
        ),
        onClick = {}
    )
}