package com.example.orangecast.interactor

import androidx.collection.arraySetOf
import com.example.data.repository.SearchRepository
import com.example.orangecast.entity.Artist
import com.example.orangecast.entity.ArtistsGenre
import com.example.orangecast.entity.Genres
import com.example.orangecast.entity.Parameters
import com.example.orangecast.mapper.mapResponseToAppEntity
import io.reactivex.Single

class GenresInteractor(
    private val repository: SearchRepository
): BaseInteractor() {

    fun fetchAllGenres(isRefresh: Boolean): Single<Genres> {
        val parameters = mutableMapOf<String, String>()
        parameters.putAll(baseParameters)
        parameters[Parameters.Search.Key.LIMIT] = Parameters.Search.Value.MAX_LIMIT
        return repository.discover(isRefresh, parameters)
            .map {
                Genres(it.mapResponseToAppEntity())
            }
    }
}