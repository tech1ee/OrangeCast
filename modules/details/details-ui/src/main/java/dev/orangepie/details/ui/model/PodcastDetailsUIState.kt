package dev.orangepie.details.ui.model

sealed class PodcastDetailsUIState {
    object Loading : PodcastDetailsUIState()
    object Error : PodcastDetailsUIState()
    data class Details(
        val details: PodcastDetailsUIModel,
    ) : PodcastDetailsUIState()
}
