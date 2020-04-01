package com.example.orangecast.di.module

import com.example.orangecast.data.repository.FeedRepository
import com.example.orangecast.data.repository.SearchRepository
import com.example.orangecast.interactor.FeedInteractor
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
    fun providesRSSInteractor(repository: FeedRepository): FeedInteractor {
        return FeedInteractor(repository)
    }
}