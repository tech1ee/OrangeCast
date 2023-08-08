package dev.orangepie.base.ui.navigation

import android.os.Bundle
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

interface NavRoute {
    val route: Routes

    fun getArguments(): List<NamedNavArgument> = listOf()

    @Composable
    fun Content(
        savedStateHandle: SavedStateHandle?,
        arguments: Bundle?,
    )

    fun composable(
        builder: NavGraphBuilder,
        navHostController: NavHostController,
        enterTransition: (@JvmSuppressWildcards AnimatedContentTransitionScope<NavBackStackEntry>.()  -> EnterTransition?)? = null,
        exitTransition: (@JvmSuppressWildcards AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? = null,
    ) {
        builder.composable(
            route = route.getRoute(),
            arguments = getArguments(),
            enterTransition = enterTransition,
            exitTransition = exitTransition,
            content = {
                Content(navHostController.currentBackStackEntry?.savedStateHandle, it.arguments)
            }
        )
    }
}