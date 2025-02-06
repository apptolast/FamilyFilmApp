package com.apptolast.familyfilmapp.di

import com.apptolast.familyfilmapp.utils.DefaultDispatcherProvider
import com.apptolast.familyfilmapp.utils.DispatcherProvider
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Provides
    fun provideDispatcherProvider(): DispatcherProvider = DefaultDispatcherProvider()
}
