package dev.orangepie.details.data

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.orangepie.data.NetworkModule
import dev.orangepie.details.data.api.PodcastDetailsApi
import dev.orangepie.details.data.repository.PodcastDetailsRepository
import dev.orangepie.details.data.repository.PodcastDetailsRepositoryImpl
import dev.orangepie.details.data.repository.PodcastRSSFeedRepository
import dev.orangepie.details.data.repository.PodcastRSSFeedRepositoryImpl
import retrofit2.Retrofit
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
abstract class PodcastDetailsModule {

    @Binds
    abstract fun bindPodcastDetailsRepository(impl: PodcastDetailsRepositoryImpl): PodcastDetailsRepository

    @Binds
    abstract fun bindPodcastRSSFeedRepository(impl: PodcastRSSFeedRepositoryImpl): PodcastRSSFeedRepository

    companion object {
        @Provides
        fun providePodcastDetailsApi(@Named(NetworkModule.ITUNES) retrofit: Retrofit): PodcastDetailsApi {
            return retrofit.create(PodcastDetailsApi::class.java)
        }
    }
}