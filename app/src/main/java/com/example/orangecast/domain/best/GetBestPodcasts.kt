package com.example.orangecast.domain.best

import com.example.orangecast.data.listennotes.ListenNotesRepository
import com.example.orangecast.domain.toUIChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetBestPodcasts @Inject constructor(
    private val listenNotesRepository: ListenNotesRepository
) {

    fun execute(
        sort: String? = SORT_LISTEN_SCORE,
        genreId: Int? = null,
        page: Int? = null,
        region: String? = null,
        safeMode: Int? = 0
    ): Flow<BestPodcastsState> = flow {
        emit(BestPodcastsState.Loading)

        val queryMap = hashMapOf<String, String>()
        sort?.let { queryMap.put("sort", it) }
        genreId?.let { queryMap.put("genre_id", it.toString()) }
        page?.let { queryMap.put("page", it.toString()) }
        region?.let { queryMap.put("region", it) }
        safeMode?.let { queryMap.put("safe_mode", it.toString()) }

        try {
            val data = listenNotesRepository.getBestPodcasts(queryMap)
            emit(BestPodcastsState.Data(data.podcasts.toUIChannel()))
        } catch (e: Exception) {
            emit(BestPodcastsState.Error(e))
        }
    }

    companion object {
        const val SORT_LISTEN_SCORE = "listen_score"
    }
}