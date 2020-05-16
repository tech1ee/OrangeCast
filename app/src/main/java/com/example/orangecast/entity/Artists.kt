package com.example.orangecast.entity

import androidx.collection.ArraySet
import androidx.collection.arraySetOf

class Artists(
    val genreId: String?,
    val genre: String?
) {
    val list: ArraySet<Artist> = arraySetOf()
}