package dev.orangepie.genres.data.api

import dev.orangepie.genres.data.model.PodcastGenresListenNotesResponse
import retrofit2.http.GET

interface PodcastGenresListenNotesApi {

    @GET("genres")
    suspend fun getPodcastGenres(): PodcastGenresListenNotesResponse
}