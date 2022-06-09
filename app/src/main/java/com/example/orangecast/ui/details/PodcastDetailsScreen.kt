package com.example.orangecast.ui.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.orangecast.R
import com.example.orangecast.ui.themes.MinContrastOfPrimaryVsSurface
import com.example.orangecast.ui.themes.contrastAgainst
import com.example.orangecast.ui.util.DynamicThemePrimaryColorsFromImage
import com.example.orangecast.ui.util.rememberDominantColorState
import com.example.orangecast.ui.util.verticalGradientScrim

@Composable
fun PodcastDetailsScreen(
    uiState: PodcastDetailsViewState,
    onBackPress: () -> Unit
) {
    PodcastDynamicTheme(uiState.data?.image) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalGradientScrim(
                    color = MaterialTheme.colors.primary.copy(alpha = 0.50f),
                    startYPercentage = 1f,
                    endYPercentage = 0f
                )
                .safeContentPadding().padding(horizontal = 8.dp)
        ) {
            TopAppBar(
                onBackPress = onBackPress
            )
            Row(
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                PodcastImage(
                    podcastImageUrl = uiState.data?.image,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                PodcastDescription(
                    title = uiState.data?.title,
                    description = uiState.data?.description
                )
            }
        }
    }
}

@Composable
private fun TopAppBar(onBackPress: () -> Unit) {
    Row(Modifier.fillMaxWidth()) {
        IconButton(onClick = onBackPress) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = stringResource(R.string.back)
            )
        }
        Spacer(Modifier.weight(1f))
        IconButton(onClick = { /* TODO */ }) {
            Icon(
                imageVector = Icons.Default.PlaylistAdd,
                contentDescription = stringResource(R.string.add)
            )
        }
        IconButton(onClick = { /* TODO */ }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.more)
            )
        }
    }
}

@Composable
private fun PodcastImage(
    podcastImageUrl: String?,
    modifier: Modifier = Modifier
) {
    Image(
        painter = rememberImagePainter(
            data = podcastImageUrl,
            builder = {
                crossfade(true)
            }
        ),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .sizeIn(maxWidth = 500.dp, maxHeight = 500.dp)
            .aspectRatio(1f)
            .clip(MaterialTheme.shapes.medium)
    )
}

@Composable
private fun PodcastDescription(
    title: String?,
    description: String?,
    titleTextStyle: TextStyle = MaterialTheme.typography.h5
) {
    title?.let {
        Text(
            text = it,
            style = titleTextStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
    description?.let {
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = it,
                style = MaterialTheme.typography.body2,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun PodcastDynamicTheme(
    podcastImageUrl: String?,
    content: @Composable () -> Unit
) {
    val surfaceColor = MaterialTheme.colors.surface
    val dominantColorState = rememberDominantColorState(
        defaultColor = MaterialTheme.colors.surface
    ) { color ->
        // We want a color which has sufficient contrast against the surface color
        color.contrastAgainst(surfaceColor) >= MinContrastOfPrimaryVsSurface
    }
    DynamicThemePrimaryColorsFromImage(dominantColorState) {
        // Update the dominantColorState with colors coming from the podcast image URL
        LaunchedEffect(podcastImageUrl) {
            if (!podcastImageUrl.isNullOrEmpty()) {
                dominantColorState.updateColorsFromImageUrl(podcastImageUrl)
            } else {
                dominantColorState.reset()
            }
        }
        content()
    }
}