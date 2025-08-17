package dev.orangecast.shared.di

import android.content.Context
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
            
            // Production - no logging
            // if (enableLogging) {
            //     install(Logging) { level = LogLevel.INFO }
            // }
            
            defaultRequest {
                header("Accept", "application/xml, application/rss+xml, text/xml, application/json, */*")
                header("User-Agent", "OrangeCast/1.0 (compatible; podcast client)")
            }
            
            expectSuccess = true
        }
    }
    
    // Audio Player
    single { AudioPlayer(androidContext()) }
    
    // Database
    single { DatabaseFactory(androidContext()) }
}