package dev.orangecast.shared.presentation.viewmodel

import dev.orangecast.shared.domain.model.Podcast
import dev.orangecast.shared.domain.usecase.SearchPodcastsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PodcastListViewModel(
    private val searchPodcastsUseCase: SearchPodcastsUseCase
) {
    private val viewModelScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    private val _uiState = MutableStateFlow(PodcastListUiState())
    val uiState: StateFlow<PodcastListUiState> = _uiState.asStateFlow()

    init {
        loadFeaturedPodcasts()
    }

    fun searchPodcasts(query: String) {
        _uiState.value = _uiState.value.copy(isLoading = true)
        
        viewModelScope.launch {
            searchPodcastsUseCase(query)
                .onSuccess { podcasts ->
                    _uiState.value = _uiState.value.copy(
                        podcasts = podcasts,
                        isLoading = false,
                        error = null
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
        }
    }

    private fun loadFeaturedPodcasts() {
        searchPodcasts("comedy")
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class PodcastListUiState(
    val podcasts: List<Podcast> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)