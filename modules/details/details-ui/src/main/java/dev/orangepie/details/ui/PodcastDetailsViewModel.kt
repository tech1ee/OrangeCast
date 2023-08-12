package dev.orangepie.details.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.orangepie.base.ui.BaseViewModel
import dev.orangepie.base.ui.navigation.NavCommand
import dev.orangepie.details.domain.usecase.GetPodcastDetailsUseCase
import dev.orangepie.details.ui.mapper.PodcastDetailsUIMapper
import dev.orangepie.details.ui.mapper.PodcastRSSFeedUIMapper
import dev.orangepie.details.ui.model.PodcastDetailsViewModelState
import dev.orangepie.details.ui.model.PodcastRSSFeedItemUIModel
import dev.orangepie.player.domain.PlayerUseCase
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
    private val player: PlayerUseCase,
    private val detailsUIMapper: PodcastDetailsUIMapper,
    private val feedUIMapper: PodcastRSSFeedUIMapper,
) : BaseViewModel() {

    private val podcastItunesId: Long =
        requireNotNull(PodcastDetailsScreenRoute.getITunesId(savedStateHandle))

    private val viewModelState = MutableStateFlow(PodcastDetailsViewModelState())
    val uiState = viewModelState
        .map(detailsUIMapper::toUIState)
        .stateInViewModel(detailsUIMapper.toUIStateBlocking(viewModelState.value))

    init {
        getPodcastDetails()
    }

    fun onPlayClick(item: PodcastRSSFeedItemUIModel) {
        viewModelScope.launch {
            if (item.isPlaying) {
                player.pause()
            } else {
                val model = feedUIMapper.toModel(item)
                player.play(model)
            }
            viewModelState.update {
                it.copy(
                    details = it.details?.copy(
                        feed = it.details.feed.copy(
                            items = it.details.feed.items.map { feedItem ->
                                feedItem.copy(
                                    isPlaying = feedItem.audio == item.audio && !item.isPlaying
                                )
                            }
                        )
                    )
                )
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
                val details = getPodcastDetails.invoke(podcastItunesId)
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
}