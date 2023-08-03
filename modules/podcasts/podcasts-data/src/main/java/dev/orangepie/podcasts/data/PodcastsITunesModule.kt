package dev.orangepie.podcasts.data

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.orangepie.data.NetworkModule
import dev.orangepie.podcasts.data.api.PodcastsITunesApi
import dev.orangepie.podcasts.data.api.PodcastsListenNotesApi
import dev.orangepie.podcasts.data.repository.PodcastsRepository
import dev.orangepie.podcasts.data.repository.PodcastsRepositoryImpl
import retrofit2.Retrofit
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
abstract class PodcastsITunesModule {

    @Binds
    abstract fun bindPodcastsRepository(impl: PodcastsRepositoryImpl): PodcastsRepository

    companion object {
        @Provides
        fun providePodcastsITunesApi(@Named(NetworkModule.ITUNES) retrofit: Retrofit): PodcastsITunesApi {
            return retrofit.create(PodcastsITunesApi::class.java)
        }

        @Provides
        fun providePodcastsListenNotesApi(@Named(NetworkModule.LISTEN_NOTES) retrofit: Retrofit): PodcastsListenNotesApi {
            return retrofit.create(PodcastsListenNotesApi::class.java)
        }
    }
}