package com.apptolast.familyfilmapp.di

import androidx.room.RoomDatabase
import com.apptolast.familyfilmapp.ads.NativeAdManager
import com.apptolast.familyfilmapp.ads.NoOpNativeAdManager
import com.apptolast.familyfilmapp.auth.GoogleSignInClient
import com.apptolast.familyfilmapp.auth.NoOpGoogleSignInClient
import com.apptolast.familyfilmapp.purchases.NoOpPurchaseManager
import com.apptolast.familyfilmapp.purchases.PurchaseManager
import com.apptolast.familyfilmapp.rating.NoOpRateAppManager
import com.apptolast.familyfilmapp.rating.RateAppManager
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

/**
 * iOS-side platform bindings. Block 10 wired the GitLive Firebase stack;
 * block 14 contributes Android equivalents. Until block 15 ships the iOS
 * SPM packages (RevenueCat, GoogleSignIn, Google Mobile Ads), we keep
 * no-op stubs here so the Koin graph resolves on iOS.
 */
actual val platformModule = module {
    single<HttpClientEngine> { Darwin.create() }
    single<Settings> { NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults) }
    single<RoomDatabase.Builder<AppDatabase>> { getDatabaseBuilder() }

    singleOf(::NoOpPurchaseManager) bind PurchaseManager::class
    singleOf(::NoOpGoogleSignInClient) bind GoogleSignInClient::class
    singleOf(::NoOpNativeAdManager) bind NativeAdManager::class
    singleOf(::NoOpRateAppManager) bind RateAppManager::class
}
