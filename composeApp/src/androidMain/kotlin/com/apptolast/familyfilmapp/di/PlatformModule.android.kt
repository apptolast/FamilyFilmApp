package com.apptolast.familyfilmapp.di

import android.content.Context
import androidx.room.RoomDatabase
import com.apptolast.familyfilmapp.ads.AdMobNativeAdManager
import com.apptolast.familyfilmapp.ads.NativeAdManager
import com.apptolast.familyfilmapp.auth.AndroidAppleSignInClient
import com.apptolast.familyfilmapp.auth.AndroidGoogleSignInClient
import com.apptolast.familyfilmapp.auth.AppleSignInClient
import com.apptolast.familyfilmapp.auth.GoogleSignInClient
import com.apptolast.familyfilmapp.platform.CurrentActivityHolder
import com.apptolast.familyfilmapp.purchases.PurchaseManager
import com.apptolast.familyfilmapp.purchases.RevenueCatPurchaseManager
import com.apptolast.familyfilmapp.rating.PlayInAppReviewManager
import com.apptolast.familyfilmapp.rating.RateAppManager
import com.apptolast.familyfilmapp.room.AppDatabase
import com.apptolast.familyfilmapp.room.getDatabaseBuilder
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val platformModule = module {
    single<HttpClientEngine> { OkHttp.create() }
    single<Settings> {
        SharedPreferencesSettings(
            androidContext().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE),
        )
    }
    single<RoomDatabase.Builder<AppDatabase>> { getDatabaseBuilder(androidContext()) }

    // MainActivity registers/unregisters itself in its onCreate/onDestroy.
    single { CurrentActivityHolder() }

    single<GoogleSignInClient> {
        AndroidGoogleSignInClient(
            context = androidContext(),
            activityHolder = get(),
            crashReporter = get(),
        )
    }
    single<AppleSignInClient> {
        AndroidAppleSignInClient(
            activityHolder = get(),
            crashReporter = get(),
        )
    }
    single<RateAppManager> {
        PlayInAppReviewManager(
            context = androidContext(),
            activityHolder = get(),
            crashReporter = get(),
        )
    }
    single<NativeAdManager> { AdMobNativeAdManager(androidContext()) }
    single<PurchaseManager> {
        RevenueCatPurchaseManager(
            context = androidContext(),
            activityHolder = get(),
            crashReporter = get(),
        )
    }
}

private const val SETTINGS_NAME = "ffa_settings"
