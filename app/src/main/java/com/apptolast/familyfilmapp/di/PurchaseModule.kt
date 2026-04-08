package com.apptolast.familyfilmapp.di

import android.content.Context
import com.apptolast.familyfilmapp.purchases.PurchaseManager
import com.apptolast.familyfilmapp.purchases.RevenueCatPurchaseManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PurchaseModule {

    @Provides
    @Singleton
    fun providePurchaseManager(@ApplicationContext context: Context): PurchaseManager =
        RevenueCatPurchaseManager(context)
}
