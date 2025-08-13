package dev.orangecast.shared.presentation.viewmodel

import dev.orangecast.shared.domain.model.PodcastEpisode
import dev.orangecast.shared.domain.usecase.GetNewEpisodesUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class NewEpisodesUiState(
    val episodes: List<PodcastEpisode> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class NewEpisodesViewModel(
    private val getNewEpisodesUseCase: GetNewEpisodesUseCase
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    private val _uiState = MutableStateFlow(NewEpisodesUiState())
    val uiState: StateFlow<NewEpisodesUiState> = _uiState.asStateFlow()
    
    init {
        loadNewEpisodes()
    }
    
    fun loadNewEpisodes() {
        scope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            getNewEpisodesUseCase()
                .collect { result ->
                    result.fold(
                        onSuccess = { episodes ->
                            _uiState.value = _uiState.value.copy(
                                episodes = episodes,
                                isLoading = false,
                                error = null
                            )
                        },
                        onFailure = { throwable ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = throwable.message ?: "Failed to load episodes"
                            )
                        }
                    )
                }
        }
    }
    
    fun onRetry() {
        loadNewEpisodes()
    }
}