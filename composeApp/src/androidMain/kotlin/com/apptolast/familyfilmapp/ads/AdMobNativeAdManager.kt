package com.apptolast.familyfilmapp.ads

import android.content.Context
import com.apptolast.familyfilmapp.BuildConfig
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.NativeAd
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Callers MUST invoke destroyAds() (e.g. from ViewModel.onCleared) to release the NativeAd resources.
class AdMobNativeAdManager(context: Context) : NativeAdManager {

    private val appContext = context.applicationContext

    private val _ads = MutableStateFlow<List<NativeAdHandle>>(emptyList())
    override val nativeAds: StateFlow<List<NativeAdHandle>> = _ads.asStateFlow()

    override fun loadAds() {
        val adLoader = AdLoader.Builder(appContext, BuildConfig.ADMOB_NATIVE_HOME_ID)
            .forNativeAd { nativeAd: NativeAd ->
                _ads.update { (it + nativeAd).takeLast(AD_POOL_SIZE) }
            }
            .build()
        adLoader.loadAds(AdRequest.Builder().build(), AD_POOL_SIZE)
    }

    override fun destroyAds() {
        val current = _ads.value
        _ads.value = emptyList()
        current.forEach { handle -> (handle as? NativeAd)?.destroy() }
    }

    private companion object {
        const val AD_POOL_SIZE = 3
    }
}
