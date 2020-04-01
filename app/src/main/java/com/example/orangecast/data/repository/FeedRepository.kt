package com.example.orangecast.data.repository

import com.example.orangecast.data.Repository
import com.example.orangecast.data.api.FeedApi
import com.example.orangecast.di.module.ApiModule
import com.example.orangecast.entity.Episode
import com.example.orangecast.entity.RSSChannel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Named

class FeedRepository @Inject constructor(
    @Named(ApiModule.FEED) private val feedApi: FeedApi
) : Repository {

    fun getFeed(feedUrl: String): Single<List<Episode>> {
        return feedApi.getFeed(feedUrl)
            .subscribeToResponse()
            .mapFeedToEpisodeList()
    }

    private fun Single<RSSChannel>.mapFeedToEpisodeList(): Single<List<Episode>> {
        return this.map { channel ->
            val list = arrayListOf<Episode>()
            channel.item.forEach {
                list.add(
                    Episode(it.title, it.link, it.pubDate, it.description, it.duration, it.image, it.episode)
                )
            }
            list as List<Episode>
        }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
    }
}