package com.example.orangecast.ui.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.orangecast.domain.BestPodcastsState
import com.example.orangecast.domain.GetBestPodcasts
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val getBestPodcasts: GetBestPodcasts
) : ViewModel() {

    private val _state = MutableStateFlow(DiscoverViewState())
    val state: StateFlow<DiscoverViewState>
        get() = _state


    init {
        getBestPodcasts()
    }

    private fun getBestPodcasts() {
        viewModelScope.launch(Dispatchers.IO) {
            getBestPodcasts.execute()
                .collect { bestPodcastsState ->
                    when (bestPodcastsState) {
                        is BestPodcastsState.Loading -> _state.emit(DiscoverViewState(bestPodcastsLoading = true))
                        is BestPodcastsState.Data -> _state.emit(
                            DiscoverViewState(
                                bestPodcasts = bestPodcastsState.data
                            )
                        )
                    }
                }
        }
    }
}