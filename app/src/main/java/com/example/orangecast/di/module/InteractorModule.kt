package com.example.orangecast.di.module

import com.example.data.repository.FeedRepository
import com.example.data.repository.SearchRepository
import com.example.orangecast.interactor.ChannelInteractor
import com.example.orangecast.interactor.GenresInteractor
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class InteractorModule {

    @Singleton
    @Provides
    fun provideDiscoverInteractor(repository: SearchRepository): GenresInteractor {
        return GenresInteractor(repository)
    }

    @Singleton
    @Provides
    fun provideChannelInteractor(searchRepository: SearchRepository, feedRepository: FeedRepository): ChannelInteractor {
        return ChannelInteractor(searchRepository, feedRepository)
    }
}