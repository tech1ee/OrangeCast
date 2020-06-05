package com.example.data.repository

import androidx.collection.ArraySet
import com.example.data.network.Api
import com.example.data.network.entity.ArtistResponse
import com.example.data.network.entity.GenreResponse
import io.reactivex.Single

class SearchRepository(
    private val searchApi: Api
) : Repository {

    private var discoverData: List<GenreResponse>? = null

    fun discover(isRefresh: Boolean, parameters: Map<String, String>): Single<List<GenreResponse>> {
        return if (isRefresh || discoverData == null) {
            searchApi.search(parameters)
                .subscribeToResponse()
                .map { data ->
                    discoverData = sortGenres(data.results)
                    discoverData
                }
        } else Single.just(discoverData)
    }

    fun getArtistByFeedUrl(feedUrl: String): ArtistResponse? {
        var artist: ArtistResponse? = null
        discoverData?.forEach { data ->
            artist = data.list.find { it.feedUrl == feedUrl }
        }
        return artist
    }

    private fun sortGenres(results: List<ArtistResponse>): List<GenreResponse> {
        val genres = ArraySet<GenreResponse>()
        results.forEach { media ->
            if (media.genreIds != null) {
                for (i in media.genreIds.indices) {
                    val genreId = media.genreIds[i]
                    if (genres.find { it.genreId == genreId } != null) {
                        genres.find { it.genreId == genreId }?.list?.add(media)
                    } else {
                        genres.add(GenreResponse(genreId, media.genres?.get(i)))
                    }
                }
            }
        }
        genres.removeAll { it.list.isEmpty() || it.genre?.toLowerCase() == "podcasts" }
        return genres.sortedByDescending { it.list.size }
    }
}