package dev.orangepie.main.model

import dev.orangepie.base.ui.navigation.TabNavRoutes

sealed class NavBarScreen(
    val route: TabNavRoutes,
) {
    object Discover : NavBarScreen(TabNavRoutes.Discover)
    object Search : NavBarScreen(TabNavRoutes.Search)
    object Library : NavBarScreen(TabNavRoutes.Library)
}
