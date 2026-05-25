package com.apptolast.familyfilmapp

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.apptolast.familyfilmapp.ads.AndroidAppOpenAdManager
import com.apptolast.familyfilmapp.ads.GoogleMobileAdsConsentManager
import com.apptolast.familyfilmapp.ads.NativeAdManager
import com.apptolast.familyfilmapp.analytics.AnalyticsTracker
import com.apptolast.familyfilmapp.di.initKoin
import com.apptolast.familyfilmapp.firebase.installAppCheckProvider
import com.apptolast.familyfilmapp.purchases.PurchaseManager
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.crashlytics.crashlytics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.atomic.AtomicBoolean

class FamilyFilmApp :
    Application(),
    Application.ActivityLifecycleCallbacks,
    KoinComponent {
    private val isMobileAdsInitializeCalled = AtomicBoolean(false)
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val appOpenAdManager: AndroidAppOpenAdManager by inject()
    private val nativeAdManager: NativeAdManager by inject()
    private val purchaseManager: PurchaseManager by inject()

    private var activityReferences = 0
    private var isActivityChangingConfigurations = false

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@FamilyFilmApp)
        }

        val isDebug = (applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0
        Firebase.crashlytics.setCrashlyticsCollectionEnabled(!isDebug)
        installAppCheckProvider(debug = isDebug)
        registerActivityLifecycleCallbacks(this)

        applicationScope.launch {
            purchaseManager.hasRemovedAds.collectLatest { removed ->
                appOpenAdManager.adsRemoved = removed
                Firebase.crashlytics.log("Android ads removed state changed: $removed")
            }
        }
    }

    fun gatherConsentAndInitializeAds(activity: Activity, analyticsTracker: AnalyticsTracker) {
        if (purchaseManager.hasRemovedAds.value) {
            Firebase.crashlytics.log("AdMob initialization skipped: ads removed")
            return
        }

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
            appOpenAdManager.adsRemoved = purchaseManager.hasRemovedAds.value
            appOpenAdManager.loadAd()
            nativeAdManager.loadAds()
        }
    }

    override fun onActivityStarted(activity: Activity) {
        if (!appOpenAdManager.isShowingAd) {
            appOpenAdManager.adsRemoved = purchaseManager.hasRemovedAds.value
        }
        if (++activityReferences == 1 && !isActivityChangingConfigurations) {
            appOpenAdManager.showAdIfAvailable(activity)
        }
    }

    override fun onActivityStopped(activity: Activity) {
        isActivityChangingConfigurations = activity.isChangingConfigurations
        activityReferences--
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit
    override fun onActivityResumed(activity: Activity) = Unit
    override fun onActivityPaused(activity: Activity) = Unit
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
    override fun onActivityDestroyed(activity: Activity) = Unit

    private companion object {
        const val TEST_DEVICE_ID = "6EBCD242716D331EAA6673852DA6C4FA"
    }
}
