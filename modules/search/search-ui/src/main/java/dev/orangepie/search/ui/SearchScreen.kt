package dev.orangepie.search.ui

import android.os.Bundle
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.SavedStateHandle
import com.orangecast.common.R
import dev.orangepie.base.ui.components.GradientText
import dev.orangepie.base.ui.navigation.NavRoute
import dev.orangepie.base.ui.navigation.Routes
import dev.orangepie.base.ui.navigation.TabNavRoutes
import dev.orangepie.base.ui.theme.Color

object SearchScreenRoute: NavRoute {
    override val route: Routes = TabNavRoutes.Search

    @Composable
    override fun Content(savedStateHandle: SavedStateHandle?, arguments: Bundle?) {
        SearchScreen()
    }


}

@Composable
fun SearchScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        GradientText(
            text = stringResource(id = R.string.coming_soon),
            gradient = listOf(
                Color.Yellow,
                Color.Orange
            )
        )
    }
}