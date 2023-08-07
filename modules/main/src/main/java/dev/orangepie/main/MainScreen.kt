package dev.orangepie.main

import android.os.Bundle
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dev.orangepie.base.ui.navigation.NavRoute
import dev.orangepie.base.ui.navigation.NavRoutes
import dev.orangepie.main.model.NavBarScreen
import dev.orangepie.splash.ui.SplashScreen

object MainScreenRoute : NavRoute {
    override val route = NavRoutes.Main

    @Composable
    override fun Content(savedStateHandle: SavedStateHandle?, arguments: Bundle?) {
        val viewModel: MainViewModel = hiltViewModel()
        val navController = rememberNavController()
        MainScreen(
            navController = navController,
            onBottomNavClick = viewModel::onBottomNavClick,
        )
    }

    override fun getArguments() = listOf(
        navArgument(NavRoutes.Main.KEY_TAB) { type = NavType.StringType }
    )
}

@Composable
fun MainScreen(
    navController: NavHostController,
    onBottomNavClick: (NavBarScreen) -> Unit,
) {
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        bottomBar = { BottomBar(navController, onBottomNavClick) },
        scaffoldState = scaffoldState,
        content = { innerPadding ->
            BottomNav(
                navController = navController,
                innerPadding = innerPadding,
                startTab = NavBarScreen.Discover.route.getRoute()
            )
        }
    )
    SplashScreen()
}