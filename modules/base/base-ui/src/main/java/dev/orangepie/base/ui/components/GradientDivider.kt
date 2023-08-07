package dev.orangepie.base.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun GradientDivider(
    modifier: Modifier = Modifier,
    height: Dp = 1.dp,
    gradient: List<Color> = listOf(
        dev.orangepie.base.ui.theme.Color.White10,
        dev.orangepie.base.ui.theme.Color.White,
        dev.orangepie.base.ui.theme.Color.White10
    )
) {
    Box(
        modifier = modifier
            .height(height)
            .background(
                brush = Brush.horizontalGradient(gradient)
            ),
    )
}