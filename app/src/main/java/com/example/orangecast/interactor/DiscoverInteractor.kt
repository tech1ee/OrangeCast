package com.example.orangecast.interactor

import androidx.collection.ArraySet
import androidx.collection.arraySetOf
import com.example.orangecast.data.ArtistsByGenre
import com.example.orangecast.data.MediaItem
import com.example.orangecast.data.Parameters
import com.example.orangecast.network.repository.Repository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class DiscoverInteractor(
    private val repository: Repository
) {

    fun fetchGenres(): Observable<List<ArtistsByGenre>> {
        val parameters = mutableMapOf<String, String>()
        parameters[Parameters.Search.Key.TERM] = Parameters.Search.Value.PODCAST
        parameters[Parameters.Search.Key.MEDIA] = Parameters.Search.Value.PODCAST
        parameters[Parameters.Search.Key.LIMIT] = Parameters.Search.Value.MAX_LIMIT
        return repository.search(parameters)
            .subscribeOn(Schedulers.io())
            .map { sortGenres(it.results) }
            .observeOn(AndroidSchedulers.mainThread())
            .toObservable()
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
        genres.removeAll { it.list.isEmpty() || it.title?.toLowerCase() == "podcasts"}
        return genres.sortedByDescending { it.list.size }
    }
}