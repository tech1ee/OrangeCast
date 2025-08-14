package dev.orangecast.shared.di

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

actual val platformModule = module {
    single<HttpClient> {
        HttpClient(Darwin) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    coerceInputValues = true
                })
            }
            
            install(HttpCache) {
                // Configure HTTP caching
            }
            
            install(HttpRequestRetry) {
                retryOnServerErrors(maxRetries = 3)
                retryIf { _, response ->
                    response.status.value >= 500
                }
                exponentialDelay()
            }
            
            expectSuccess = true
        }
    }
}