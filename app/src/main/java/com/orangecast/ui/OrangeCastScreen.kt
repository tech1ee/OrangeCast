package com.orangecast.ui

import androidx.compose.runtime.Composable
import com.orangecast.ui.theme.OrangeCastTheme
import dev.orangecast.shared.presentation.OrangeCastApp

@Composable
fun OrangeCastScreen() {
    OrangeCastTheme {
        OrangeCastApp()
    }
}