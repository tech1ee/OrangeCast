package dev.orangepie.base.ui.navigation

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import timber.log.Timber

object Navigator {
    private val _rootRouterFlow = MutableSharedFlow<NavCommand>(extraBufferCapacity = 1, replay = 1)
    val rootRouterFlow = _rootRouterFlow.asSharedFlow()

    private val _childRouterFlow = MutableSharedFlow<NavCommand>(extraBufferCapacity = 1, replay = 1)
    val childRouterFlow = _childRouterFlow.asSharedFlow()

    fun navigateTo(command: NavCommand) {
        val commandSent = when (command.router) {
            Router.ROOT -> _rootRouterFlow.tryEmit(command)
            Router.CHILD -> _childRouterFlow.tryEmit(command)
        }
        Timber.d("Navigator:navigateTo($command)=$commandSent")
    }

    enum class Router {
        ROOT,
        CHILD
    }
}