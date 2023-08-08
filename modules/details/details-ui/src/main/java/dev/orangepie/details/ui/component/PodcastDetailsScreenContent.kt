package dev.orangepie.details.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.orangepie.details.ui.model.PodcastDetailsUIModel

@Composable
fun PodcastDetailsScreenContent(
    details: PodcastDetailsUIModel,
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
    }
}