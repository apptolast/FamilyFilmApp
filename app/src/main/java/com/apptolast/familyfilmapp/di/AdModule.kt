package com.apptolast.familyfilmapp.di

import android.content.Context
import com.apptolast.familyfilmapp.ads.AppOpenAdManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AdModule {

    @Provides
    @Singleton
    fun provideAppOpenAdManager(@ApplicationContext context: Context): AppOpenAdManager = AppOpenAdManager(context)
}
