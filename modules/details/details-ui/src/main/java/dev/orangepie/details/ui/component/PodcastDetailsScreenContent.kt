package dev.orangepie.details.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import dev.orangepie.details.ui.model.PodcastDetailsUIModel
import dev.orangepie.details.ui.model.PodcastRSSFeedItemUIModel
import kotlinx.collections.immutable.ImmutableList
import java.lang.Float.min

@Composable
fun PodcastDetailsScreenContent(
    details: PodcastDetailsUIModel,
    episodes: ImmutableList<PodcastRSSFeedItemUIModel>,
    onPlayClick: (item: PodcastRSSFeedItemUIModel) -> Unit,
    onBackClick: () -> Unit,
) {
    val listState = rememberLazyListState()
    val scrollOffset: Float = min(
        1f,
        1 - (listState.firstVisibleItemScrollOffset / 600f + listState.firstVisibleItemIndex)
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            PodcastDetailsHeader(
                details = details,
                scrollOffset = scrollOffset,
                onBackClick = onBackClick
            )
        },
        content = { contentPadding ->
            PodcastEpisodeList(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                lazyListState = listState,
                list = episodes,
                onPlayClick = onPlayClick,
            )
        }
    )
}