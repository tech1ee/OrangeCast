package com.example.orangecast.domain.details

import com.example.orangecast.data.listennotes.ListenNotesRepository
import com.example.orangecast.domain.toUIChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetPodcastDetails @Inject constructor(
    private val listenNotesRepository: ListenNotesRepository
) {

    fun execute(
        channelId: String
    ): Flow<PodcastDetailsState> = flow {
        emit(PodcastDetailsState.Loading)

        val queryMap = hashMapOf<String, String>()
        queryMap["id"] = channelId
        queryMap["sort"] = SORT_RECENT_FIRST

        try {
            val data = listenNotesRepository.getPodcastDetails(queryMap)
            emit(PodcastDetailsState.Data(data.toUIChannel()))
        } catch (e: Exception) {
            emit(PodcastDetailsState.Error(e))
        }
    }

    companion object {
        const val SORT_RECENT_FIRST = "recent_first"
    }
}