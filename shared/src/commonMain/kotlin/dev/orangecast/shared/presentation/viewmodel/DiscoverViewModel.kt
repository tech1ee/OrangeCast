package dev.orangecast.shared.presentation.viewmodel

import dev.orangecast.shared.domain.model.Podcast
import dev.orangecast.shared.domain.model.Genre
import dev.orangecast.shared.domain.model.GenreSection
import dev.orangecast.shared.domain.repository.PodcastRepository
import dev.orangecast.shared.domain.result.ApiResult
import dev.orangecast.shared.domain.usecase.SearchPodcastsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import io.github.aakira.napier.Napier

data class DiscoverUiState(
    val isLoading: Boolean = false,
    val featuredPodcasts: List<Podcast> = emptyList(),
    val genreSections: List<GenreSection> = emptyList(),
    val availableGenres: List<Genre> = emptyList(),
    val errorMessage: String? = null,
    val selectedContentType: ContentType = ContentType.POPULAR
)

enum class ContentType {
    POPULAR, RECOMMENDATIONS, NEW
}

class DiscoverViewModel(
    private val podcastRepository: PodcastRepository,
    private val searchPodcastsUseCase: SearchPodcastsUseCase
) {
    private val _uiState = MutableStateFlow(DiscoverUiState())
    val uiState: StateFlow<DiscoverUiState> = _uiState.asStateFlow()
    
    private val viewModelScope = CoroutineScope(Dispatchers.Main)

    init {
        loadPopularContent()
    }

    fun selectContentType(contentType: ContentType) {
        _uiState.value = _uiState.value.copy(selectedContentType = contentType)
        when (contentType) {
            ContentType.POPULAR -> loadPopularContent()
            ContentType.RECOMMENDATIONS -> loadRecommendations()
            ContentType.NEW -> loadNewContent()
        }
    }

    private fun loadPopularContent() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            Napier.d("Loading popular content dynamically from API", tag = "DiscoverViewModel")
            
            // Load featured podcasts (no genre filter)
            val featuredResult = podcastRepository.getFeaturedPodcasts()
            val featured = featuredResult.fold(
                onSuccess = { podcasts ->
                    Napier.d("Found ${podcasts.size} featured podcasts", tag = "DiscoverViewModel")
                    podcasts
                },
                onFailure = { exception ->
                    Napier.e("Failed to load featured podcasts", exception, tag = "DiscoverViewModel")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Failed to load featured podcasts"
                    )
                    return@launch
                }
            )
            
            // Load genres dynamically from API
            val genresResult = podcastRepository.getGenres()
            val genres = genresResult.fold(
                onSuccess = { genreList ->
                    Napier.d("Found ${genreList.size} genres from API", tag = "DiscoverViewModel")
                    genreList.filter { it.parentId == null }.take(5) // Top-level genres only, limit to 5
                },
                onFailure = { exception ->
                    Napier.e("Failed to load genres", exception, tag = "DiscoverViewModel")
                    emptyList()
                }
            )
            
            // Load podcasts for each genre dynamically
            val genreSections = mutableListOf<GenreSection>()
            genres.forEach { genre ->
                val podcastsResult = podcastRepository.getPodcastsByGenre(genre.id)
                val podcasts = podcastsResult.getOrNull()?.take(6) ?: emptyList()
                if (podcasts.isNotEmpty()) {
                    genreSections.add(GenreSection(genre, podcasts))
                    Napier.d("Genre ${genre.name} (${genre.id}): ${podcasts.size} podcasts", tag = "DiscoverViewModel")
                }
            }
            
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                featuredPodcasts = featured.take(6),
                genreSections = genreSections,
                availableGenres = genres,
                errorMessage = null
            )
            
            Napier.d("Loaded ${featured.size} featured podcasts and ${genreSections.size} genre sections", tag = "DiscoverViewModel")
        }
    }

    private fun loadRecommendations() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            Napier.d("Loading recommendations", tag = "DiscoverViewModel")
            
            // Get some featured content as recommendations for now
            val featuredResult = podcastRepository.getFeaturedPodcasts()
            val featured = featuredResult.getOrNull() ?: emptyList()
            
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                featuredPodcasts = featured.take(6),
                genreSections = emptyList(), // Will be improved later with user preferences
                availableGenres = emptyList(),
                errorMessage = null
            )
        }
    }

    private fun loadNewContent() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            Napier.d("Loading new content", tag = "DiscoverViewModel")
            
            // Get some featured content as new content for now  
            val featuredResult = podcastRepository.getFeaturedPodcasts()
            val featured = featuredResult.getOrNull() ?: emptyList()
            
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                featuredPodcasts = featured.take(6),
                genreSections = emptyList(), // Will be improved later with recency filters
                availableGenres = emptyList(),
                errorMessage = null
            )
        }
    }

    fun retry() {
        when (_uiState.value.selectedContentType) {
            ContentType.POPULAR -> loadPopularContent()
            ContentType.RECOMMENDATIONS -> loadRecommendations()  
            ContentType.NEW -> loadNewContent()
        }
    }
}