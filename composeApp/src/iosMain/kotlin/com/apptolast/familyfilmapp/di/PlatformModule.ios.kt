package com.apptolast.familyfilmapp.di

import androidx.room.RoomDatabase
import com.apptolast.familyfilmapp.ads.IosNativeAdManager
import com.apptolast.familyfilmapp.ads.NativeAdManager
import com.apptolast.familyfilmapp.auth.AppleSignInClient
import com.apptolast.familyfilmapp.auth.GoogleSignInClient
import com.apptolast.familyfilmapp.auth.IosAppleSignInClient
import com.apptolast.familyfilmapp.auth.IosGoogleSignInClient
import com.apptolast.familyfilmapp.purchases.IosRevenueCatPurchaseManager
import com.apptolast.familyfilmapp.purchases.PurchaseManager
import com.apptolast.familyfilmapp.rating.RateAppManager
import com.apptolast.familyfilmapp.rating.StoreKitRateAppManager
import com.apptolast.familyfilmapp.room.AppDatabase
import com.apptolast.familyfilmapp.room.getDatabaseBuilder
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults

actual val platformModule = module {
    single<HttpClientEngine> { Darwin.create() }
    single<Settings> { NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults) }
    single<RoomDatabase.Builder<AppDatabase>> { getDatabaseBuilder() }

    singleOf(::StoreKitRateAppManager) bind RateAppManager::class
    singleOf(::IosGoogleSignInClient) bind GoogleSignInClient::class
    singleOf(::IosAppleSignInClient) bind AppleSignInClient::class
    singleOf(::IosRevenueCatPurchaseManager) bind PurchaseManager::class
    // IosNativeAdManager delegates the actual GADAdLoader call to Swift via NativeAdBridge.
    single<NativeAdManager> { IosNativeAdManager(get()) }
}
