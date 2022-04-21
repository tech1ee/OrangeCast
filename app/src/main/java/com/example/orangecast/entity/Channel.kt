package com.example.orangecast.entity

data class Channel(
        val idListenNotes: String?,
        val idItunes: Int?,
        val image: String?,
        val thumbnail: String?,
        val description: String?,
        val website: String?,
        val genreIds: List<Int>?
)
