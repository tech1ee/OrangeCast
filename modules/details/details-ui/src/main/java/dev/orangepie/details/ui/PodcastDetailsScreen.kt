package dev.orangepie.details.ui

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import dev.orangepie.base.ui.components.LoaderCircle
import dev.orangepie.base.ui.navigation.NavRoute
import dev.orangepie.base.ui.navigation.NavRoutes
import dev.orangepie.details.ui.component.PodcastDetailsScreenContent
import dev.orangepie.details.ui.model.PodcastDetailsUIState

object PodcastDetailsScreenRoute : NavRoute {
    override val route = NavRoutes.PodcastDetails

    fun getITunesId(savedStateHandle: SavedStateHandle) =
        savedStateHandle.get<Long>(NavRoutes.PodcastDetails.KEY_PODCAST_ITUNES_ID)

    @Composable
    override fun Content(savedStateHandle: SavedStateHandle?, arguments: Bundle?) {
        PodcastDetailsScreen()
    }

    override fun getArguments(): List<NamedNavArgument> {
        return listOf(
            navArgument(NavRoutes.PodcastDetails.KEY_PODCAST_ITUNES_ID) { type = NavType.LongType }
        )
    }
}

@Composable
fun PodcastDetailsScreen(
    viewModel: PodcastDetailsViewModel = hiltViewModel()
) {

    val uiState = viewModel.uiState.collectAsState()

    when (val state = uiState.value) {
        is PodcastDetailsUIState.Details -> PodcastDetailsScreenContent(
            details = state.details,
            episodes = state.details.feed.items,
            onBackClick = viewModel::onBackClick
        )
        is PodcastDetailsUIState.Loading -> LoaderCircle()
        else -> Unit
    }

}