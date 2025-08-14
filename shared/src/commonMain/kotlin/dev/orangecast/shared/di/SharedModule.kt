package dev.orangecast.shared.di

import dev.orangecast.shared.data.api.ITunesApiService
import dev.orangecast.shared.data.cache.PodcastCacheManager
import dev.orangecast.shared.data.local.LocalStorageManager
import dev.orangecast.shared.data.repository.PodcastRepositoryImpl
import dev.orangecast.shared.data.rss.RssFeedParser
import dev.orangecast.shared.domain.repository.PodcastRepository
import dev.orangecast.shared.domain.usecase.GetPodcastDetailsUseCase
import dev.orangecast.shared.domain.usecase.SearchPodcastsUseCase
import dev.orangecast.shared.domain.usecase.GetNewEpisodesUseCase
import dev.orangecast.shared.domain.usecase.SubscribeToPodcastUseCase
import dev.orangecast.shared.domain.usecase.UnsubscribeFromPodcastUseCase
import dev.orangecast.shared.presentation.viewmodel.NewEpisodesViewModel
import dev.orangecast.shared.presentation.viewmodel.PodcastDetailViewModel
import dev.orangecast.shared.presentation.viewmodel.LibraryViewModel
import org.koin.core.module.Module
import org.koin.dsl.module

expect val platformModule: Module

val sharedModule = module {
    includes(platformModule)
    
    // HTTP Client is provided by platform modules with proper configuration
    
    // Cache Manager
    single { PodcastCacheManager() }
    
    // API Services
    single { ITunesApiService(get()) }
    single { RssFeedParser(get()) }
    
    // Local Storage
    single { LocalStorageManager() }
    
    // Repositories
    single<PodcastRepository> { PodcastRepositoryImpl(get(), get(), get(), get()) }
    
    // Use Cases
    single { SearchPodcastsUseCase(get()) }
    single { GetPodcastDetailsUseCase(get()) }
    single { GetNewEpisodesUseCase(get()) }
    single { SubscribeToPodcastUseCase(get()) }
    single { UnsubscribeFromPodcastUseCase(get()) }
    
    // ViewModels
    single { dev.orangecast.shared.presentation.viewmodel.PodcastListViewModel(get(), get()) }
    single { NewEpisodesViewModel(get()) }
    single { PodcastDetailViewModel(get(), get(), get()) }
    single { LibraryViewModel(get()) }
}