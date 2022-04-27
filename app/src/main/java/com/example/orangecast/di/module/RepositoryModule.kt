package com.example.orangecast.di.module

import com.example.orangecast.data.itunes.ITunesRepository
import com.example.orangecast.data.itunes.ITunesRepositoryImpl
import com.example.orangecast.data.listennotes.ListenNotesRepository
import com.example.orangecast.data.listennotes.ListenNotesRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface RepositoryModule {

    @Binds
    fun provideListenNotesRepository(listenNotesRepository: ListenNotesRepositoryImpl): ListenNotesRepository

    @Binds
    fun provideITunesRepository(iTunesRepository: ITunesRepositoryImpl): ITunesRepository
}