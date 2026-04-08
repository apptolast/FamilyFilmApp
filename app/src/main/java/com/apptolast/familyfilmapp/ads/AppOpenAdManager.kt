package com.apptolast.familyfilmapp.ads

import android.app.Activity
import android.content.Context
import com.apptolast.familyfilmapp.BuildConfig
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import timber.log.Timber
import java.util.Date

/**
 * Manages loading and showing App Open Ads.
 * Follows Google's official example with consent gating:
 * https://github.com/googleads/googleads-mobile-android-examples/tree/main/kotlin/admob/AppOpenExample
 */
class AppOpenAdManager(private val context: Context) {

    private val consentManager = GoogleMobileAdsConsentManager.getInstance(context)
    private var appOpenAd: AppOpenAd? = null
    private var isLoadingAd = false
    var isShowingAd = false
        private set
    var adsRemoved: Boolean = false
    private var loadTime: Long = 0

    fun loadAd() {
        if (adsRemoved) {
            Timber.d("$TAG loadAd() skip — ads removed by purchase")
            return
        }
        if (isLoadingAd || isAdAvailable()) {
            Timber.d("$TAG loadAd() skip — loading=$isLoadingAd, available=${isAdAvailable()}")
            return
        }

        if (!consentManager.canRequestAds) {
            Timber.w("$TAG loadAd() skip — consent not granted (canRequestAds=false)")
            return
        }

        isLoadingAd = true
        val adUnitId = if (BuildConfig.DEBUG) TEST_AD_UNIT_ID else BuildConfig.ADMOB_APP_OPEN_ID
        Timber.d("$TAG loadAd() START — unit=...${adUnitId.takeLast(12)}")

        AppOpenAd.load(
            context,
            adUnitId,
            AdRequest.Builder().build(),
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    isLoadingAd = false
                    loadTime = Date().time
                    Timber.d("$TAG onAdLoaded — ad ready")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    isLoadingAd = false
                    Timber.e(
                        "$TAG onAdFailedToLoad — code=${loadAdError.code} " +
                            "msg=${loadAdError.message}",
                    )
                }
            },
        )
    }

    fun showAdIfAvailable(activity: Activity) {
        Timber.d(
            "$TAG showAdIfAvailable — showing=$isShowingAd, " +
                "available=${isAdAvailable()}, " +
                "canRequestAds=${consentManager.canRequestAds}, " +
                "activity=${activity.javaClass.simpleName}",
        )

        if (adsRemoved) {
            Timber.d("$TAG showAdIfAvailable skip — ads removed by purchase")
            return
        }

        if (isShowingAd) {
            Timber.d("$TAG skip — already showing")
            return
        }

        if (!isAdAvailable()) {
            Timber.d("$TAG no ad available — loading for next time")
            loadAd()
            return
        }

        Timber.d("$TAG showing ad now")

        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Timber.d("$TAG onAdDismissed — user closed ad")
                appOpenAd = null
                isShowingAd = false
                loadAd()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Timber.e("$TAG onAdFailedToShow — code=${adError.code} msg=${adError.message}")
                appOpenAd = null
                isShowingAd = false
                loadAd()
            }

            override fun onAdShowedFullScreenContent() {
                Timber.d("$TAG onAdShowed — ad visible")
            }

            override fun onAdClicked() {
                Timber.d("$TAG onAdClicked")
            }

            override fun onAdImpression() {
                Timber.d("$TAG onAdImpression")
            }
        }

        isShowingAd = true
        appOpenAd?.show(activity)
    }

    private fun isAdAvailable(): Boolean = appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)

    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference: Long = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 3_600_000
        return dateDifference < numMilliSecondsPerHour * numHours
    }

    companion object {
        private const val TAG = "AppOpenAd"
        private const val TEST_AD_UNIT_ID = "ca-app-pub-3940256099942544/9257395921"
    }
}
