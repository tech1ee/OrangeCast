package dev.orangepie.base.ui.navigation

interface Routes {
    fun getRoute(): String
}

sealed class NavRoutes: Routes {

    object Main : NavRoutes() {
        const val KEY_TAB = "KEY_TAB"

        override fun getRoute() = "main/{$KEY_TAB}"

        fun getNavGraphRoute(value: TabNavRoutes) =
            getRoute().replace("{$KEY_TAB}", value.getRoute())
    }

    object Podcasts : NavRoutes() {
        override fun getRoute() = "podcasts"
    }
}

sealed class TabNavRoutes : Routes {
    object Discover : TabNavRoutes() {
        override fun getRoute() = "discover"
    }
    object Search : TabNavRoutes() {
        override fun getRoute() = "search"
    }
    object Library : TabNavRoutes() {
        override fun getRoute() = "library"
    }
}