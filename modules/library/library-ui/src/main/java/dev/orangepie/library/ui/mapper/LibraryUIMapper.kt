package dev.orangepie.library.ui.mapper

import dev.orangepie.library.ui.LibraryUIState
import dev.orangepie.library.ui.LibraryViewModelState
import dev.orangepie.podcasts.ui.PodcastsUIMapper
import kotlinx.collections.immutable.toPersistentList
import javax.inject.Inject

class LibraryUIMapper @Inject constructor(
    private val podcastsUIMapper: PodcastsUIMapper,
) {

    suspend fun toUIState(state: LibraryViewModelState): LibraryUIState {
        return toUIStateBlocking(state)
    }

    fun toUIStateBlocking(state: LibraryViewModelState): LibraryUIState {
        return when {
            state.isLoading -> LibraryUIState.Loading
            state.isError -> LibraryUIState.Error
            state.podcasts.isEmpty() -> LibraryUIState.Empty
            else -> LibraryUIState.Podcasts(state.podcasts
                .map { podcastsUIMapper.toUIModel(it) }.toPersistentList()
            )
        }
    }
}