package com.example.orangecast.entity

import androidx.collection.ArraySet
import androidx.collection.arraySetOf

class ArtistsByGenre(
    val id: String?,
    val title: String?
) {
    val list: ArraySet<MediaItem> = arraySetOf<MediaItem>()
}