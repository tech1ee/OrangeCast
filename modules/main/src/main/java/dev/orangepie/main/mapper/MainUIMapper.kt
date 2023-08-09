package dev.orangepie.main.mapper

import dev.orangepie.main.model.MainUIState
import dev.orangepie.main.model.MainViewModelState
import javax.inject.Inject

class MainUIMapper @Inject constructor() {

    suspend fun toUIState(state: MainViewModelState): MainUIState {
        return toUIStateBlocking(state)
    }

    fun toUIStateBlocking(state: MainViewModelState): MainUIState {
        return if (state.splashShown) {
            MainUIState.Home
        } else {
            MainUIState.Splash
        }
    }
}