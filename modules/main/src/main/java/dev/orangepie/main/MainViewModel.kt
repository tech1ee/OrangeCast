package dev.orangepie.main

import dagger.hilt.android.lifecycle.HiltViewModel
import dev.orangepie.base.ui.BaseViewModel
import dev.orangepie.base.ui.navigation.NavCommand
import dev.orangepie.base.ui.navigation.Navigator
import dev.orangepie.main.mapper.MainUIMapper
import dev.orangepie.main.model.MainUIState
import dev.orangepie.main.model.MainViewModelState
import dev.orangepie.main.model.NavBarScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mapper: MainUIMapper
): BaseViewModel() {

    private val viewModelState = MutableStateFlow(MainViewModelState())
    val uiState = viewModelState
        .map(mapper::toUIState)
        .stateInViewModel(mapper::toUIStateBlocking)

    fun onSplashShown() {
        viewModelState.update {
            it.copy(splashShown = true)
        }
    }

    fun onBottomNavClick(navBarScreen: NavBarScreen) {
        navigate(
            NavCommand.Navigate(
                router = Navigator.Router.CHILD,
                route = navBarScreen.route.getRoute(),
            )
        )
    }

}