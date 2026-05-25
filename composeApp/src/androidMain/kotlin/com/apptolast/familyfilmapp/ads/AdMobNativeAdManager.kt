package com.apptolast.familyfilmapp.ads

import android.content.Context
import android.util.Log
import com.apptolast.familyfilmapp.firebase.CrashReporter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Callers MUST invoke destroyAds() (e.g. from ViewModel.onCleared) to release the NativeAd resources.
class AdMobNativeAdManager(context: Context, private val crashReporter: CrashReporter) : NativeAdManager {

    private val appContext = context.applicationContext

    private val ads = MutableStateFlow<List<NativeAdHandle>>(emptyList())
    override val nativeAds: StateFlow<List<NativeAdHandle>> = ads.asStateFlow()
    private var isLoading = false

    override fun loadAds() {
        if (isLoading) {
            val message = "Android native ads load skipped: already loading"
            Log.d(TAG, message)
            crashReporter.log(message)
            return
        }
        if (ads.value.size >= AD_POOL_SIZE) {
            val message = "Android native ads load skipped: poolSize=${ads.value.size}"
            Log.d(TAG, message)
            crashReporter.log(message)
            return
        }

        val adUnitId = AndroidAdUnitIds.nativeHome()
        if (adUnitId.isBlank()) {
            val exception = IllegalStateException("Android native ad unit id is blank")
            Log.e(TAG, exception.message.orEmpty(), exception)
            crashReporter.recordException(exception)
            return
        }
        isLoading = true
        val requestMessage =
            "Android native ads load requested source=${AndroidAdUnitIds.source()} " +
                "count=$AD_POOL_SIZE suffix=${AndroidAdUnitIds.suffixSafe(adUnitId)}"
        Log.d(TAG, requestMessage)
        crashReporter.log(
            requestMessage,
        )

        val adLoader = AdLoader.Builder(appContext, adUnitId)
            .forNativeAd { nativeAd: NativeAd ->
                ads.update { (it + nativeAd).takeLast(AD_POOL_SIZE) }
                val message =
                    "Android native ad loaded poolSize=${ads.value.size} " +
                        "hasHeadline=${nativeAd.headline != null} hasMedia=${nativeAd.mediaContent != null}"
                Log.d(TAG, message)
                crashReporter.log(message)
                if (ads.value.size >= AD_POOL_SIZE) isLoading = false
            }
            .withAdListener(
                object : AdListener() {
                    override fun onAdFailedToLoad(error: LoadAdError) {
                        isLoading = false
                        val exception = RuntimeException(
                            "Android native ad failed code=${error.code} domain=${error.domain} " +
                                "message=${error.message} suffix=${AndroidAdUnitIds.suffixSafe(adUnitId)}",
                        )
                        Log.w(TAG, exception.message.orEmpty(), exception)
                        crashReporter.recordException(exception)
                    }
                },
            )
            .build()
        adLoader.loadAds(AdRequest.Builder().build(), AD_POOL_SIZE)
    }

    override fun destroyAds() {
        val current = ads.value
        ads.value = emptyList()
        current.forEach { handle -> (handle as? NativeAd)?.destroy() }
    }

    private companion object {
        const val TAG = "AdMobNativeAds"
        const val AD_POOL_SIZE = 5
    }
}
