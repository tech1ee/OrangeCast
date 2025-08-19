package dev.orangecast.shared.data.api

import dev.orangecast.shared.data.api.model.ListenNotesSearchResponse
import dev.orangecast.shared.data.api.model.ListenNotesPodcast
import dev.orangecast.shared.data.api.model.ListenNotesBestPodcastsResponse
import dev.orangecast.shared.data.api.model.ListenNotesGenresResponse
import dev.orangecast.shared.data.config.ApiConfig
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import io.github.aakira.napier.Napier

class ListenNotesApiService(
    private val httpClient: HttpClient,
    private val apiKey: String
) {
    private val json = Json { 
        ignoreUnknownKeys = true
        isLenient = true 
    }
    
    suspend fun searchPodcasts(
        query: String,
        limit: Int = 10,
        language: String = "English"
    ): ListenNotesSearchResponse {
        val response = httpClient.get(ApiConfig.LISTENNOTES_SEARCH_URL) {
            header("X-ListenAPI-Key", apiKey)
            parameter("q", query)
            parameter("type", "podcast")
            parameter("language", language)
            parameter("len_min", 10)
            parameter("len_max", 30)
            parameter("published_after", 0)
            parameter("only_in", "title,description")
            parameter("safe_mode", 1)
        }
        
        Napier.d("ListenNotes search response status: ${response.status}", tag = "ListenNotesAPI")
        
        if (!response.status.isSuccess()) {
            val errorBody = response.bodyAsText()
            Napier.e("ListenNotes search failed: ${response.status} - $errorBody", tag = "ListenNotesAPI")
            throw Exception("Search failed with status ${response.status}: $errorBody")
        }
        
        val jsonString = response.bodyAsText()
        Napier.d("ListenNotes search response: ${jsonString.take(200)}...", tag = "ListenNotesAPI")
        return json.decodeFromString<ListenNotesSearchResponse>(jsonString)
    }
    
    suspend fun lookupPodcast(podcastId: String): ListenNotesPodcast {
        val response = httpClient.get("${ApiConfig.LISTENNOTES_BASE_URL}/podcasts/$podcastId") {
            header("X-ListenAPI-Key", apiKey)
        }
        
        val jsonString = response.bodyAsText()
        return json.decodeFromString<ListenNotesPodcast>(jsonString)
    }
    
    suspend fun getBestPodcasts(
        region: String = "us",
        genreId: Int? = null
    ): ListenNotesBestPodcastsResponse {
        val response = httpClient.get(ApiConfig.LISTENNOTES_BEST_PODCASTS_URL) {
            header("X-ListenAPI-Key", apiKey)
            parameter("region", region)
            genreId?.let { parameter("genre_id", it) }
        }
        
        Napier.d("ListenNotes best podcasts response status: ${response.status}", tag = "ListenNotesAPI")
        
        if (!response.status.isSuccess()) {
            val errorBody = response.bodyAsText()
            Napier.e("ListenNotes best podcasts failed: ${response.status} - $errorBody", tag = "ListenNotesAPI")
            throw Exception("Best podcasts failed with status ${response.status}: $errorBody")
        }
        
        val jsonString = response.bodyAsText()
        Napier.d("ListenNotes best podcasts response: ${jsonString.take(200)}...", tag = "ListenNotesAPI")
        return json.decodeFromString<ListenNotesBestPodcastsResponse>(jsonString)
    }
    
    suspend fun getGenres(): ListenNotesGenresResponse {
        val response = httpClient.get(ApiConfig.LISTENNOTES_GENRES_URL) {
            header("X-ListenAPI-Key", apiKey)
        }
        
        val jsonString = response.bodyAsText()
        return json.decodeFromString<ListenNotesGenresResponse>(jsonString)
    }
    
    suspend fun searchEpisodes(
        query: String,
        limit: Int = 10
    ): ListenNotesSearchResponse {
        val response = httpClient.get(ApiConfig.LISTENNOTES_SEARCH_URL) {
            header("X-ListenAPI-Key", apiKey)
            parameter("q", query)
            parameter("type", "episode")
            parameter("len_min", 10)
            parameter("len_max", 60)
            parameter("published_after", 0)
            parameter("only_in", "title,description")
            parameter("safe_mode", 1)
        }
        
        val jsonString = response.bodyAsText()
        return json.decodeFromString<ListenNotesSearchResponse>(jsonString)
    }
    
    suspend fun debugRawBestPodcasts(): String {
        val response = httpClient.get(ApiConfig.LISTENNOTES_BEST_PODCASTS_URL) {
            header("X-ListenAPI-Key", apiKey)
        }
        
        val jsonString = response.bodyAsText()
        Napier.i("Raw response: ${jsonString.take(2000)}", tag = "ApiDebug")
        return jsonString
    }
}