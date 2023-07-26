package dev.orangepie.base.ui.navigation

interface Routes {
    fun getRoute(): String
}

class NavRoutes {
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