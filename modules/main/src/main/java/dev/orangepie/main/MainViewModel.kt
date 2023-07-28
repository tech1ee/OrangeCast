package dev.orangepie.main

import dev.orangepie.base.ui.BaseViewModel
import dev.orangepie.base.ui.navigation.NavCommand
import dev.orangepie.base.ui.navigation.Navigator
import dev.orangepie.main.model.NavBarScreen
import javax.inject.Inject

class MainViewModel @Inject constructor(): BaseViewModel() {

    fun onBottomNavClick(navBarScreen: NavBarScreen) {
        navigate(
            NavCommand.Navigate(
                router = Navigator.Router.ROOT,
                route = navBarScreen.route.getRoute(),
            )
        )
    }

}