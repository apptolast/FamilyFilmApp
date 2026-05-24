package com.apptolast.familyfilmapp.ads

import android.app.Activity
import android.content.Context
import com.apptolast.familyfilmapp.firebase.CrashReporter
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import java.util.Date

class AndroidAppOpenAdManager(
    context: Context,
    private val crashReporter: CrashReporter,
) {
    private val appContext = context.applicationContext
    private val consentManager = GoogleMobileAdsConsentManager.getInstance(appContext)

    private var appOpenAd: AppOpenAd? = null
    private var isLoadingAd = false
    var isShowingAd = false
        private set
    var adsRemoved: Boolean = false
    private var loadTime: Long = 0

    fun loadAd() {
        if (adsRemoved) {
            crashReporter.log("Android app-open ad load skipped: ads removed")
            return
        }
        if (isLoadingAd || isAdAvailable()) {
            crashReporter.log(
                "Android app-open ad load skipped: loading=$isLoadingAd available=${isAdAvailable()}",
            )
            return
        }
        if (!consentManager.canRequestAds) {
            crashReporter.log("Android app-open ad load skipped: consent not granted")
            return
        }

        val adUnitId = AndroidAdUnitIds.appOpen()
        if (adUnitId.isBlank()) {
            crashReporter.recordException(IllegalStateException("Android app-open ad unit id is blank"))
            return
        }

        isLoadingAd = true
        crashReporter.log(
            "Android app-open ad load requested source=${AndroidAdUnitIds.source()} " +
                "suffix=${AndroidAdUnitIds.suffixSafe(adUnitId)}",
        )

        AppOpenAd.load(
            appContext,
            adUnitId,
            AdRequest.Builder().build(),
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    isLoadingAd = false
                    loadTime = Date().time
                    crashReporter.log("Android app-open ad loaded")
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    isLoadingAd = false
                    crashReporter.recordException(
                        RuntimeException(
                            "Android app-open ad failed code=${error.code} domain=${error.domain} " +
                                "message=${error.message} suffix=${AndroidAdUnitIds.suffixSafe(adUnitId)}",
                        ),
                    )
                }
            },
        )
    }

    fun showAdIfAvailable(activity: Activity) {
        crashReporter.log(
            "Android app-open show requested showing=$isShowingAd available=${isAdAvailable()} " +
                "canRequestAds=${consentManager.canRequestAds} adsRemoved=$adsRemoved",
        )

        if (adsRemoved || isShowingAd) return
        if (!isAdAvailable()) {
            loadAd()
            return
        }

        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                appOpenAd = null
                isShowingAd = false
                crashReporter.log("Android app-open ad dismissed")
                loadAd()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                appOpenAd = null
                isShowingAd = false
                crashReporter.recordException(
                    RuntimeException(
                        "Android app-open ad failed to show code=${adError.code} " +
                            "domain=${adError.domain} message=${adError.message}",
                    ),
                )
                loadAd()
            }

            override fun onAdShowedFullScreenContent() {
                crashReporter.log("Android app-open ad shown")
            }

            override fun onAdImpression() {
                crashReporter.log("Android app-open ad impression")
            }
        }

        isShowingAd = true
        appOpenAd?.show(activity)
    }

    private fun isAdAvailable(): Boolean =
        appOpenAd != null && Date().time - loadTime < FOUR_HOURS_IN_MILLIS

    private companion object {
        const val FOUR_HOURS_IN_MILLIS = 4 * 60 * 60 * 1000L
    }
}
