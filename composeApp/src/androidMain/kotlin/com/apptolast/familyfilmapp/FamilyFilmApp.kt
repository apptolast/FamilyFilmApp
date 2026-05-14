package com.apptolast.familyfilmapp

import android.app.Application
import com.apptolast.familyfilmapp.di.initKoin
import org.koin.android.ext.koin.androidContext

// Application class — entry point for Android-side initialisation.
// Firebase auto-initialises through its own ContentProvider (google-services
// plugin + composeApp/google-services.json), so we don't call initializeApp()
// here. Block 10 will hook the App Check provider factory; blocks 14/15 will
// add AdMob / RevenueCat / UMP consent setup.
class FamilyFilmApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@FamilyFilmApp)
        }
    }
}
