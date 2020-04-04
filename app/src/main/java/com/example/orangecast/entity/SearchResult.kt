package com.example.orangecast.entity

import com.google.gson.annotations.SerializedName

class SearchResult(
    @SerializedName("resultCount") val resultCount: Int?,
    @SerializedName("results") val results: List<Channel>
)