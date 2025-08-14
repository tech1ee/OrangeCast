package dev.orangecast.shared.data.cache

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

fun createCachedHttpClient(enableLogging: Boolean = false): HttpClient {
    return HttpClient {
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
        
        if (enableLogging) {
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        // Logging implementation for debug builds
                        // Will be provided by platform-specific implementations
                    }
                }
                level = LogLevel.INFO
            }
        }
        
        expectSuccess = true
    }
}