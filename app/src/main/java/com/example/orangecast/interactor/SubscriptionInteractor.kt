package com.example.orangecast.interactor

import com.example.data.repository.SubscriptionsRepository
import com.example.orangecast.entity.Artist
import com.example.orangecast.mapper.mapDatabaseToAppEntity
import io.reactivex.Single

class SubscriptionInteractor(
    private val subscriptionsRepository: SubscriptionsRepository
): BaseInteractor() {

    fun getAllSubscriptions(): Single<List<Artist>> {
        return subscriptionsRepository.getAllSubscriptionArtists()
            .map { it.mapDatabaseToAppEntity() }
    }
}