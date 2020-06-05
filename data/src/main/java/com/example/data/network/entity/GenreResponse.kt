package com.example.data.network.entity

import androidx.collection.ArraySet

class GenreResponse(
        val genreId: String?,
        val genre: String?
) {
    val list: ArraySet<ArtistResponse> = ArraySet()
}