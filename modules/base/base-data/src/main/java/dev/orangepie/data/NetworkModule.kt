package dev.orangepie.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.orangepie.data.interceptor.ListenNotesAuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    const val ITUNES = "iTunes"
    const val LISTEN_NOTES = "ListenNotes"
    const val PODCAST_RSS_FEED = "PodcastRSSFeed"
    private const val ITUNES_URL = "https://itunes.apple.com/"
    private const val LISTEN_NOTES_URL = "https://listen-api.listennotes.com/api/v2/"

    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(getLoggingInterceptor())
            .addInterceptor(ListenNotesAuthInterceptor())
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.MINUTES)
            .build()
    }

    @Provides
    internal fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Named(ITUNES)
    @Provides
    internal fun provideRetrofitInterfaceITunes(client: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ITUNES_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi).withNullSerialization())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
    }

    @Named(LISTEN_NOTES)
    @Provides
    internal fun provideRetrofitInterfaceListenNotes(client: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl(LISTEN_NOTES_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi).withNullSerialization())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
    }

    @Named(PODCAST_RSS_FEED)
    @Provides
    internal fun provideRetrofitInterfacePodcastRSSFeed(client: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl(LISTEN_NOTES_URL)
            .client(client)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
    }

    fun getLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

}