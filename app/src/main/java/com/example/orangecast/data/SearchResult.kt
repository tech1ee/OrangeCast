package com.example.orangecast.data

import com.google.gson.annotations.SerializedName

class SearchResult(
    @SerializedName("resultCount") val resultCount: Int?,
    @SerializedName("results") val results: List<MediaItem?>?
)