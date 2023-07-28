package dev.orangepie.discover.ui

import android.os.Bundle
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.SavedStateHandle
import dev.orangepie.base.ui.navigation.NavRoute
import dev.orangepie.base.ui.navigation.TabNavRoutes

object DiscoverScreenRoute : NavRoute {
    override val route = TabNavRoutes.Discover

    @Composable
    override fun Content(savedStateHandle: SavedStateHandle?, arguments: Bundle?) {
        DiscoverScreen()
    }
}

@Composable
fun DiscoverScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(text = "Discover")
    }
}