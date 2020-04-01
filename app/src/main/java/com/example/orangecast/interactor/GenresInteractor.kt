package com.example.orangecast.interactor

import androidx.collection.arraySetOf
import com.example.orangecast.entity.ArtistsByGenre
import com.example.orangecast.entity.MediaItem
import com.example.orangecast.entity.Parameters
import com.example.orangecast.data.repository.SearchRepository
import io.reactivex.Single

class GenresInteractor(
    private val repository: SearchRepository
): BaseInteractor() {

    fun fetchAllGenres(): Single<List<ArtistsByGenre>> {
        val parameters = mutableMapOf<String, String>()
        parameters[Parameters.Search.Key.TERM] = Parameters.Search.Value.PODCAST
        parameters[Parameters.Search.Key.MEDIA] = Parameters.Search.Value.PODCAST
        parameters[Parameters.Search.Key.LIMIT] = Parameters.Search.Value.MAX_LIMIT
        return repository.search(parameters)
            .map { sortGenres(it.results) }
    }

    private fun sortGenres(results: List<MediaItem>): List<ArtistsByGenre> {
        val genres = arraySetOf<ArtistsByGenre>()
        results.forEach { media ->
            if (media.genreIds != null) {
                for (i in media.genreIds.indices) {
                    val genreId = media.genreIds[i]
                    if (genres.find { it.id == genreId } != null) {
                        genres.find { it.id == genreId }?.list?.add(media)
                    } else {
                        genres.add(ArtistsByGenre(genreId, media.genres?.get(i)))
                    }
                }
            }
        }
        genres.removeAll { it.list.isEmpty() || it.title?.toLowerCase() == "podcasts" }
        return genres.sortedByDescending { it.list.size }
    }
}