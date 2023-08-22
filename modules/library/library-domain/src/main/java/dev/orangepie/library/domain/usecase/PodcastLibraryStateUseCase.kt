package dev.orangepie.library.domain.usecase

import dev.orangepie.data.repository.PodcastLibraryRepository
import dev.orangepie.library.domain.mapper.PodcastLibraryMapper
import dev.orangepie.podcasts.domain.model.PodcastChannelModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PodcastLibraryStateUseCase @Inject constructor(
    private val libraryRepository: PodcastLibraryRepository,
    private val mapper: PodcastLibraryMapper,
) {

    private val podcastLibraryState = MutableSharedFlow<List<PodcastChannelModel>>(replay = 1)
    val libraryState = podcastLibraryState.asSharedFlow()

    suspend fun update() = withContext(Dispatchers.IO) {
        podcastLibraryState.emit(
            libraryRepository.getAll().map { mapper.toModel(it) }
        )
    }
}