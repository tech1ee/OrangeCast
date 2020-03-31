package com.example.orangecast.interactor

import com.example.orangecast.data.Repository
import com.example.orangecast.entity.Episode
import com.example.orangecast.entity.RSSChannel
import io.reactivex.Single

class RSSInteractor(private val repository: Repository
) : BaseInteractor() {


    fun getEpisodesFromRSS(url: String): Single<List<Episode>> {
        return Single.defer {
            repository.getFeed(url).map {
                    mapFeedToEpisodeList(it)
                }
        }
    }

    private fun mapFeedToEpisodeList(channel: RSSChannel): List<Episode> {
        val list = arrayListOf<Episode>()
        channel.item.forEach {
            list.add(
                Episode(it.title, it.link, it.pubDate, it.description, it.duration, it.image, it.episode)
            )
        }
        return list
    }
}