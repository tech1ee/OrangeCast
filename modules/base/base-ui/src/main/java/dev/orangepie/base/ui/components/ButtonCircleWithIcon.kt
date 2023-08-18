package dev.orangepie.base.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.orangepie.base.ui.theme.Color

@Composable
fun ButtonCircleWithIcon(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    icon: @Composable () -> Unit = {},
) {
    IconButton(
        modifier = modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(Color.BackgroundBlack),
        onClick = onClick,
    ) {
        icon()
    }
}

@Preview
@Composable
fun ButtonCircleWithIconPreview() {
    ButtonCircleWithIcon(
        onClick = {},
        icon = {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            )
        }
    )
}