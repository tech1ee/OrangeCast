package dev.orangepie.podcasts.data.mapper

import dev.orangepie.podcasts.data.model.PodcastListenNotesRepoModel
import dev.orangepie.podcasts.data.model.PodcastListenNotesResponse
import dev.orangepie.podcasts.data.model.PodcastsITunesRepoModel
import dev.orangepie.podcasts.data.model.PodcastsITunesResponse
import javax.inject.Inject

class PodcastsITunesRepoMapper @Inject constructor() {

    fun toRepoModel(response: PodcastsITunesResponse): PodcastsITunesRepoModel {
        return PodcastsITunesRepoModel(
            wrapperType = response.wrapperType,
            kind = response.kind,
            artistId = response.artistId,
            collectionId = response.collectionId,
            trackId = response.trackId,
            artistName = response.artistName,
            collectionName = response.collectionName,
            trackName = response.trackName,
            collectionCensoredName = response.collectionCensoredName,
            trackCensoredName = response.trackCensoredName,
            artistViewUrl = response.artistViewUrl,
            collectionViewUrl = response.collectionViewUrl,
            feedUrl = response.feedUrl,
            trackViewUrl = response.trackViewUrl,
            artworkUrl30 = response.artworkUrl30,
            artworkUrl60 = response.artworkUrl60,
            artworkUrl100 = response.artworkUrl100,
            collectionPrice = response.collectionPrice,
            trackPrice = response.trackPrice,
            trackRentalPrice = response.trackRentalPrice,
            collectionHdPrice = response.collectionHdPrice,
            trackHdPrice = response.trackHdPrice,
            trackHdRentalPrice = response.trackHdRentalPrice,
            releaseDate = response.releaseDate,
            collectionExplicitness = response.collectionExplicitness,
            trackExplicitness = response.trackExplicitness,
            trackCount = response.trackCount,
            country = response.country,
            currency = response.currency,
            primaryGenreName = response.primaryGenreName,
            contentAdvisoryRating = response.contentAdvisoryRating,
            artworkUrl600 = response.artworkUrl600,
            genreIds = response.genreIds,
            genres = response.genres,
        )
    }

    fun toRepoModel(response: PodcastListenNotesResponse): PodcastListenNotesRepoModel {
        return PodcastListenNotesRepoModel(
            id = response.id,
            title = response.title,
            publisher = response.publisher,
            image = response.image,
            thumbnail = response.thumbnail,
            totalEpisodes = response.totalEpisodes,
            explicitContent = response.explicitContent,
            description = response.description,
            itunesId = response.itunesId,
            language = response.language,
            country = response.country,
            website = response.website,
            isClaimed = response.isClaimed,
            genreIds = response.genreIds,
        )
    }
}