package dev.orangecast.shared.domain.usecase

import dev.orangecast.shared.domain.model.Genre
import dev.orangecast.shared.domain.repository.PodcastRepository

class GetGenresUseCase(
    private val repository: PodcastRepository
) {
    suspend fun getGenres(): List<Genre> {
        return listOf(
            Genre(1, "Arts"),
            Genre(2, "Business"), 
            Genre(3, "Comedy"),
            Genre(4, "Education"),
            Genre(5, "Fiction"),
            Genre(6, "Government"),
            Genre(7, "Health & Fitness"),
            Genre(8, "History"),
            Genre(9, "Kids & Family"),
            Genre(10, "Leisure"),
            Genre(11, "Music"),
            Genre(12, "News"),
            Genre(13, "Religion & Spirituality"),
            Genre(14, "Science"),
            Genre(15, "Society & Culture"),
            Genre(16, "Sports"),
            Genre(17, "Technology"),
            Genre(18, "True Crime"),
            Genre(19, "TV & Film")
        )
    }
}