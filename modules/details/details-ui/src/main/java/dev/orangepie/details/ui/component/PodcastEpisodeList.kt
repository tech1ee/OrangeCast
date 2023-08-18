package dev.orangepie.details.ui.component

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.orangepie.details.ui.model.PodcastRSSFeedItemUIModel

@Composable
fun PodcastEpisodeList(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState,
    list: List<PodcastRSSFeedItemUIModel>,
    onPlayClick: (item: PodcastRSSFeedItemUIModel) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        state = lazyListState,
    ) {
        items(list.size) { index ->
            PodcastEpisodeListItem(
                item = list[index],
                onPlayClick = onPlayClick,
            )
        }
    }
}