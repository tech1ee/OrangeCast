package dev.orangepie.podcasts.ui

import android.os.Bundle
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import dev.orangepie.base.ui.navigation.NavRoute
import dev.orangepie.base.ui.navigation.NavRoutes
import dev.orangepie.podcasts.ui.components.PodcastChannelListItem
import dev.orangepie.podcasts.ui.model.PodcastChannelUIModel
import kotlinx.collections.immutable.ImmutableList

object PodcastsScreenRoute : NavRoute {
    override val route = NavRoutes.Podcasts

    @Composable
    override fun Content(savedStateHandle: SavedStateHandle?, arguments: Bundle?) {
        PodcastsScreen()
    }
}

@Composable
fun PodcastsScreen(
    viewModel: PodcastsViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()

    when (val state = uiState.value) {
        is PodcastsUIState.Success -> PodcastsList(state.channels)
        else -> Unit
    }
}

@Composable
private fun PodcastsList(
    podcasts: ImmutableList<PodcastChannelUIModel>
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(podcasts) { podcast ->
            PodcastChannelListItem(
                model = podcast,
                onClick = { /* TODO */ }
            )
        }
    }
}