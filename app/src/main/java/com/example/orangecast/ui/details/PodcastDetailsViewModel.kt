package com.example.orangecast.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.orangecast.domain.details.GetPodcastDetails
import com.example.orangecast.domain.details.PodcastDetailsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class PodcastDetailsViewModel @Inject constructor(
    private val getPodcastDetails: GetPodcastDetails
) : ViewModel() {

    private val _state = MutableStateFlow(PodcastDetailsViewState())
    val state: StateFlow<PodcastDetailsViewState>
        get() = _state

    init {

    }

    private fun getPodcastDetails(channelId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getPodcastDetails.execute(channelId)
                .collect { state ->
                    when (state) {
                        is PodcastDetailsState.Loading -> _state.emit(PodcastDetailsViewState(loading = true))
                        is PodcastDetailsState.Data -> _state.emit(PodcastDetailsViewState(
                            data = state.data
                        ))
                    }
                }
        }
    }

}