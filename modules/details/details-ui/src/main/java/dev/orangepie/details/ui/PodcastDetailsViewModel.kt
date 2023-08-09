package dev.orangepie.details.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.orangepie.base.ui.BaseViewModel
import dev.orangepie.base.ui.navigation.NavCommand
import dev.orangepie.details.domain.usecase.GetPodcastDetailsUseCase
import dev.orangepie.details.ui.mapper.PodcastDetailsUIMapper
import dev.orangepie.details.ui.model.PodcastDetailsViewModelState
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
    private val mapper: PodcastDetailsUIMapper,
) : BaseViewModel() {

    private val podcastItunesId: Long = requireNotNull(PodcastDetailsScreenRoute.getITunesId(savedStateHandle))

    private val viewModelState = MutableStateFlow(PodcastDetailsViewModelState())
    val uiState = viewModelState
        .map(mapper::toUIState)
        .stateInViewModel(mapper.toUIStateBlocking(viewModelState.value))

    init {
        getPodcastDetails()
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