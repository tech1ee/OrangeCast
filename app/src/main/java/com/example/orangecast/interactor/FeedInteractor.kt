package com.example.orangecast.interactor

import com.example.orangecast.data.repository.FeedRepository
import com.example.orangecast.entity.Episode
import io.reactivex.Single

class FeedInteractor(
    private val repository: FeedRepository
) : BaseInteractor() {


    fun getEpisodesFromRSS(url: String): Single<List<Episode>> {
        return repository.getFeed(url)
    }
}