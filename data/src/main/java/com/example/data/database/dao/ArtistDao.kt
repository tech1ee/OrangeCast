package com.example.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.data.database.entity.ArtistEntity
import io.reactivex.Single

@Dao
interface ArtistDao {

    @Query("SELECT * FROM ARTIST WHERE :artistId IS artistId")
    fun getArtistById(artistId: String): Single<List<ArtistEntity>>

    @Insert
    fun insertSubscriptionArtist(artistEntity: ArtistEntity)

    @Query("DELETE FROM ARTIST WHERE :artistId IS artistId")
    fun removeSubscriptionArtist(artistId: String)

    @Query("SELECT * FROM ARTIST")
    fun getAllSubscriptionArtists(): Single<List<ArtistEntity>>
}