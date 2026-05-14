package com.apptolast.familyfilmapp

import android.app.Application
import com.apptolast.familyfilmapp.di.initKoin
import com.apptolast.familyfilmapp.firebase.installAppCheckProvider
import com.google.android.gms.ads.MobileAds
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.crashlytics.crashlytics
import org.koin.android.ext.koin.androidContext

class FamilyFilmApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@FamilyFilmApp)
        }

        val isDebug = (applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0
        Firebase.crashlytics.setCrashlyticsCollectionEnabled(!isDebug)
        installAppCheckProvider(debug = isDebug)

        // TODO: wire UMP consent flow (GoogleMobileAdsConsentManager).
        MobileAds.initialize(this) { /* completion no-op */ }
    }
}
