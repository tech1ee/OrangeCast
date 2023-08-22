package dev.orangepie.library.ui

import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import com.orangecast.common.R
import dev.orangepie.base.ui.components.GradientDivider
import dev.orangepie.base.ui.components.GradientScreenTitle
import dev.orangepie.base.ui.components.GradientText
import dev.orangepie.base.ui.components.LoaderCircle
import dev.orangepie.base.ui.navigation.NavRoute
import dev.orangepie.base.ui.navigation.TabNavRoutes
import dev.orangepie.base.ui.theme.Color
import dev.orangepie.base.ui.theme.TextStyle
import dev.orangepie.library.ui.components.LibraryEmptyScreen
import dev.orangepie.library.ui.components.LibraryItem

object LibraryScreenRoute : NavRoute {
    override val route = TabNavRoutes.Library

    @Composable
    override fun Content(savedStateHandle: SavedStateHandle?, arguments: Bundle?) {
        LibraryScreen()
    }
}

@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel = hiltViewModel()
) {

    val uiState = viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {

        GradientScreenTitle(
            title = stringResource(id = R.string.library_title),
        )

        when (val state = uiState.value) {

            is LibraryUIState.Loading -> LoaderCircle()

            is LibraryUIState.Empty -> LibraryEmptyScreen()

            is LibraryUIState.Podcasts -> {
                LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxSize(),
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(16.dp),
                ) {
                    items(state.podcasts) { podcast ->
                        LibraryItem(
                            model = podcast,
                            onClick = { viewModel.onPodcastClick(podcast.itunesId) }
                        )
                    }
                }
            }

            else -> Unit
        }
    }
}

