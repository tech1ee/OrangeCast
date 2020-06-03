package com.example.orangecast.interactor

import com.example.data.repository.FeedRepository
import com.example.data.repository.SearchRepository
import com.example.orangecast.entity.Artist
import com.example.orangecast.entity.Feed
import com.example.orangecast.mapper.mapResponseToAppEntity
import io.reactivex.Observable
import io.reactivex.Single

class ArtistInteractor(
    private val searchRepository: SearchRepository,
    private val feedRepository: FeedRepository
) : BaseInteractor() {

    fun getArtistDetails(feedUrl: String): Single<Artist> {
        return Single.create {
            val artistResponse = searchRepository.getArtistByFeedUrl(feedUrl)
            val artist = artistResponse?.mapResponseToAppEntity() ?: return@create
            it.onSuccess(artist)
        }
    }

    fun getArtistFeed(feedUrl: String): Single<Feed> {
        return feedRepository.getFeed(feedUrl)
            .map { it.mapResponseToAppEntity() }
    }
}