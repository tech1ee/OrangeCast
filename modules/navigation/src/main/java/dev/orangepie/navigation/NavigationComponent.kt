package dev.orangepie.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import dev.orangepie.base.ui.navigation.NavCommand
import dev.orangepie.base.ui.navigation.NavRoutes
import dev.orangepie.base.ui.navigation.Navigator
import dev.orangepie.base.ui.navigation.TabNavRoutes
import dev.orangepie.discover.ui.DiscoverScreenRoute
import dev.orangepie.main.MainScreenRoute
import dev.orangepie.podcasts.ui.PodcastsScreenRoute
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
fun NavigationComponent(paddingValues: PaddingValues) {
    val navController: NavHostController = rememberNavController()

    LaunchedEffect("root_navigation") {
        Navigator.rootRouterFlow
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
                            saveState = false
                        )
                    }

                    is NavCommand.Navigate -> navController.navigate(
                        navCommand.route,
                        navCommand.builder
                    )

                    is NavCommand.Reset -> navController.navigate(
                        NavRoutes.Main.getNavGraphRoute(TabNavRoutes.Discover)
                    ) {
                        popUpTo(NavRoutes.Main.getRoute()) { inclusive = true }
                        launchSingleTop = true
                        restoreState = true
                    }

                    null -> Unit
                }
            }
            .launchIn(this)
    }
    NavHost(
        navController = navController,
        startDestination = NavRoutes.Main.getRoute(),
        modifier = Modifier.padding(paddingValues)
    ) {
        MainScreenRoute.composable(this, navController)
        DiscoverScreenRoute.composable(this, navController)
        PodcastsScreenRoute.composable(this, navController)
    }
}