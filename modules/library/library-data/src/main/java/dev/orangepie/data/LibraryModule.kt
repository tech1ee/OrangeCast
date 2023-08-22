package dev.orangepie.data

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.orangepie.data.db.LibraryDao
import dev.orangepie.data.db.LibraryDatabase
import dev.orangepie.data.repository.PodcastLibraryRepository
import dev.orangepie.data.repository.PodcastLibraryRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class LibraryModule {

    @Binds
    abstract fun bindPodcastLibraryRepository(impl: PodcastLibraryRepositoryImpl): PodcastLibraryRepository

    companion object {
        @Provides
        fun provideLibraryDao(@ApplicationContext context: Context): LibraryDao {
            return Room
                .databaseBuilder(context, LibraryDatabase::class.java, "podcasts.db")
                .build()
                .podcastDao()
        }
    }
}