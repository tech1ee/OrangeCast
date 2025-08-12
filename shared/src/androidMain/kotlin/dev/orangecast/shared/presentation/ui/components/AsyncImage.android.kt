package dev.orangecast.shared.presentation.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale

@Composable
actual fun AsyncImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier,
    contentScale: ContentScale
) {
    PlaceholderImage(modifier = modifier)
}