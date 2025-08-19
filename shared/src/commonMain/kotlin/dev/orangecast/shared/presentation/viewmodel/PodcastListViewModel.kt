package dev.orangecast.shared.presentation.viewmodel

import dev.orangecast.shared.domain.model.Podcast
import dev.orangecast.shared.domain.repository.PodcastRepository
import dev.orangecast.shared.domain.usecase.SearchPodcastsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class PodcastListViewModel(
    private val searchPodcastsUseCase: SearchPodcastsUseCase,
    private val podcastRepository: PodcastRepository
) {
    private val viewModelScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    private val _uiState = MutableStateFlow(PodcastListUiState())
    val uiState: StateFlow<PodcastListUiState> = _uiState.asStateFlow()
    
    private val _categorySections = MutableStateFlow<Map<String, List<Podcast>>>(emptyMap())
    val categorySections: StateFlow<Map<String, List<Podcast>>> = _categorySections.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private var searchJob: Job? = null

    init {
        loadFeaturedPodcasts()
        loadCategorySections()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        
        if (query.isBlank()) {
            loadFeaturedPodcasts()
            return
        }
        
        searchJob = viewModelScope.launch {
            delay(300) // Debounce 300ms
            searchPodcasts(query)
        }
    }
    
    private fun searchPodcasts(query: String) {
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
        _uiState.value = _uiState.value.copy(isLoading = true)
        
        viewModelScope.launch {
            podcastRepository.getFeaturedPodcasts()
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
    
    private fun loadCategorySections() {
        viewModelScope.launch {
            // Use dynamic genres from API
            val genresResult = podcastRepository.getGenres()
            genresResult.onSuccess { genres ->
                val topGenres = genres.filter { it.parentId == null }.take(4)
                val categoryResults = topGenres.map { genre ->
                    async {
                        genre.name to podcastRepository.getPodcastsByGenre(genre.id)
                    }
                }.awaitAll()
                
                val categorySections = mutableMapOf<String, List<Podcast>>()
                categoryResults.forEach { (genreName, result) ->
                    result.onSuccess { podcasts ->
                        categorySections[genreName] = podcasts.take(8)
                    }
                }
                
                _categorySections.value = categorySections
            }
        }
    }
    
    fun retrySearch() {
        val currentQuery = _searchQuery.value
        if (currentQuery.isBlank()) {
            loadFeaturedPodcasts()
        } else {
            searchPodcasts(currentQuery)
        }
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