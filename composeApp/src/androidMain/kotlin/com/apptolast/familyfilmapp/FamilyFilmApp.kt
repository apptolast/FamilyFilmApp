package com.apptolast.familyfilmapp

import android.app.Application
import com.apptolast.familyfilmapp.di.initKoin
import com.apptolast.familyfilmapp.firebase.installAppCheckProvider
import com.google.android.gms.ads.MobileAds
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.crashlytics.crashlytics
import org.koin.android.ext.koin.androidContext

/**
 * Application entry point on Android.
 *
 * Firebase auto-initialises through its own ContentProvider, so we only
 * need to wire Crashlytics gating + the native App Check provider here.
 * AdMob is started eagerly so the first banner / native ad load doesn't
 * pay the SDK initialisation cost. RevenueCat is configured lazily by
 * [PurchaseManager.initialize] once a user logs in (so the SDK binds the
 * Firebase UID as RevenueCat appUserId).
 */
class FamilyFilmApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@FamilyFilmApp)
        }

        val isDebug = (applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0
        Firebase.crashlytics.setCrashlyticsCollectionEnabled(!isDebug)
        installAppCheckProvider(debug = isDebug)

        // AdMob SDK init. UMP consent flow remains a TODO — for now we
        // trust the device's default consent state; block 14b can wire
        // GoogleMobileAdsConsentManager later.
        MobileAds.initialize(this) { /* completion no-op */ }
    }
}
