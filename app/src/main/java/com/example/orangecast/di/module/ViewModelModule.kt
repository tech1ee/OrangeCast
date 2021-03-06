package com.example.orangecast.di.module

import com.example.orangecast.interactor.ArtistInteractor
import com.example.orangecast.interactor.GenresInteractor
import com.example.orangecast.interactor.SubscriptionInteractor
import com.example.orangecast.ui.artist.ArtistViewModel
import com.example.orangecast.ui.discover.DiscoverViewModel
import com.example.orangecast.ui.library.LibraryViewModel
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
    fun provideChannelDetailsViewModel(channelInteractor: ArtistInteractor,
        subscriptionInteractor: SubscriptionInteractor
    ): ArtistViewModel {
        return ArtistViewModel(channelInteractor, subscriptionInteractor)
    }

    @Singleton
    @Provides
    fun provideLibraryViewModel(subscriptionInteractor: SubscriptionInteractor): LibraryViewModel {
        return LibraryViewModel(subscriptionInteractor)
    }
}