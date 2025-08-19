package dev.orangecast.shared.di

import android.content.Context
import dev.orangecast.shared.data.config.ApiKeyProvider
import dev.orangecast.shared.data.database.DatabaseFactory
import dev.orangecast.shared.domain.player.AudioPlayer
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

actual val platformModule = module {
    single<HttpClient> {
        HttpClient(OkHttp) {
            // Engine configuration
            engine {
                config {
                    connectTimeout(30, TimeUnit.SECONDS)
                    readTimeout(30, TimeUnit.SECONDS)
                    followRedirects(true)
                    followSslRedirects(true)
                }
            }
            
            // Install common plugins from createCachedHttpClient
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
            
            // Enable HTTP client logging for debugging
            install(io.ktor.client.plugins.logging.Logging) {
                logger = object : io.ktor.client.plugins.logging.Logger {
                    override fun log(message: String) {
                        io.github.aakira.napier.Napier.d(message, tag = "HttpClient")
                    }
                }
                level = io.ktor.client.plugins.logging.LogLevel.ALL
            }
            
            defaultRequest {
                header("Accept", "application/xml, application/rss+xml, text/xml, application/json, */*")
                header("User-Agent", "OrangeCast/1.0 (compatible; podcast client)")
            }
            
            expectSuccess = false  // Handle errors manually for better debugging
        }
    }
    
    // Audio Player
    single { AudioPlayer(androidContext()) }
    
    // Database
    single { DatabaseFactory(androidContext()) }
    
    // API Key Provider
    single<ApiKeyProvider> {
        object : ApiKeyProvider {
            override fun getListenNotesApiKey(): String = "f9cfc7b4369d4ecbb285a385da034fd0"
            override fun getPodcastIndexApiKey(): String = ""
            override fun getPodcastIndexApiSecret(): String = ""
        }
    }
}