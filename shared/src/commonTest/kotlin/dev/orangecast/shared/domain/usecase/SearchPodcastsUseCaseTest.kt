package dev.orangecast.shared.domain.usecase

import dev.orangecast.shared.domain.model.Podcast
import dev.orangecast.shared.domain.repository.PodcastRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SearchPodcastsUseCaseTest {

    private class MockPodcastRepository : PodcastRepository {
        var searchQuery: String? = null
        
        private val testPodcast = Podcast(
            id = "1",
            title = "Test Podcast",
            description = "Test Description",
            imageUrl = "https://example.com/image.jpg",
            author = "Test Author",
            category = "Comedy",
            language = "en",
            isExplicit = false,
            episodeCount = 5,
            lastUpdated = System.currentTimeMillis()
        )

        override suspend fun searchPodcasts(query: String): Result<List<Podcast>> {
            searchQuery = query
            return if (query.isNotBlank()) {
                Result.success(listOf(testPodcast))
            } else {
                Result.success(emptyList())
            }
        }

        override suspend fun getPodcastDetails(podcastId: String) = Result.failure<dev.orangecast.shared.domain.model.PodcastDetails>(NotImplementedError())
        override suspend fun getEpisodes(podcastId: String) = Result.failure<List<dev.orangecast.shared.domain.model.PodcastEpisode>>(NotImplementedError())
        override suspend fun subscribeToPodcast(podcast: Podcast) = Result.failure<Unit>(NotImplementedError())
        override suspend fun unsubscribeFromPodcast(podcastId: String) = Result.failure<Unit>(NotImplementedError())
        override fun getSubscribedPodcasts() = flowOf(emptyList<Podcast>())
        override suspend fun getFeaturedPodcasts() = Result.failure<List<Podcast>>(NotImplementedError())
        override suspend fun getCategoriesPodcasts(category: String) = Result.failure<List<Podcast>>(NotImplementedError())
        override suspend fun getAvailableCategories(): List<String> = listOf("Comedy", "Technology", "News")
    }

    @Test
    fun invoke_withValidQuery_callsRepositoryWithTrimmedQuery() = runTest {
        val repository = MockPodcastRepository()
        val useCase = SearchPodcastsUseCase(repository)
        
        val result = useCase("  comedy  ")
        
        assertTrue(result.isSuccess)
        assertEquals("comedy", repository.searchQuery)
        val podcasts = result.getOrThrow()
        assertEquals(1, podcasts.size)
        assertEquals("Test Podcast", podcasts.first().title)
    }

    @Test
    fun invoke_withBlankQuery_returnsEmptyList() = runTest {
        val repository = MockPodcastRepository()
        val useCase = SearchPodcastsUseCase(repository)
        
        val result = useCase("   ")
        
        assertTrue(result.isSuccess)
        val podcasts = result.getOrThrow()
        assertTrue(podcasts.isEmpty())
    }

    @Test
    fun invoke_withEmptyQuery_returnsEmptyList() = runTest {
        val repository = MockPodcastRepository()
        val useCase = SearchPodcastsUseCase(repository)
        
        val result = useCase("")
        
        assertTrue(result.isSuccess)
        val podcasts = result.getOrThrow()
        assertTrue(podcasts.isEmpty())
    }
}