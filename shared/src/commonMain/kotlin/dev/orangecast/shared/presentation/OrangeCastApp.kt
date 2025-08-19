package dev.orangecast.shared.presentation

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import dev.orangecast.shared.presentation.ui.components.ProvideShimmerBrush
import dev.orangecast.shared.presentation.ui.screens.MainScreen

@Composable
fun OrangeCastApp() {
    MaterialTheme {
        ProvideShimmerBrush {
            MainScreen()
        }
    }
}