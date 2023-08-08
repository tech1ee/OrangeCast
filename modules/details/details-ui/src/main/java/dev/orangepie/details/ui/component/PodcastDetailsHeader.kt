package dev.orangepie.details.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.orangecast.details.ui.R
import dev.orangepie.base.ui.components.ButtonCircleWithIcon
import dev.orangepie.base.ui.components.ButtonRoundedWithText
import dev.orangepie.base.ui.theme.Color
import dev.orangepie.base.ui.theme.TextStyle
import dev.orangepie.details.ui.model.PodcastDetailsUIModel
import dev.orangepie.details.ui.model.PodcastRSSFeedEnclosureUIModel
import dev.orangepie.details.ui.model.PodcastRSSFeedImageUIModel
import dev.orangepie.details.ui.model.PodcastRSSFeedItemUIModel
import dev.orangepie.details.ui.model.PodcastRSSFeedUIModel

@Composable
fun PodcastDetailsHeader(
    details: PodcastDetailsUIModel,
    onBackClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            ButtonCircleWithIcon(
                onClick = onBackClick,
                icon = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_back_arrow),
                        contentDescription = null
                    )
                }
            )
            ButtonCircleWithIcon(
                onClick = onBackClick,
                icon = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_share),
                        contentDescription = null
                    )
                }
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 8.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Background,
                            Color.BackgroundBlack,
                        )
                    ),
                    shape = RoundedCornerShape(topStart = 70.dp, bottomStart = 70.dp)
                )
        ) {
            Card(
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape),
                elevation = 8.dp
            ) {
                SubcomposeAsyncImage(
                    model = details.artworkUrl100,
                    contentScale = ContentScale.Crop,
                    contentDescription = null
                )
            }
            Spacer(modifier = Modifier.size(16.dp))
            Column(
                modifier = Modifier.padding(end = 16.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = details.collectionName,
                    style = TextStyle.H2,
                    color = Color.White,
                    maxLines = 2,
                    minLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    text = details.artistName,
                    style = TextStyle.B3,
                    color = Color.White50,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    text = details.genres.joinToString(", "),
                    style = TextStyle.overline,
                    color = Color.Grey1,
                    minLines = 1,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.End,
                )
            }
        }

        ButtonRoundedWithText(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .width(120.dp)
                .align(alignment = Alignment.End),
            text = stringResource(id =
            if (details.isSubscribed) com.orangecast.common.R.string.details_unsubscribe_button
            else com.orangecast.common.R.string.details_subscribe_button),
            onClick = {},
        )
    }
}

@Preview
@Composable
fun PodcastDetailsHeaderPreview() {
    PodcastDetailsHeader(
        details = PodcastDetailsUIModel(
            collectionId = 1,
            trackId = 1,
            artistName = "Joe Rogan",
            collectionName = "The Joe Rogan Experience",
            trackName = "trackName",
            collectionViewUrl = "collectionViewUrl",
            feedUrl = "feedUrl",
            artworkUrl100 = "artworkUrl100",
            genres = listOf("genre1", "genre2"),
            feed = PodcastRSSFeedUIModel(
                description = "description",
                items = listOf(),
            )
        ),
        onBackClick = {},
    )
}