package dev.orangepie.main.model

sealed class MainUIState {
    object Splash: MainUIState()
    object Home: MainUIState()
}
