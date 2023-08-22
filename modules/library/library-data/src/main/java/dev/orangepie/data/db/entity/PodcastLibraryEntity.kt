package dev.orangepie.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "podcasts")
data class PodcastLibraryEntity(
    @PrimaryKey
    val itunesId: Long,
    val title: String?,
    val imagePreviewUrl: String?,
    val imageFullUrl: String?,
)
