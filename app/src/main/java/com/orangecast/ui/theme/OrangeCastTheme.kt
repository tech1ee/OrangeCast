package com.orangecast.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFFF6B35),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFB366),
    onPrimaryContainer = Color(0xFF1C1B1F),
    secondary = Color(0xFFFFB366),
    onSecondary = Color.White,
    background = Color.White,
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFF8F9FA),
    onSurface = Color(0xFF1C1B1F),
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFF6B35),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE55A2B),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFFFFB366),
    onSecondary = Color.Black,
    background = Color(0xFF121212),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE6E1E5),
)

@Composable
fun OrangeCastTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}