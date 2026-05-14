package com.apptolast.familyfilmapp

import android.app.Application
import com.apptolast.familyfilmapp.di.initKoin
import com.apptolast.familyfilmapp.firebase.installAppCheckProvider
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.crashlytics.crashlytics
import org.koin.android.ext.koin.androidContext

// Application class — entry point for Android-side initialisation.
// Firebase auto-initialises through its own ContentProvider (google-services
// plugin + composeApp/google-services.json), so we don't call initializeApp()
// here. Blocks 14/15 will add AdMob / RevenueCat / UMP consent setup.
class FamilyFilmApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@FamilyFilmApp)
        }

        val isDebug = (applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0
        // Mirror the legacy gating: capture crashes in release, stay quiet in debug
        Firebase.crashlytics.setCrashlyticsCollectionEnabled(!isDebug)
        installAppCheckProvider(debug = isDebug)
    }
}
