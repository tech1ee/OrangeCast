package dev.orangepie.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.orangepie.data.db.entity.PodcastLibraryEntity

@Dao
interface LibraryDao {

    @Query("SELECT * FROM podcasts")
    fun getAll(): List<PodcastLibraryEntity>

    @Query("SELECT * FROM podcasts WHERE itunesId = :itunesId")
    fun getPodcast(itunesId: Long): PodcastLibraryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(podcast: PodcastLibraryEntity)

    @Delete
    fun delete(podcast: PodcastLibraryEntity)
}