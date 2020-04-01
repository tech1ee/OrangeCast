package com.example.orangecast.interactor

import com.example.orangecast.data.repository.FeedRepository
import com.example.orangecast.data.repository.SearchRepository
import com.example.orangecast.entity.Episode
import com.example.orangecast.entity.RSSChannel
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

class FeedInteractor(
    private val repository: FeedRepository
) : BaseInteractor() {


    fun getEpisodesFromRSS(url: String): Single<List<Episode>> {
        return repository.getFeed(url)
    }
}