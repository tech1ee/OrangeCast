package dev.orangepie.details.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.orangepie.details.ui.model.PodcastDetailsUIModel
import dev.orangepie.details.ui.model.PodcastRSSFeedItemUIModel
import kotlinx.collections.immutable.ImmutableList

@Composable
fun PodcastDetailsScreenContent(
    details: PodcastDetailsUIModel,
    episodes: ImmutableList<PodcastRSSFeedItemUIModel>,
    onBackClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        PodcastDetailsHeader(
            details = details,
            onBackClick = onBackClick
        )
        PodcastEpisodeList(
            list = episodes
        )
    }
}