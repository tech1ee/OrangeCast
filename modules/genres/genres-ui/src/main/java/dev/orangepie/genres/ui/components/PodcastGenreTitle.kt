package dev.orangepie.genres.ui.components

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import dev.orangepie.base.ui.theme.Color
import dev.orangepie.base.ui.theme.OrangeCastTypography

@Composable
fun PodcastGenreTitle(
    modifier: Modifier = Modifier,
    title: String,
) {
    Text(
        modifier = modifier,
        text = title,
        fontSize = 18.sp,
        style = OrangeCastTypography.h3,
        color = Color.Orange,
    )
}