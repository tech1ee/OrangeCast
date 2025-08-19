package dev.orangecast.shared.di

import dev.orangecast.shared.data.api.PodcastIndexApiService
import dev.orangecast.shared.data.api.ListenNotesApiService
import dev.orangecast.shared.data.config.ApiKeyProvider
import dev.orangecast.shared.data.cache.PodcastCacheManager
import dev.orangecast.shared.data.database.DatabaseFactory
import dev.orangecast.shared.data.database.DatabaseRepository
import dev.orangecast.shared.data.repository.PodcastRepositoryImpl
import dev.orangecast.shared.data.rss.RssFeedParser
import dev.orangecast.shared.domain.repository.PodcastRepository
import dev.orangecast.shared.domain.usecase.GetPodcastDetailsUseCase
import dev.orangecast.shared.domain.usecase.SearchPodcastsUseCase
import dev.orangecast.shared.domain.usecase.GetNewEpisodesUseCase
import dev.orangecast.shared.domain.usecase.GetSubscribedPodcastsUseCase
import dev.orangecast.shared.domain.usecase.SubscribeToPodcastUseCase
import dev.orangecast.shared.domain.usecase.UnsubscribeFromPodcastUseCase
import dev.orangecast.shared.domain.usecase.PlayerUseCase
import dev.orangecast.shared.domain.usecase.GetGenresUseCase
import dev.orangecast.shared.presentation.viewmodel.NewEpisodesViewModel
import dev.orangecast.shared.presentation.viewmodel.PodcastDetailViewModel
import dev.orangecast.shared.presentation.viewmodel.LibraryViewModel
import dev.orangecast.shared.presentation.viewmodel.DiscoverViewModel
import org.koin.core.module.Module
import org.koin.dsl.module

expect val platformModule: Module

val sharedModule = module {
    includes(platformModule)
    
    // HTTP Client is provided by platform modules with proper configuration
    
    // Database
    single { get<DatabaseFactory>().createDatabase() }
    single { DatabaseRepository(get()) }
    
    single { PodcastCacheManager() }
    
    // API Services
    single { 
        PodcastIndexApiService(
            httpClient = get(),
            apiKey = get<ApiKeyProvider>().getPodcastIndexApiKey(),
            apiSecret = get<ApiKeyProvider>().getPodcastIndexApiSecret()
        )
    }
    single { 
        ListenNotesApiService(
            httpClient = get(),
            apiKey = get<ApiKeyProvider>().getListenNotesApiKey()
        )
    }
    single { RssFeedParser(get()) }
    
    
    // Repositories
    single<PodcastRepository> { 
        PodcastRepositoryImpl(
            apiService = get<ListenNotesApiService>(),
            rssFeedParser = get<RssFeedParser>(),
            cacheManager = get<PodcastCacheManager>(),
            databaseRepository = get<DatabaseRepository>()
        )
    }
    
    // Use Cases
    single { SearchPodcastsUseCase(get()) }
    single { GetPodcastDetailsUseCase(get()) }
    single { GetNewEpisodesUseCase(get(), get()) }
    single { GetSubscribedPodcastsUseCase(get()) }
    single { SubscribeToPodcastUseCase(get()) }
    single { UnsubscribeFromPodcastUseCase(get()) }
    single { PlayerUseCase(get()) }
    single { GetGenresUseCase(get(), get()) }
    
    // ViewModels
    single { dev.orangecast.shared.presentation.viewmodel.PodcastListViewModel(get(), get()) }
    single { DiscoverViewModel(get(), get()) }
    single { NewEpisodesViewModel(get(), get()) }
    single { PodcastDetailViewModel(get(), get(), get()) }
    single { LibraryViewModel(get()) }
}