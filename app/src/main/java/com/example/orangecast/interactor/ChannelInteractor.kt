package com.example.orangecast.interactor

import com.example.data.repository.FeedRepository
import com.example.data.repository.SearchRepository
import com.example.orangecast.entity.Artist
import com.example.orangecast.mapper.mapResponseToAppEntity
import io.reactivex.Single

class ChannelInteractor(
    private val searchRepository: SearchRepository,
    private val feedRepository: FeedRepository
): BaseInteractor() {

    fun getChannelDetailsWithFeed(feedUrl: String): Single<Artist> {
        return feedRepository.getFeed(feedUrl)
            .map {
                val artistResponse = searchRepository.getChannelByFeedUrl(feedUrl)
                val artist: Artist? = artistResponse?.mapResponseToAppEntity(it)
                artist ?: throw Throwable("Artist is null")
            }
    }
}