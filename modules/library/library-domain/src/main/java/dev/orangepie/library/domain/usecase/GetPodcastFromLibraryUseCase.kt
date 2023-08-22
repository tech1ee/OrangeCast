package dev.orangepie.library.domain.usecase

import dev.orangepie.data.repository.PodcastLibraryRepository
import dev.orangepie.library.domain.mapper.PodcastLibraryMapper
import dev.orangepie.podcasts.domain.model.PodcastChannelModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetPodcastFromLibraryUseCase @Inject constructor(
    private val mapper: PodcastLibraryMapper,
    private val repository: PodcastLibraryRepository
) {

    suspend operator fun invoke(itunesId: Long): PodcastChannelModel? = withContext(Dispatchers.IO) {
        repository.getPodcast(itunesId)?.let { mapper.toModel(it) }
    }
}