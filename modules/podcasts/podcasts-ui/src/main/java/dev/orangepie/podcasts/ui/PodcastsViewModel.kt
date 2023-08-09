package dev.orangepie.podcasts.ui

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.orangepie.base.ui.BaseViewModel
import dev.orangepie.base.ui.navigation.NavCommand
import dev.orangepie.base.ui.navigation.NavRoutes
import dev.orangepie.base.ui.navigation.Navigator
import dev.orangepie.details.ui.PodcastDetailsScreenRoute
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

    fun onPodcastClick(iTunesId: Long){
        navigate(
            NavCommand.Navigate(
                router = Navigator.Router.ROOT,
                route = NavRoutes.PodcastDetails.getNavGraphRoute(iTunesId = iTunesId)
            )
        )
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