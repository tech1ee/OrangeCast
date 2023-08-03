package dev.orangepie.podcasts.ui

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.orangepie.base.ui.BaseViewModel
import dev.orangepie.podcasts.domain.usecase.GetBestPodcastsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PodcastsViewModel @Inject constructor(
    private val getBestPodcasts: GetBestPodcastsUseCase,
    private val podcastsUIMapper: PodcastsUIMapper,
) : BaseViewModel() {

    private val viewModelState = MutableStateFlow(PodcastsViewModelState())
    val uiState = viewModelState
        .map(podcastsUIMapper::toUIState)
        .stateInViewModel(podcastsUIMapper.toUIStateBlocking(viewModelState.value))

    init {
        getBestPodcasts()
    }

    private fun getBestPodcasts() {
        viewModelScope.launch {
            viewModelState.update {
                it.copy(
                    podcastsLoading = true,
                    podcastsError = false,
                    podcasts = null
                )
            }
            try {
                val podcasts = getBestPodcasts.invoke()
                viewModelState.update {
                    it.copy(
                        podcastsLoading = false,
                        podcasts = podcasts
                    )
                }
            } catch (e: Exception) {
                viewModelState.update {
                    it.copy(
                        podcastsLoading = false,
                        podcastsError = true
                    )
                }
            }
        }
    }
}