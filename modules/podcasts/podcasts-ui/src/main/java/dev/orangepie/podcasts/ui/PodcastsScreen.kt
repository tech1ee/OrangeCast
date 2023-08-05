package dev.orangepie.podcasts.ui

import android.os.Bundle
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import dev.orangepie.base.ui.components.GradientDivider
import dev.orangepie.base.ui.navigation.NavRoute
import dev.orangepie.base.ui.navigation.NavRoutes
import dev.orangepie.genres.ui.components.PodcastGenreTitle
import dev.orangepie.genres.ui.model.PodcastGenreUiModel
import dev.orangepie.podcasts.ui.components.PodcastChannelListItem
import dev.orangepie.podcasts.ui.model.PodcastChannelUIModel
import dev.orangepie.podcasts.ui.model.PodcastsByGenreUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import okhttp3.internal.immutableListOf

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
        is PodcastsUIState.PodcastsByGenre -> PodcastsList(state.podcasts)
        else -> Unit
    }
}

@Composable
private fun PodcastsList(
    podcasts: ImmutableList<PodcastsByGenreUiModel>
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(podcasts) { genre ->
            PodcastsByGenreList(
                genre = genre.genre,
                podcasts = genre.podcasts
            )
        }
    }
}

@Composable
private fun PodcastsByGenreList(
    genre: PodcastGenreUiModel,
    podcasts: ImmutableList<PodcastChannelUIModel>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        PodcastGenreTitle(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .fillMaxWidth(),
            title = genre.name
        )
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            items(podcasts) { podcast ->
                PodcastChannelListItem(
                    model = podcast,
                    onClick = { /* TODO */ }
                )
            }
        }
        GradientDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )
    }
}

@Preview
@Composable
private fun PodcastsByGenreListPreview() {
    PodcastsByGenreList(
        genre = PodcastGenreUiModel(
            id = 1,
            name = "Comedy",
            parentId = null
        ),
        podcasts = immutableListOf(
            PodcastChannelUIModel(
                title = "The Joe Rogan Experience",
                imagePreviewUrl = "https://is1-ssl.mzstatic.com/image/thumb/Podcasts124/v4/4a/0e/1a/4a0e1a4a-0e1a-4b1e-8e1e-3b8b8b8b8b8b/mza_11780547688680588606.jpg/600x600bb.jpg",
                imageFullUrl = null,
                itunesId = 360084272,
                genres = immutableListOf(
                    PodcastGenreUiModel(
                        id = 1,
                        name = "Comedy",
                        parentId = null
                    )
                )
            ),
        ).toPersistentList()
    )
}