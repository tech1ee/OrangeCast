package com.example.data.repository

import com.example.data.database.dao.ArtistDao
import com.example.data.database.entity.ArtistEntity
import io.reactivex.Single

class SubscriptionsRepository(
    private val artistDao: ArtistDao
): Repository {

    fun getAllSubscriptionArtists(): Single<List<ArtistEntity>> {
        return artistDao.getAllSubscriptionArtists()
    }
}