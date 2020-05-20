package com.example.data.repository

import com.example.data.database.dao.ArtistDao
import com.example.data.database.entity.ArtistEntity
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SubscriptionsRepository(
    private val artistDao: ArtistDao
): Repository {

    fun isSubscribed(artistId: String): Single<Boolean> {
        return artistDao.getArtistById(artistId)
            .map { !it.isNullOrEmpty() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun addSubscription(artist: ArtistEntity): Completable {
        return Completable.fromAction {
            artistDao.insertSubscriptionArtist(artist)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun deleteSubscription(artist: ArtistEntity): Completable {
        return Completable.fromAction {
            artistDao.removeSubscriptionArtist(artist.artistId)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getAllSubscriptionArtists(): Single<List<ArtistEntity>> {
        return artistDao.getAllSubscriptionArtists()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}