package dev.orangecast.shared.presentation.viewmodel

import dev.orangecast.shared.domain.model.PodcastEpisode
import dev.orangecast.shared.domain.usecase.GetNewEpisodesUseCase
import dev.orangecast.shared.domain.repository.PodcastRepository
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
    val isRefreshing: Boolean = false,
    val error: String? = null
)

class NewEpisodesViewModel(
    private val getNewEpisodesUseCase: GetNewEpisodesUseCase,
    private val podcastRepository: PodcastRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    private val _uiState = MutableStateFlow(NewEpisodesUiState())
    val uiState: StateFlow<NewEpisodesUiState> = _uiState.asStateFlow()
    
    init {
        loadNewEpisodes()
        syncEpisodesOnStartup()
    }
    
    fun loadNewEpisodes() {
        scope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                getNewEpisodesUseCase.getNewEpisodes()
                    .collect { episodes ->
                        _uiState.value = _uiState.value.copy(
                            episodes = episodes,
                            isLoading = false,
                            error = null
                        )
                    }
            } catch (throwable: Throwable) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = throwable.message ?: "Failed to load episodes"
                )
            }
        }
    }
    
    private fun syncEpisodesOnStartup() {
        scope.launch {
            try {
                podcastRepository.syncEpisodesForSubscribedPodcasts()
            } catch (throwable: Throwable) {
                // Silent failure for background sync
            }
        }
    }
    
    fun refreshEpisodes() {
        scope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true, error = null)
            
            try {
                val syncResult = podcastRepository.syncEpisodesForSubscribedPodcasts()
                if (syncResult.isFailure) {
                    _uiState.value = _uiState.value.copy(
                        isRefreshing = false,
                        error = syncResult.exceptionOrNull()?.message ?: "Sync failed"
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isRefreshing = false,
                        error = null
                    )
                }
            } catch (throwable: Throwable) {
                _uiState.value = _uiState.value.copy(
                    isRefreshing = false,
                    error = throwable.message ?: "Failed to refresh episodes"
                )
            }
        }
    }
    
    fun onRetry() {
        loadNewEpisodes()
    }
}