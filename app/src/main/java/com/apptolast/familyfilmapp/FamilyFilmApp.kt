package com.apptolast.familyfilmapp

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.apptolast.familyfilmapp.ads.AppOpenAdManager
import com.apptolast.familyfilmapp.ads.GoogleMobileAdsConsentManager
import com.apptolast.familyfilmapp.utils.ReleaseTree
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import timber.log.Timber.DebugTree
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

/**
 * Application class for App Open Ads.
 * Uses ActivityLifecycleCallbacks directly to detect foreground transitions
 * instead of ProcessLifecycleOwner (which doesn't dispatch events reliably
 * in Hilt-generated Application subclasses).
 */
@HiltAndroidApp
class FamilyFilmApp :
    Application(),
    Configuration.Provider,
    Application.ActivityLifecycleCallbacks {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setWorkerFactory(workerFactory)
            .build()

    @Inject
    lateinit var appOpenAdManager: AppOpenAdManager

    private var currentActivity: Activity? = null
    private val isMobileAdsInitializeCalled = AtomicBoolean(false)

    // Track app background/foreground state using activity count
    private var activityReferences = 0
    private var isActivityChangingConfigurations = false

    override fun onCreate() {
        super.onCreate()

        Firebase.crashlytics.isCrashlyticsCollectionEnabled = !BuildConfig.DEBUG

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            Timber.plant(ReleaseTree())
        }

        Timber.d("$TAG onCreate — registering ActivityLifecycleCallbacks")
        registerActivityLifecycleCallbacks(this)
    }

    /**
     * Called from MainActivity.onCreate() to gather consent and then initialize ads.
     */
    fun gatherConsentAndInitializeAds(activity: Activity) {
        Timber.d("$TAG gatherConsentAndInitializeAds — starting consent flow")

        val consentManager = GoogleMobileAdsConsentManager.getInstance(applicationContext)
        consentManager.gatherConsent(activity) { consentError ->
            if (consentError != null) {
                Timber.w(
                    "$TAG Consent error: code=${consentError.errorCode} " +
                        "msg=${consentError.message}",
                )
            }

            Timber.d("$TAG Consent resolved — canRequestAds=${consentManager.canRequestAds}")

            if (consentManager.canRequestAds) {
                initializeMobileAdsSdk()
            }
        }

        // Also check if consent was already obtained in a previous session
        if (consentManager.canRequestAds) {
            Timber.d("$TAG canRequestAds already true — initializing SDK immediately")
            initializeMobileAdsSdk()
        }
    }

    private fun initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            Timber.d("$TAG initializeMobileAdsSdk — already called, skipping")
            return
        }

        Timber.d("$TAG Initializing MobileAds SDK...")

        if (BuildConfig.DEBUG) {
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
            Timber.d("$TAG MobileAds SDK initialized — $adapters")
            appOpenAdManager.loadAd()
        }
    }

    // --- ActivityLifecycleCallbacks ---
    // Detect foreground by counting started activities (same technique ProcessLifecycleOwner uses)

    override fun onActivityStarted(activity: Activity) {
        if (!appOpenAdManager.isShowingAd) {
            currentActivity = activity
        }

        if (++activityReferences == 1 && !isActivityChangingConfigurations) {
            // App came to foreground
            Timber.d("$TAG APP FOREGROUND — activity=${activity.javaClass.simpleName}")
            if (!appOpenAdManager.isShowingAd) {
                appOpenAdManager.showAdIfAvailable(activity)
            }
        }
    }

    override fun onActivityStopped(activity: Activity) {
        isActivityChangingConfigurations = activity.isChangingConfigurations
        activityReferences--
        if (activityReferences == 0 && !isActivityChangingConfigurations) {
            Timber.d("$TAG APP BACKGROUND")
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {
        if (currentActivity === activity) {
            currentActivity = null
        }
    }

    companion object {
        private const val TAG = "AppOpenAd:"
        private const val TEST_DEVICE_ID = "6EBCD242716D331EAA6673852DA6C4FA"
    }
}
