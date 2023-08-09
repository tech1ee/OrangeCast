package dev.orangepie.details.ui.model

import dev.orangepie.details.domain.model.PodcastDetailsModel

data class PodcastDetailsViewModelState(
    val loading: Boolean = false,
    val error: Boolean = false,
    val details: PodcastDetailsModel? = null,
)
