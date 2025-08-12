package dev.orangecast.shared.data.api

import dev.orangecast.shared.data.api.model.ITunesSearchResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class ITunesApiService(
    private val httpClient: HttpClient
) {
    suspend fun searchPodcasts(
        query: String,
        limit: Int = 50
    ): ITunesSearchResponse {
        return httpClient.get("https://itunes.apple.com/search") {
            parameter("term", query)
            parameter("media", "podcast")
            parameter("limit", limit)
        }.body()
    }

    suspend fun lookupPodcast(podcastId: String): ITunesSearchResponse {
        return httpClient.get("https://itunes.apple.com/lookup") {
            parameter("id", podcastId)
        }.body()
    }
}