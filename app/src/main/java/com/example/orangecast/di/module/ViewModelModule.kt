package com.example.orangecast.di.module

import com.example.orangecast.interactor.FeedInteractor
import com.example.orangecast.interactor.GenresInteractor
import com.example.orangecast.view.channeldetails.ChannelDetailsViewModel
import com.example.orangecast.view.discover.DiscoverViewModel
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ViewModelModule {

    @Singleton
    @Provides
    fun provideDiscoverViewModel(interactor: GenresInteractor): DiscoverViewModel {
        return DiscoverViewModel(interactor)
    }

    @Singleton
    @Provides
    fun provideChannelDetailsViewModel(feedInteractor: FeedInteractor): ChannelDetailsViewModel {
        return ChannelDetailsViewModel(feedInteractor)
    }
}