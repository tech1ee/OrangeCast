package com.example.orangecast.data.itunes

import com.example.orangecast.data.itunes.entity.SearchResultITunes

interface ITunesRepository {

    suspend fun search(
            queryMap: Map<String, String>,
            success: (data: SearchResultITunes) -> Unit,
            fail: (e: Exception) -> Unit
    )
}