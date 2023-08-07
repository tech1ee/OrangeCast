package dev.orangepie.genres.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import dev.orangepie.base.ui.components.GradientText
import dev.orangepie.base.ui.theme.Color

@Composable
fun PodcastGenreTitle(
    modifier: Modifier = Modifier,
    title: String,
    textSize: TextUnit = TextUnit.Unspecified,
) {
    GradientText(
        modifier = modifier,
        text = title,
        textSize = textSize,
        gradient = listOf(
            Color.Yellow,
            Color.Orange
        ),
    )
}