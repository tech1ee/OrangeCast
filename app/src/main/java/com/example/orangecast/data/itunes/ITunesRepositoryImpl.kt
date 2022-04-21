package com.example.orangecast.data.itunes

import com.example.orangecast.data.itunes.entity.SearchResultITunes
import javax.inject.Inject

class ITunesRepositoryImpl @Inject constructor(
        private val api: ITunesApi
): ITunesRepository {

    override suspend fun search(
            queryMap: Map<String, String>,
            success: (data: SearchResultITunes) -> Unit,
            fail: (e: Exception) -> Unit
    ) {
        try {
            val result = api.search(queryMap)
            val data = result.body()
            if (result.isSuccessful && data != null) success(data)
            else fail(Exception())
        } catch (e: Exception) {
            fail(e)
        }
    }
}