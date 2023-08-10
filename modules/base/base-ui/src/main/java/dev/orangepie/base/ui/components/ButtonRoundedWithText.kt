package dev.orangepie.base.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.orangepie.base.ui.theme.Color
import dev.orangepie.base.ui.theme.TextStyle

@Composable
fun ButtonRoundedWithText(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    selected: Boolean = false,
) {
    Card(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(50))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(50),
        border = if (selected) BorderStroke(1.dp, Color.Yellow.copy(alpha = .2f))
        else BorderStroke(1.dp, Color.BackgroundBlack.copy(alpha = .2f)),
        elevation = 8.dp
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        colors = if (selected) listOf(
                            Color.Yellow,
                            Color.Orange
                        )
                        else listOf(
                            Color.Background,
                            Color.BackgroundBlack,
                        )
                    )
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                modifier = Modifier
                    .padding(vertical = 4.dp, horizontal = 16.dp),
                text = text,
                style = TextStyle.Button,
                color = Color.White
            )
        }
    }
}

@Preview
@Composable
fun ButtonRoundedWithTextPreview() {
    ButtonRoundedWithText(
        text = "Unsubscribe",
        onClick = {},
    )
}