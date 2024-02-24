package dev.orangepie.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import com.seamm_team.main.ui.R
import dev.orangepie.base.ui.navigation.NavCommand
import dev.orangepie.base.ui.navigation.Navigator
import dev.orangepie.base.ui.theme.Color
import dev.orangepie.discover.ui.DiscoverScreenRoute
import dev.orangepie.library.ui.LibraryScreenRoute
import dev.orangepie.main.model.NavBarScreen
import dev.orangepie.search.ui.SearchScreenRoute
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
fun BottomNav(
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
                            NavBarScreen.Library.route.getRoute(),
                            -> {
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
        DiscoverScreenRoute.composable(this, navController)
        SearchScreenRoute.composable(this, navController)
        LibraryScreenRoute.composable(this, navController)
    }
}

@Composable
fun BottomBar(
    navController: NavController,
    onClick: (NavBarScreen) -> Unit,
) {
    Column {
        BottomNavigation(
            backgroundColor = Color.Black,
            contentColor = Color.Grey,
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            listOf(
                NavBarScreen.Discover,
                NavBarScreen.Search,
                NavBarScreen.Library,
            ).forEach { screen ->
                val selected = currentDestination?.hierarchy
                    ?.any { it.route == screen.route.getRoute() } == true

                BottomNavigationItem(
                    icon = {
                        when (screen) {
                            is NavBarScreen.Discover -> Icon(
                                painter = painterResource(id = R.drawable.ic_discover),
                                contentDescription = "Discover",
                            )
                            is NavBarScreen.Search -> Icon(
                                painter = painterResource(id = R.drawable.ic_search),
                                contentDescription = "Search",
                            )
                            is NavBarScreen.Library -> Icon(
                                painter = painterResource(id = R.drawable.ic_library),
                                contentDescription = "Library",
                            )
                        }
                    },
                    label = {
                        Text(
                            modifier = Modifier.padding(top = 2.dp),
                            text = when (screen) {
                                is NavBarScreen.Discover -> stringResource(id = R.string.bottom_bar_discover)
                                is NavBarScreen.Search -> stringResource(id = R.string.bottom_bar_search)
                                is NavBarScreen.Library -> stringResource(id = R.string.bottom_bar_library)
                            }
                        )
                    },
                    selected = selected,
                    selectedContentColor = Color.Orange,
                    unselectedContentColor = Color.White,
                    onClick = { onClick(screen) },
                )
            }
        }
    }
}