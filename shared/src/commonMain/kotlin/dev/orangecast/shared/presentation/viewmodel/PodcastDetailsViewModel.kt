package dev.orangecast.shared.presentation.viewmodel

import dev.orangecast.shared.domain.error.toPodcastError
import dev.orangecast.shared.domain.model.PodcastDetails
import dev.orangecast.shared.domain.model.PodcastEpisode
import dev.orangecast.shared.domain.model.PodcastState
import dev.orangecast.shared.domain.usecase.GetPodcastDetailsUseCase
import dev.orangecast.shared.domain.usecase.PlayEpisodeUseCase
import dev.orangecast.shared.domain.usecase.SubscriptionUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class PodcastDetailsViewModel(
    private val getPodcastDetailsUseCase: GetPodcastDetailsUseCase,
    private val playEpisodeUseCase: PlayEpisodeUseCase,
    private val subscriptionUseCase: SubscriptionUseCase,
    private val coroutineScope: CoroutineScope
) {
    
    private val _detailsState = MutableStateFlow<PodcastState<PodcastDetails>>(PodcastState.Loading)
    val detailsState: StateFlow<PodcastState<PodcastDetails>> = _detailsState.asStateFlow()
    
    private val _isSubscribed = MutableStateFlow(false)
    val isSubscribed: StateFlow<Boolean> = _isSubscribed.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private var currentPodcastId: String? = null
    
    fun loadPodcastDetails(podcastId: String) {
        if (currentPodcastId == podcastId && _detailsState.value is PodcastState.Success) {
            return
        }
        
        currentPodcastId = podcastId
        
        coroutineScope.launch {
            try {
                _detailsState.value = PodcastState.Loading
                val result = getPodcastDetailsUseCase(podcastId)
                
                if (result.isSuccess) {
                    val details = result.getOrThrow()
                    _detailsState.value = PodcastState.Success(details)
                    _isSubscribed.value = details.isSubscribed
                } else {
                    _detailsState.value = PodcastState.Error(
                        result.exceptionOrNull()?.toPodcastError() 
                            ?: Throwable("Unknown error").toPodcastError()
                    )
                }
            } catch (e: Exception) {
                _detailsState.value = PodcastState.Error(e.toPodcastError())
            }
        }
    }
    
    fun playEpisode(episode: PodcastEpisode) {
        coroutineScope.launch {
            try {
                val result = playEpisodeUseCase.execute(episode)
                if (result.isFailure) {
                    val error = result.exceptionOrNull()?.message ?: "Failed to play episode"
                }
            } catch (e: Exception) {
            }
        }
    }
    
    fun playLatestEpisode() {
        val currentState = _detailsState.value
        if (currentState is PodcastState.Success && currentState.data.episodes.isNotEmpty()) {
            playEpisode(currentState.data.episodes.first())
        }
    }
    
    fun toggleSubscription() {
        val currentState = _detailsState.value
        if (currentState is PodcastState.Success) {
            val podcast = currentState.data.podcast
            
            coroutineScope.launch {
                try {
                    _isLoading.value = true
                    
                    if (_isSubscribed.value) {
                        val result = subscriptionUseCase.unsubscribe(podcast.id)
                        if (result.isSuccess) {
                            _isSubscribed.value = false
                        }
                    } else {
                        val result = subscriptionUseCase.subscribe(podcast)
                        if (result.isSuccess) {
                            _isSubscribed.value = true
                        }
                    }
                } catch (e: Exception) {
                    
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }
    
    fun refresh() {
        currentPodcastId?.let { podcastId ->
            loadPodcastDetails(podcastId)
        }
    }
}