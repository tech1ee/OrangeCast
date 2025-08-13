package dev.orangecast.shared.presentation.viewmodel

import dev.orangecast.shared.domain.model.PodcastDetails
import dev.orangecast.shared.domain.usecase.GetPodcastDetailsUseCase
import dev.orangecast.shared.domain.usecase.SubscribeToPodcastUseCase
import dev.orangecast.shared.domain.usecase.UnsubscribeFromPodcastUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PodcastDetailViewModel(
    private val getPodcastDetailsUseCase: GetPodcastDetailsUseCase,
    private val subscribeToPodcastUseCase: SubscribeToPodcastUseCase,
    private val unsubscribeFromPodcastUseCase: UnsubscribeFromPodcastUseCase
) {
    private val viewModelScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    private val _uiState = MutableStateFlow(PodcastDetailUiState())
    val uiState: StateFlow<PodcastDetailUiState> = _uiState.asStateFlow()
    
    fun loadPodcastDetails(podcastId: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        
        viewModelScope.launch {
            getPodcastDetailsUseCase(podcastId)
                .onSuccess { podcastDetails ->
                    _uiState.value = _uiState.value.copy(
                        podcastDetails = podcastDetails,
                        isLoading = false,
                        error = null
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Unknown error occurred"
                    )
                }
        }
    }
    
    fun toggleSubscription() {
        val currentDetails = _uiState.value.podcastDetails ?: return
        
        viewModelScope.launch {
            val result = if (currentDetails.isSubscribed) {
                unsubscribeFromPodcastUseCase(currentDetails.podcast.id)
            } else {
                subscribeToPodcastUseCase(currentDetails.podcast)
            }
            
            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    podcastDetails = currentDetails.copy(
                        isSubscribed = !currentDetails.isSubscribed
                    )
                )
            }.onFailure { error ->
                // Could show a toast or error message for subscription failure
                // For now, we'll just ignore the error
            }
        }
    }
}

data class PodcastDetailUiState(
    val podcastDetails: PodcastDetails? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)