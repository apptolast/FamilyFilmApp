package com.apptolast.familyfilmapp.ads

import android.content.Context
import com.apptolast.familyfilmapp.BuildConfig
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber

class NativeAdManager(private val context: Context) {

    private val _nativeAds = MutableStateFlow<List<NativeAd>>(emptyList())
    val nativeAds: StateFlow<List<NativeAd>> = _nativeAds.asStateFlow()

    fun loadAds(count: Int = 5) {
        val adLoader = AdLoader.Builder(context, BuildConfig.ADMOB_NATIVE_HOME_ID)
            .forNativeAd { nativeAd ->
                _nativeAds.update { it + nativeAd }
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    Timber.e("Native home ad failed: ${error.message}")
                }
            })
            .build()
        adLoader.loadAds(AdRequest.Builder().build(), count)
    }

    fun destroyAds() {
        _nativeAds.value.forEach { it.destroy() }
        _nativeAds.update { emptyList() }
    }
}
