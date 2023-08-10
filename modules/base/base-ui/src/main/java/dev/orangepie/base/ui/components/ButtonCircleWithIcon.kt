package dev.orangepie.base.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import dev.orangepie.base.ui.theme.Color

@Composable
fun ButtonCircleWithIcon(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    icon: @Composable () -> Unit = {},
) {
    Card(
        modifier = modifier
            .size(44.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        shape = CircleShape,
        border = BorderStroke(1.dp, Color.BackgroundBlack.copy(alpha = .2f)),
        elevation = 8.dp
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.Background,
                            Color.BackgroundBlack,
                        )
                    )
                ),
            contentAlignment = Alignment.Center,
        ) {
            icon()
        }
    }
}