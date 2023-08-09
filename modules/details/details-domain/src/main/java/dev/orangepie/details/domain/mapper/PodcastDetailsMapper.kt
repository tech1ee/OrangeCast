package dev.orangepie.details.domain.mapper

import dev.orangepie.details.data.model.PodcastDetailsRepoModel
import dev.orangepie.details.data.model.PodcastRSSFeedRepoModel
import dev.orangepie.details.domain.model.PodcastDetailsModel
import dev.orangepie.details.domain.model.PodcastRSSFeedModel
import javax.inject.Inject

class PodcastDetailsMapper @Inject constructor(
    private val feedMapper: PodcastRSSFeedMapper,
) {

    fun toModel(repoModel: PodcastDetailsRepoModel, feedRepoModel: PodcastRSSFeedRepoModel): PodcastDetailsModel {
        return PodcastDetailsModel(
            collectionId = repoModel.collectionId,
            trackId = repoModel.trackId,
            artistName = repoModel.artistName,
            collectionName = repoModel.collectionName,
            trackName = repoModel.trackName,
            collectionViewUrl = repoModel.collectionViewUrl,
            feedUrl = repoModel.feedUrl,
            artworkUrl100 = repoModel.artworkUrl100,
            genres = repoModel.genres,
            feed = feedMapper.toModel(feedRepoModel),
        )
    }
}