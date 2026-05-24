package com.apptolast.familyfilmapp

import android.app.Activity
import android.app.Application
import com.apptolast.familyfilmapp.ads.GoogleMobileAdsConsentManager
import com.apptolast.familyfilmapp.analytics.AnalyticsTracker
import com.apptolast.familyfilmapp.di.initKoin
import com.apptolast.familyfilmapp.firebase.installAppCheckProvider
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.crashlytics.crashlytics
import org.koin.android.ext.koin.androidContext
import java.util.concurrent.atomic.AtomicBoolean

class FamilyFilmApp : Application() {
    private val isMobileAdsInitializeCalled = AtomicBoolean(false)

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@FamilyFilmApp)
        }

        val isDebug = (applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0
        Firebase.crashlytics.setCrashlyticsCollectionEnabled(!isDebug)
        installAppCheckProvider(debug = isDebug)
    }

    fun gatherConsentAndInitializeAds(activity: Activity, analyticsTracker: AnalyticsTracker) {
        val consentManager = GoogleMobileAdsConsentManager.getInstance(applicationContext)
        consentManager.gatherConsent(activity) { consentError ->
            if (consentError != null) {
                Firebase.crashlytics.log(
                    "AdMob consent error code=${consentError.errorCode} message=${consentError.message}",
                )
            }

            analyticsTracker.setConsent(
                analyticsGranted = consentManager.canRequestAds,
                adsGranted = consentManager.canRequestAds,
            )

            if (consentManager.canRequestAds) {
                initializeMobileAdsSdk()
            }
        }

        if (consentManager.canRequestAds) {
            analyticsTracker.setConsent(analyticsGranted = true, adsGranted = true)
            initializeMobileAdsSdk()
        }
    }

    private fun initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) return

        val isDebug = (applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0
        if (isDebug) {
            MobileAds.setRequestConfiguration(
                RequestConfiguration.Builder()
                    .setTestDeviceIds(listOf(TEST_DEVICE_ID))
                    .build(),
            )
        }

        MobileAds.initialize(this) { initStatus ->
            val adapters = initStatus.adapterStatusMap.entries.joinToString {
                "${it.key}: ${it.value.initializationState}"
            }
            Firebase.crashlytics.log("AdMob MobileAds initialized: $adapters")
        }
    }

    private companion object {
        const val TEST_DEVICE_ID = "6EBCD242716D331EAA6673852DA6C4FA"
    }
}
