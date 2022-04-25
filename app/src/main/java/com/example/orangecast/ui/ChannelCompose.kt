package com.example.orangecast.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.example.orangecast.R
import com.example.orangecast.entity.Category
import com.example.orangecast.entity.Channel

@Composable
fun ChannelsByCategoryColumn(
    channels: List<Channel>,
    onSubscribeClicked: (Channel) -> Unit,
    modifier: Modifier = Modifier
) {
   LazyColumn(
       modifier = modifier
   ) {

       ChannelsRow(
               channels = channels,
               onSubscribeClicked = onSubscribeClicked,
               modifier = modifier
       )
   }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun ChannelsRow(
    channels: List<Channel>,
    onSubscribeClicked: (Channel) -> Unit,
    modifier: Modifier = Modifier
) {
    val lastIndex = channels.size - 1
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 16.dp)
    ) {
        channels.forEachIndexed { index, channel ->
            ChannelsRowItem(
                title = channel.title ?: "",
                imageUrl = channel.image,
                isSubscribed = channel.isSubscribed,
                onSubscribeClicked = { onSubscribeClicked(channel) },
                modifier = Modifier.width(128.dp)
            )

            if (index < lastIndex) Spacer(Modifier.width(24.dp))
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
private fun ChannelsRowItem(
    title: String,
    imageUrl: String?,
    isSubscribed: Boolean,
    onSubscribeClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier.semantics(mergeDescendants = true){}
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .align(Alignment.CenterHorizontally)
        ) {
            if (imageUrl != null) {
                Image(
                    painter = rememberImagePainter(
                        data = imageUrl,
                        builder = {
                            crossfade(true)
                        }
                    ),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(MaterialTheme.shapes.medium)
                )
            }

            SubscribeChannelIconButton(
                onClick = onSubscribeClicked,
                isSubscribed = isSubscribed,
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }

        Text(
            text = title,
            style = MaterialTheme.typography.body2,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
fun SubscribeChannelIconButton(
    isSubscribed: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val clickLabel = stringResource(if (isSubscribed) R.string.subscribe else R.string.unsubscribe)
    IconButton(
        onClick = onClick,
        modifier = modifier.semantics {
            onClick(label = clickLabel, action = null)
        }
    ) {
        Icon(
            // TODO: think about animating these icons
            imageVector = when {
                isSubscribed -> Icons.Default.Check
                else -> Icons.Default.Add
            },
            contentDescription = when {
                isSubscribed -> stringResource(R.string.subscribed)
                else -> stringResource(R.string.unsubscribed)
            },
            tint = animateColorAsState(
                when {
                    isSubscribed -> LocalContentColor.current
                    else -> Color.Black.copy(alpha = ContentAlpha.high)
                }
            ).value,
            modifier = Modifier
                .shadow(
                    elevation = animateDpAsState(if (isSubscribed) 0.dp else 1.dp).value,
                    shape = MaterialTheme.shapes.small
                )
                .background(
                    color = animateColorAsState(
                        when {
                            isSubscribed -> MaterialTheme.colors.surface.copy(0.38f)
                            else -> Color.White
                        }
                    ).value,
                    shape = MaterialTheme.shapes.small
                )
                .padding(4.dp)
        )
    }
}