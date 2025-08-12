package dev.orangecast.shared.di

import dev.orangecast.shared.data.api.ITunesApiService
import dev.orangecast.shared.data.repository.PodcastRepositoryImpl
import dev.orangecast.shared.domain.repository.PodcastRepository
import dev.orangecast.shared.domain.usecase.GetPodcastDetailsUseCase
import dev.orangecast.shared.domain.usecase.SearchPodcastsUseCase
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val sharedModule = module {
    
    // Network
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
        }
    }
    
    // API Services
    single { ITunesApiService(get()) }
    
    // Repositories
    single<PodcastRepository> { PodcastRepositoryImpl(get()) }
    
    // Use Cases
    single { SearchPodcastsUseCase(get()) }
    single { GetPodcastDetailsUseCase(get()) }
    
    // ViewModels
    single { dev.orangecast.shared.presentation.viewmodel.PodcastListViewModel(get()) }
}