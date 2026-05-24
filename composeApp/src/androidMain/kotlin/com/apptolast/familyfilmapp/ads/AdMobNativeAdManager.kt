package com.apptolast.familyfilmapp.ads

import android.content.Context
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
class AdMobNativeAdManager(
    context: Context,
    private val crashReporter: CrashReporter,
) : NativeAdManager {

    private val appContext = context.applicationContext

    private val _ads = MutableStateFlow<List<NativeAdHandle>>(emptyList())
    override val nativeAds: StateFlow<List<NativeAdHandle>> = _ads.asStateFlow()
    private var isLoading = false

    override fun loadAds() {
        if (isLoading) {
            crashReporter.log("Android native ads load skipped: already loading")
            return
        }
        if (_ads.value.size >= AD_POOL_SIZE) {
            crashReporter.log("Android native ads load skipped: poolSize=${_ads.value.size}")
            return
        }

        val adUnitId = AndroidAdUnitIds.nativeHome()
        if (adUnitId.isBlank()) {
            crashReporter.recordException(IllegalStateException("Android native ad unit id is blank"))
            return
        }
        isLoading = true
        crashReporter.log(
            "Android native ads load requested source=${AndroidAdUnitIds.source()} " +
                "count=$AD_POOL_SIZE suffix=${AndroidAdUnitIds.suffixSafe(adUnitId)}",
        )

        val adLoader = AdLoader.Builder(appContext, adUnitId)
            .forNativeAd { nativeAd: NativeAd ->
                _ads.update { (it + nativeAd).takeLast(AD_POOL_SIZE) }
                crashReporter.log("Android native ad loaded poolSize=${_ads.value.size}")
                if (_ads.value.size >= AD_POOL_SIZE) isLoading = false
            }
            .withAdListener(
                object : AdListener() {
                    override fun onAdFailedToLoad(error: LoadAdError) {
                        isLoading = false
                        crashReporter.recordException(
                            RuntimeException(
                                "Android native ad failed code=${error.code} domain=${error.domain} " +
                                    "message=${error.message} suffix=${AndroidAdUnitIds.suffixSafe(adUnitId)}",
                            ),
                        )
                    }
                },
            )
            .build()
        adLoader.loadAds(AdRequest.Builder().build(), AD_POOL_SIZE)
    }

    override fun destroyAds() {
        val current = _ads.value
        _ads.value = emptyList()
        current.forEach { handle -> (handle as? NativeAd)?.destroy() }
    }

    private companion object {
        const val AD_POOL_SIZE = 5
    }
}
