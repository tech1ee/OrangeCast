package dev.orangepie.details.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.orangepie.base.ui.BaseViewModel
import dev.orangepie.base.ui.navigation.NavCommand
import dev.orangepie.details.domain.model.PodcastRSSFeedItemState
import dev.orangepie.details.domain.usecase.GetPodcastDetailsUseCase
import dev.orangepie.details.ui.mapper.PodcastDetailsUIMapper
import dev.orangepie.details.ui.mapper.PodcastRSSFeedUIMapper
import dev.orangepie.details.ui.model.PodcastDetailsViewModelState
import dev.orangepie.details.ui.model.PodcastRSSFeedItemUIModel
import dev.orangepie.details.ui.model.PodcastRSSFeedItemUIState
import dev.orangepie.library.domain.usecase.DeletePodcastUseCase
import dev.orangepie.library.domain.usecase.GetPodcastFromLibraryUseCase
import dev.orangepie.library.domain.usecase.SavePodcastUseCase
// TODO: Restore player imports after fixing DI
// import dev.orangepie.player.domain.PlayerEvent
// import dev.orangepie.player.domain.PlayerUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PodcastDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getPodcastDetails: GetPodcastDetailsUseCase,
    // TODO: Restore player injection after fixing DI
    // private val player: PlayerUseCase,
    private val detailsUIMapper: PodcastDetailsUIMapper,
    private val feedUIMapper: PodcastRSSFeedUIMapper,
    private val getPodcastFromLibrary: GetPodcastFromLibraryUseCase,
    private val savePodcast: SavePodcastUseCase,
    private val deletePodcast: DeletePodcastUseCase,
) : BaseViewModel() {

    private val podcastItunesId: Long =
        requireNotNull(PodcastDetailsScreenRoute.getITunesId(savedStateHandle))

    private val viewModelState = MutableStateFlow(PodcastDetailsViewModelState())
    val uiState = viewModelState
        .map(detailsUIMapper::toUIState)
        .stateInViewModel(detailsUIMapper.toUIStateBlocking(viewModelState.value))

    init {
        getPodcastDetails()
        // TODO: Restore after fixing DI
        // collectPlayerEvents()
    }

    fun onPlayClick(item: PodcastRSSFeedItemUIModel) {
        // TODO: Restore player functionality after fixing DI
        Timber.d("Play button clicked for: ${item.title}")
        // viewModelScope.launch {
        //     if (item.state is PodcastRSSFeedItemUIState.Playing) {
        //         player.pause()
        //     } else {
        //         val model = feedUIMapper.toModel(item)
        //         player.play(model)
        //     }
        // }
    }

    fun onSubscribeClick() {
        viewModelScope.launch {
            try {
                val details = viewModelState.value.details
                if (details != null) {
                    if (details.subscribed) {
                        deletePodcast(details)
                    } else {
                        savePodcast(details)
                    }
                }
                viewModelState.update {
                    it.copy(
                        details = it.details?.copy(
                            subscribed = !it.details.subscribed
                        )
                    )
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    fun onBackClick() {
        navigate(NavCommand.Back())
    }

    private fun getPodcastDetails() {
        viewModelScope.launch {
            viewModelState.update {
                it.copy(
                    loading = true,
                    error = false,
                    details = null
                )
            }
            try {
                val libraryPodcast = getPodcastFromLibrary.invoke(podcastItunesId)
                val details = getPodcastDetails.invoke(podcastItunesId).copy(
                    subscribed = libraryPodcast != null
                )
                viewModelState.update {
                    it.copy(
                        loading = false,
                        details = details
                    )
                }
            } catch (e: Exception) {
                Timber.e(e)
                viewModelState.update {
                    it.copy(
                        loading = false,
                        error = true
                    )
                }
            }
        }
    }

    // TODO: Restore after fixing DI
    // private fun collectPlayerEvents() {
    //     viewModelScope.launch {
    //         player.playerEvents.collect { event ->
    //             when (event) {
    //                 is PlayerEvent.Loading -> updatePodcastItemsState(
    //                     event = event,
    //                     state = PodcastRSSFeedItemState.Loading
    //                 )
    //                 is PlayerEvent.Playing -> updatePodcastItemsState(
    //                     event = event,
    //                     state = PodcastRSSFeedItemState.Playing
    //                 )
    //                 is PlayerEvent.Paused -> updatePodcastItemsState(
    //                     event = event,
    //                     state = PodcastRSSFeedItemState.Paused
    //                 )
    //             }
    //         }
    //     }
    // }

    // TODO: Restore after fixing DI
    // private fun updatePodcastItemsState(event: PlayerEvent, state: PodcastRSSFeedItemState) {
    //     viewModelState.update {
    //         it.copy(
    //             details = it.details?.copy(
    //                 feed = it.details.feed.copy(
    //                     items = it.details.feed.items.map { feedItem ->
    //                         if (feedItem.audio == event.podcast?.audio) {
    //                             feedItem.copy(
    //                                 state = state
    //                             )
    //                         } else {
    //                             feedItem.copy(
    //                                 state = PodcastRSSFeedItemState.None
    //                             )
    //                         }
    //                     }
    //                 )
    //             )
    //         )
    //     }
    // }
}