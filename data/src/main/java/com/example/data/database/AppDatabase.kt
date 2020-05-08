package com.example.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.data.database.entity.ArtistEntity

@Database(entities = [ArtistEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {


    companion object {
        const val NAME = "AppDatabase"
    }
}