package dev.orangepie.base.ui.components

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import dev.orangepie.base.ui.modifier.brush
import dev.orangepie.base.ui.theme.TextStyle

@Composable
fun GradientText(
    modifier: Modifier = Modifier,
    text: String,
    gradient: List<Color>,
    textSize: TextUnit = TextUnit.Unspecified,
    orientation: Orientation = Orientation.Horizontal
) {
    Text(
        modifier = modifier
            .brush(
                brush = if (orientation == Orientation.Horizontal) {
                    Brush.horizontalGradient(gradient)
                } else {
                    Brush.verticalGradient(gradient)
                }
            ),
        text = text,
        style = if (textSize != TextUnit.Unspecified) {
            TextStyle.H3.copy(fontSize = textSize)
        } else {
            TextStyle.H3
        },
    )
}