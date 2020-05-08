package com.example.orangecast.interactor

import androidx.collection.arraySetOf
import com.example.data.repository.SearchRepository
import com.example.orangecast.entity.Artist
import com.example.orangecast.entity.Artists
import com.example.orangecast.entity.Parameters
import com.example.orangecast.mapper.mapToAppEntity
import io.reactivex.Single

class GenresInteractor(
    private val repository: SearchRepository
): BaseInteractor() {

    fun fetchAllGenres(isRefresh: Boolean): Single<List<Artists>> {
        val parameters = mutableMapOf<String, String>()
        parameters.putAll(baseParameters)
        parameters[Parameters.Search.Key.LIMIT] = Parameters.Search.Value.MAX_LIMIT
        return repository.discover(isRefresh, parameters)
            .map { sortGenres(it.results.mapToAppEntity()) }
    }

    private fun sortGenres(results: List<Artist>): List<Artists> {
        val genres = arraySetOf<Artists>()
        results.forEach { media ->
            if (media.genreIds != null) {
                for (i in media.genreIds.indices) {
                    val genreId = media.genreIds[i]
                    if (genres.find { it.id == genreId } != null) {
                        genres.find { it.id == genreId }?.list?.add(media)
                    } else {
                        genres.add(Artists(genreId, media.genres?.get(i)))
                    }
                }
            }
        }
        genres.removeAll { it.list.isEmpty() || it.title?.toLowerCase() == "podcasts" }
        return genres.sortedByDescending { it.list.size }
    }
}