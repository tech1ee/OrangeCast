package dev.orangepie.base.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val LightColorPalette = lightColors(
    primary = Color.Orange,
    primaryVariant = Color.Orange,
    secondary = Color.Orange,
    secondaryVariant = Color.Orange,
    background = Color.White,
    surface = Color.White,
    error = Color.Red,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black,
    onError = Color.White,
)

private val DarkColorPalette = darkColors(
    primary = Color.Orange,
    primaryVariant = Color.Orange,
    secondary = Color.Orange,
    secondaryVariant = Color.Orange,
    background = Color.BackgroundDark,
    surface = Color.BackgroundDark,
    error = Color.Red,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    onError = Color.White,
)

@Composable
fun OrangeCastTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
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