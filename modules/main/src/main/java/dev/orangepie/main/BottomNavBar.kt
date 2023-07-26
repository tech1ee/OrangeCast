package dev.orangepie.main

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import dev.orangepie.base.ui.navigation.NavCommand
import dev.orangepie.base.ui.navigation.Navigator
import dev.orangepie.main.model.NavBarScreen
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
fun BottomNavBar(
    navController: NavHostController,
    innerPadding: PaddingValues,
    startTab: String,
) {
    LaunchedEffect(true) {
        Navigator.childRouterFlow
            .onEach {
                when (val navCommand = it.getContentIfNotHandled()) {
                    is NavCommand.Back -> {
                        navCommand.result?.let { result ->
                            navController.previousBackStackEntry?.savedStateHandle?.let {
                                result.forEach { (key, value) ->
                                    it.set(key, value)
                                }
                            }
                        }
                        navController.popBackStack()
                    }
                    is NavCommand.BackTo -> {
                        navCommand.result?.let { result ->
                            navController.previousBackStackEntry?.savedStateHandle?.let {
                                result.forEach { (key, value) ->
                                    it.set(key, value)
                                }
                            }
                        }
                        navController.popBackStack(
                            route = navCommand.route,
                            inclusive = false,
                            saveState = false,
                        )
                    }
                    is NavCommand.Navigate -> {
                        when (navCommand.route) {
                            NavBarScreen.Discover.route.getRoute(),
                            NavBarScreen.Search.route.getRoute(),
                            NavBarScreen.Library.route.getRoute(),-> {
                                navController.navigate(
                                    navCommand.route
                                ) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                            else -> navController.navigate(
                                navCommand.route,
                                navCommand.builder,
                            )
                        }
                    }
                    is NavCommand.Reset -> navController.navigate(startTab) {
                        popUpTo(NavBarScreen.Discover.route.getRoute()) { inclusive = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                    null -> Unit
                }
            }
            .launchIn(this)
    }
    NavHost(
        modifier = Modifier.padding(innerPadding),
        navController = navController,
        startDestination = startTab,
    ) {

    }

}