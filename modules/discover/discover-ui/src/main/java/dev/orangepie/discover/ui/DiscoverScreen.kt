package dev.orangepie.discover.ui

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import dev.orangepie.base.ui.navigation.NavRoute
import dev.orangepie.base.ui.navigation.TabNavRoutes
import dev.orangepie.podcasts.ui.PodcastsScreen

object DiscoverScreenRoute : NavRoute {
    override val route = TabNavRoutes.Discover

    @Composable
    override fun Content(savedStateHandle: SavedStateHandle?, arguments: Bundle?) {
        DiscoverScreen()
    }
}

@Composable
fun DiscoverScreen() {
    PodcastsScreen()
}