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

    fun getChannelDetails(feedUrl: String): Channel? {
        return searchRepository.getChannelByFeedUrl(feedUrl)
    }

    fun getChannelFeed(feedUrl: String): Single<Feed> {
        return feedRepository.getFeed(feedUrl)
    }
}