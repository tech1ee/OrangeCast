package dev.orangepie.base.ui.navigation

import androidx.navigation.NavOptionsBuilder

sealed class NavCommand(val router: Navigator.Router) {

    private var hasBeenHandled = false

    fun getContentIfNotHandled(): NavCommand? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            this
        }
    }

    fun peekContent(): NavCommand = this

    class Navigate(
        router: Navigator.Router,
        val route: String,
        val builder: (NavOptionsBuilder.() -> Unit) = {},
    ) : NavCommand(router) {
        override fun toString(): String {
            return "$router $route"
        }
    }

    class Reset(
        router: Navigator.Router = Navigator.Router.ROOT,
    ) : NavCommand(router)

    class Back(
        router: Navigator.Router = Navigator.Router.ROOT,
        val result: Map<String, Any>? = null,
    ) : NavCommand(router)

    class BackTo(
        val route: String,
        router: Navigator.Router = Navigator.Router.ROOT,
        val result: Map<String, Any>? = null,
    ) : NavCommand(router)

}