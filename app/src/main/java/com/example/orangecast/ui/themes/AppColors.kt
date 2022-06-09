package com.example.orangecast.ui.themes

import androidx.compose.material.darkColors
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import kotlin.math.max
import kotlin.math.min

val AppColors = darkColors(
    primary = Color(0xFFFFC328),
    onPrimary = Color.Black,
    secondary = Color(0xFFFF3D00),
    onSecondary = Color.Black,
    background = Color(0xFF3B3C40),
    error = Color.Red
)

const val MinContrastOfPrimaryVsSurface = 3f


fun Color.contrastAgainst(background: Color): Float {
    val fg = if (alpha < 1f) compositeOver(background) else this

    val fgLuminance = fg.luminance() + 0.05f
    val bgLuminance = background.luminance() + 0.05f

    return max(fgLuminance, bgLuminance) / min(fgLuminance, bgLuminance)
}