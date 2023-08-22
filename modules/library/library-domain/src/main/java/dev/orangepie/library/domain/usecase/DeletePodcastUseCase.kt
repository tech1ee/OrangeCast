package dev.orangepie.library.domain.usecase

import dev.orangepie.data.repository.PodcastLibraryRepository
import dev.orangepie.details.domain.model.PodcastDetailsModel
import dev.orangepie.library.domain.mapper.PodcastLibraryMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DeletePodcastUseCase @Inject constructor(
    private val getLibrary: PodcastLibraryStateUseCase,
    private val libraryRepository: PodcastLibraryRepository,
    private val mapper: PodcastLibraryMapper
) {

    suspend operator fun invoke(podcast: PodcastDetailsModel) = withContext(Dispatchers.IO) {
        libraryRepository.delete(mapper.toRepoModel(podcast))
        getLibrary.update()
    }
}