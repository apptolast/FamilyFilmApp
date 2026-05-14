package com.apptolast.familyfilmapp.di

import androidx.room.RoomDatabase
import com.apptolast.familyfilmapp.ads.NativeAdManager
import com.apptolast.familyfilmapp.ads.NoOpNativeAdManager
import com.apptolast.familyfilmapp.auth.GoogleSignInClient
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

/**
 * iOS-side platform bindings. After block 15:
 *
 * - [StoreKitRateAppManager] is fully functional — StoreKit ships with
 *   Kotlin/Native's default frameworks, no SPM cinterop needed.
 * - [IosGoogleSignInClient] and [IosRevenueCatPurchaseManager] are
 *   scaffolds that report "no-op" results until the user wires the
 *   GoogleSignIn-iOS / RevenueCat SPM modules in Xcode and exposes
 *   them via cinterop `.def` files (see the README + each class's
 *   KDoc for the exact wiring steps).
 * - [NativeAdManager] stays on the commonMain `NoOpNativeAdManager`
 *   stub — the Home screen renders without native ads on iOS until
 *   the GoogleMobileAds SPM module is added.
 */
actual val platformModule = module {
    single<HttpClientEngine> { Darwin.create() }
    single<Settings> { NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults) }
    single<RoomDatabase.Builder<AppDatabase>> { getDatabaseBuilder() }

    singleOf(::StoreKitRateAppManager) bind RateAppManager::class
    singleOf(::IosGoogleSignInClient) bind GoogleSignInClient::class
    singleOf(::IosRevenueCatPurchaseManager) bind PurchaseManager::class
    singleOf(::NoOpNativeAdManager) bind NativeAdManager::class
}
