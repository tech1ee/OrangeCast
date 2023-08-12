package dev.orangepie.details.ui.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.orangepie.details.ui.model.PodcastRSSFeedItemUIModel

@Composable
fun PodcastEpisodeList(
    list: List<PodcastRSSFeedItemUIModel>,
    onPlayClick: (item: PodcastRSSFeedItemUIModel) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(list.size) { index ->
            PodcastEpisodeListItem(
                item = list[index],
                onPlayClick = onPlayClick,
            )
        }
    }
}