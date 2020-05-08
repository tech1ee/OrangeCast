package com.example.data.di

import com.example.data.utils.XmlParser
import dagger.Module
import dagger.Provides

@Module
class UtilsModule {

    @Provides
    fun provideXmlParser(): XmlParser {
        return XmlParser()
    }
}