package dev.orangepie.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.orangepie.data.db.entity.PodcastLibraryEntity

@Database(entities = [PodcastLibraryEntity::class], version = 1)
abstract class LibraryDatabase: RoomDatabase() {
    abstract fun podcastDao(): LibraryDao
}