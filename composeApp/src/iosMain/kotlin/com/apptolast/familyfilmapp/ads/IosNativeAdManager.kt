package com.apptolast.familyfilmapp.ads

import com.apptolast.familyfilmapp.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class IosNativeAdManager : NativeAdManager {

    private val _nativeAds = MutableStateFlow<List<NativeAdHandle>>(emptyList())
    override val nativeAds: StateFlow<List<NativeAdHandle>> = _nativeAds.asStateFlow()

    override fun loadAds() {
        val loader = NativeAdBridge.loader ?: return
        loader.load(BuildConfig.ADMOB_NATIVE_HOME_ID_IOS, AD_POOL_SIZE) { handle ->
            _nativeAds.update { (it + handle).takeLast(AD_POOL_SIZE) }
        }
    }

    override fun destroyAds() {
        val current = _nativeAds.value
        _nativeAds.value = emptyList()
        NativeAdBridge.loader?.destroy(current)
    }

    private companion object {
        const val AD_POOL_SIZE = 3
    }
}
