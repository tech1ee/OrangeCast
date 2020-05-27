package com.example.orangecast.di

import com.example.data.di.ApiModule
import com.example.data.di.DatabaseModule
import com.example.data.di.RepositoryModule
import com.example.data.di.UtilsModule
import com.example.orangecast.App
import com.example.orangecast.di.module.*
import com.example.orangecast.ui.artist.ArtistFragment
import com.example.orangecast.ui.discover.DiscoverFragment
import com.example.orangecast.ui.library.LibraryFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, ApiModule::class, InteractorModule::class,
    DatabaseModule::class, RepositoryModule::class, ViewModelModule::class, UtilsModule::class])
interface AppComponent {

    fun inject(app: App)

    fun inject(fragment: DiscoverFragment)

    fun inject(fragment: ArtistFragment)

    fun inject(fragment: LibraryFragment)

}