package dev.orangepie.genres.data

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.orangepie.data.NetworkModule
import dev.orangepie.genres.data.api.PodcastGenresListenNotesApi
import dev.orangepie.genres.data.repository.PodcastsGenresRepository
import dev.orangepie.genres.data.repository.PodcastsGenresRepositoryImpl
import retrofit2.Retrofit
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
abstract class PodcastGenresModule {

    @Binds
    abstract fun bindPodcastGenresRepository(impl: PodcastsGenresRepositoryImpl): PodcastsGenresRepository

    companion object {
        @Provides
        fun providePodcastsListenNotesApi(@Named(NetworkModule.LISTEN_NOTES) retrofit: Retrofit): PodcastGenresListenNotesApi {
            return retrofit.create(PodcastGenresListenNotesApi::class.java)
        }
    }
}