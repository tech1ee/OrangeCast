package com.example.orangecast.ui.discover

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.orangecast.domain.BestPodcastsState
import com.example.orangecast.domain.GetBestPodcasts
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DiscoverViewModel @Inject constructor(
    private val getBestPodcasts: GetBestPodcasts
) : ViewModel() {

    private val _state = MutableStateFlow(DiscoverViewState())
    val state: StateFlow<DiscoverViewState>
        get() = _state


    init {
        viewModelScope.launch {
            val bestPodcastsState = getBestPodcasts.execute()
                .collectAsState(BestPodcastsState.None, viewModelScope.coroutineContext)
            when (bestPodcastsState.value) {
                is BestPodcastsState.Loading -> _state.emit(DiscoverViewState(bestPodcastsLoading = true))
                is BestPodcastsState.Data -> _state.emit(
                    DiscoverViewState(
                        bestPodcasts =
                        (bestPodcastsState.value as BestPodcastsState.Data).data
                    )
                )
            }

        }
    }
}