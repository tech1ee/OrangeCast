package com.example.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.data.database.entity.ArtistEntity
import io.reactivex.Single

@Dao
interface ArtistDao {

    @Insert
    fun insertSubscriptionArtist(artistEntity: ArtistEntity)

    @Query("SELECT * FROM ARTIST")
    fun getAllSubscriptionArtists(): Single<List<ArtistEntity>>
}