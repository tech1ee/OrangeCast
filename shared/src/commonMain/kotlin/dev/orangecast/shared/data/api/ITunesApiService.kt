package dev.orangecast.shared.data.api

import dev.orangecast.shared.data.api.model.ITunesSearchResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json

class ITunesApiService(
    private val httpClient: HttpClient
) {
    private val json = Json { 
        ignoreUnknownKeys = true
        isLenient = true 
    }
    
    suspend fun searchPodcasts(
        query: String,
        limit: Int = 50
    ): ITunesSearchResponse {
        val response = httpClient.get("https://itunes.apple.com/search") {
            parameter("term", query)
            parameter("media", "podcast")
            parameter("limit", limit)
        }
        
        val jsonString = response.bodyAsText()
        return json.decodeFromString<ITunesSearchResponse>(jsonString)
    }

    suspend fun lookupPodcast(podcastId: String): ITunesSearchResponse {
        val response = httpClient.get("https://itunes.apple.com/lookup") {
            parameter("id", podcastId)
        }
        val jsonString = response.bodyAsText()
        return json.decodeFromString<ITunesSearchResponse>(jsonString)
    }
}