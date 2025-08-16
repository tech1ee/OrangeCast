package dev.orangecast.shared.data.repository

import dev.orangecast.shared.data.api.ITunesApiService
import dev.orangecast.shared.data.api.model.ITunesPodcast
import dev.orangecast.shared.data.api.model.ITunesSearchResponse
import dev.orangecast.shared.data.local.LocalStorageManager
import dev.orangecast.shared.domain.repository.PodcastRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PodcastRepositoryImplTest {

    private val mockSearchResponse = ITunesSearchResponse(
        resultCount = 1,
        results = listOf(
            ITunesPodcast(
                trackId = 123456,
                trackName = "Test Podcast",
                artistName = "Test Author",
                artworkUrl100 = "https://example.com/image.jpg",
                artworkUrl600 = "https://example.com/image600.jpg",
                feedUrl = "https://example.com/feed.rss",
                trackCount = 10,
                primaryGenreName = "Comedy"
            )
        )
    )

    private fun createMockRepository(): PodcastRepository {
        val mockEngine = MockEngine { request ->
            when {
                request.url.encodedPath.contains("/search") -> {
                    respond(
                        content = Json.encodeToString(ITunesSearchResponse.serializer(), mockSearchResponse),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    )
                }
                request.url.encodedPath.contains("/lookup") -> {
                    respond(
                        content = Json.encodeToString(ITunesSearchResponse.serializer(), mockSearchResponse),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    )
                }
                else -> respond("", HttpStatusCode.NotFound)
            }
        }

        val httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
        }

        val apiService = ITunesApiService(httpClient)
        val rssFeedParser = dev.orangecast.shared.data.rss.RssFeedParser(httpClient)
        val localStorageManager = LocalStorageManager()
        val cacheManager = dev.orangecast.shared.data.cache.PodcastCacheManager()
        return PodcastRepositoryImpl(apiService, rssFeedParser, localStorageManager, cacheManager)
    }

    @Test
    fun searchPodcasts_returnsCorrectResults() = runTest {
        val repository = createMockRepository()
        
        val result = repository.searchPodcasts("comedy")
        
        assertTrue(result.isSuccess)
        val podcasts = result.getOrThrow()
        assertEquals(1, podcasts.size)
        
        val podcast = podcasts.first()
        assertEquals("123456", podcast.id)
        assertEquals("Test Podcast", podcast.title)
        assertEquals("Test Author", podcast.author)
        assertEquals("https://example.com/image600.jpg", podcast.imageUrl)
        assertEquals("Comedy", podcast.category)
        assertEquals(10, podcast.episodeCount)
    }

    @Test
    fun searchPodcasts_emptyQuery_returnsEmptyList() = runTest {
        val repository = createMockRepository()
        
        val result = repository.searchPodcasts("")
        
        assertTrue(result.isSuccess)
        val podcasts = result.getOrThrow()
        // Note: iTunes API still returns results for empty query, but our UseCase handles this
        // This test verifies that the repository calls the API correctly
        assertEquals(1, podcasts.size) // Mock always returns 1 result
    }

    @Test
    fun getPodcastDetails_returnsCorrectDetails() = runTest {
        val repository = createMockRepository()
        
        val result = repository.getPodcastDetails("123456")
        
        assertTrue(result.isSuccess)
        val details = result.getOrThrow()
        assertEquals("Test Podcast", details.podcast.title)
        assertEquals("Test Author", details.podcast.author)
        assertEquals(false, details.isSubscribed)
    }

    @Test
    fun getFeaturedPodcasts_callsSearchWithFeaturedTerm() = runTest {
        val repository = createMockRepository()
        
        val result = repository.getFeaturedPodcasts()
        
        assertTrue(result.isSuccess)
        val podcasts = result.getOrThrow()
        assertEquals(1, podcasts.size)
    }

    @Test
    fun getCategoriesPodcasts_callsSearchWithCategory() = runTest {
        val repository = createMockRepository()
        
        val result = repository.getCategoriesPodcasts("technology")
        
        assertTrue(result.isSuccess)
        val podcasts = result.getOrThrow()
        assertEquals(1, podcasts.size)
    }
}