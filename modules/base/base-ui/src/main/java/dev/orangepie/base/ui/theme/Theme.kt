package dev.orangepie.base.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable

private val OrangeCastColorPalette = darkColors(
    primary = Color.Yellow,
    primaryVariant = Color.Yellow,
    secondary = Color.Orange,
    secondaryVariant = Color.Orange,
    background = Color.Background,
    surface = Color.Background,
    error = Color.Red,
    onPrimary = Color.Background,
    onSecondary = Color.Background,
    onBackground = Color.Yellow,
    onSurface = Color.Yellow,
    onError = Color.Yellow,
)

@Composable
fun OrangeCastTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colors = OrangeCastColorPalette,
        typography = OrangeCastTypography,
        shapes = androidx.compose.material.Shapes(
            small = Shapes.Small,
            medium = Shapes.Medium,
            large = Shapes.Large,
        ),
    ) {
        content()
    }
}