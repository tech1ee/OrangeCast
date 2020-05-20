package com.example.orangecast.interactor

import com.example.data.repository.SubscriptionsRepository
import com.example.orangecast.entity.Artist
import com.example.orangecast.entity.Subscriptions
import com.example.orangecast.mapper.mapAppEntityToDatabase
import com.example.orangecast.mapper.mapDatabaseToAppEntity
import io.reactivex.Completable
import io.reactivex.Single

class SubscriptionInteractor(
    private val subscriptionsRepository: SubscriptionsRepository
): BaseInteractor() {

    fun isSubscribed(artist: Artist): Single<Boolean> {
        return subscriptionsRepository.isSubscribed(artist.artistId ?: "")
    }

    fun addSubscription(artist: Artist): Completable {
        return subscriptionsRepository.addSubscription(artist.mapAppEntityToDatabase())
    }

    fun deleteSubscription(artist: Artist): Completable {
        return subscriptionsRepository.deleteSubscription(artist.mapAppEntityToDatabase())
    }

    fun getAllSubscriptions(): Single<Subscriptions> {
        return subscriptionsRepository.getAllSubscriptionArtists()
            .map { Subscriptions(it.mapDatabaseToAppEntity()) }
    }
}