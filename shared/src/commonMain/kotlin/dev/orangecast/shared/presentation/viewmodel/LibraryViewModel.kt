package dev.orangecast.shared.presentation.viewmodel

import dev.orangecast.shared.domain.model.Podcast
import dev.orangecast.shared.domain.usecase.GetSubscribedPodcastsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LibraryUiState(
    val subscribedPodcasts: List<Podcast> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class LibraryViewModel(
    private val getSubscribedPodcastsUseCase: GetSubscribedPodcastsUseCase
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()
    
    init {
        loadSubscribedPodcasts()
    }
    
    fun loadSubscribedPodcasts() {
        scope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                getSubscribedPodcastsUseCase.getSubscribedPodcasts()
                    .collect { podcasts ->
                        _uiState.value = _uiState.value.copy(
                            subscribedPodcasts = podcasts,
                            isLoading = false,
                            error = null
                        )
                    }
            } catch (throwable: Throwable) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = throwable.message ?: "Failed to load subscribed podcasts"
                )
            }
        }
    }
    
    fun onRetry() {
        loadSubscribedPodcasts()
    }
}