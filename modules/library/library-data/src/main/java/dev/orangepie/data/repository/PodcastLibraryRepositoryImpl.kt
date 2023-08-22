package dev.orangepie.data.repository

import dev.orangepie.data.db.LibraryDao
import dev.orangepie.data.mapper.PodcastLibraryRepoMapper
import dev.orangepie.data.model.PodcastLibraryRepoModel
import javax.inject.Inject

class PodcastLibraryRepositoryImpl @Inject constructor(
    private val dao: LibraryDao,
    private val mapper: PodcastLibraryRepoMapper
): PodcastLibraryRepository {

    override suspend fun getAll(): List<PodcastLibraryRepoModel> {
        return dao.getAll().map { mapper.toRepoModel(it) }
    }

    override suspend fun getPodcast(itunesId: Long): PodcastLibraryRepoModel? {
        return dao.getPodcast(itunesId)?.let { mapper.toRepoModel(it) }
    }

    override suspend fun save(podcast: PodcastLibraryRepoModel) {
        dao.insert(mapper.toEntity(podcast))
    }

    override suspend fun delete(podcast: PodcastLibraryRepoModel) {
        dao.delete(mapper.toEntity(podcast))
    }
}