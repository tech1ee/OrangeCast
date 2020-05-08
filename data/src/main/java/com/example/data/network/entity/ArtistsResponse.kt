package com.example.data.network.entity

import androidx.collection.ArraySet

class ArtistsResponse(
    val id: String?,
    val title: String?
) {
    val list: ArraySet<ArtistResponse> = ArraySet<ArtistResponse>()
}