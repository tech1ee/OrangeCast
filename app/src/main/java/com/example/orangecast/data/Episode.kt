package com.example.orangecast.data

import java.util.*

class Episode(
    val title: String?,
    val author: String,
    val link: String,
    val pubDate: Date,
    val description: String,
    val content: String,
    val image: String,
    val categories: List<String>?
)