package com.example.orangecast.interactor

import com.example.orangecast.data.repository.FeedRepository
import com.example.orangecast.data.repository.SearchRepository
import com.example.orangecast.entity.Episode
import com.example.orangecast.entity.Channel
import com.example.orangecast.entity.Feed
import io.reactivex.Single

class ChannelInteractor(
    private val searchRepository: SearchRepository,
    private val feedRepository: FeedRepository
): BaseInteractor() {

    fun getChannelDetailsWithFeed(feedUrl: String): Single<Channel> {
        return feedRepository.getFeed(feedUrl)
            .map {
                val channel = searchRepository.getChannelByFeedUrl(feedUrl)
                channel?.feed = it
                channel ?: throw Throwable("Channel is null")
            }
    }
}