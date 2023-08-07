package dev.orangepie.podcasts.domain.usecase

import dev.orangepie.genres.domain.usecase.GetPodcastGenresUseCase
import dev.orangepie.podcasts.data.repository.PodcastsRepository
import dev.orangepie.podcasts.domain.mapper.PodcastChannelMapper
import dev.orangepie.podcasts.domain.model.PodcastsByGenreModel
import javax.inject.Inject

class GetBestPodcastsUseCase @Inject constructor(
    private val getPodcastGenres: GetPodcastGenresUseCase,
    private val repository: PodcastsRepository,
    private val channelMapper: PodcastChannelMapper
) {

    suspend operator fun invoke(): List<PodcastsByGenreModel> {
        val genres = getPodcastGenres()
        val repoModel = repository.getBestPodcasts(
            mapOf(
                "region" to "us",
            )
        )
        val channels = repoModel.map { channelMapper.toModel(it, genres) }
        return genres.map { genre ->
            PodcastsByGenreModel(
                genre = genre,
                podcasts = channels.filter { it.genres.contains(genre) }
            )
        }
            .filter { it.podcasts.isNotEmpty() }
            .sortedByDescending { it.podcasts.size }
    }
}