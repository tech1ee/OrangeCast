package dev.orangepie.genres.domain.usecase

import dev.orangepie.genres.data.repository.PodcastsGenresRepository
import dev.orangepie.genres.domain.mapper.PodcastGenresMapper
import javax.inject.Inject

class GetPodcastGenresUseCase @Inject constructor(
    private val podcastsGenresRepository: PodcastsGenresRepository,
    private val podcastGenresMapper: PodcastGenresMapper,
) {

    suspend operator fun invoke() = podcastsGenresRepository.getPodcastGenresListenNotes()
        .map { podcastGenresMapper.toModel(it) }
}