package dev.orangepie.details.data.repository

import dev.orangepie.details.data.model.PodcastDetailsRepoModel

interface PodcastDetailsRepository {

    suspend fun getPodcastDetails(id: Long): PodcastDetailsRepoModel
}