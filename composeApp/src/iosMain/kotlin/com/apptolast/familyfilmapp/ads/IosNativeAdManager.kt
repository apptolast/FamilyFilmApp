package com.apptolast.familyfilmapp.ads

import com.apptolast.familyfilmapp.BuildConfig
import com.apptolast.familyfilmapp.firebase.CrashReporter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class IosNativeAdManager(private val crashReporter: CrashReporter) : NativeAdManager {

    private val _nativeAds = MutableStateFlow<List<NativeAdHandle>>(emptyList())
    override val nativeAds: StateFlow<List<NativeAdHandle>> = _nativeAds.asStateFlow()

    override fun loadAds() {
        val adUnitId = BuildConfig.ADMOB_NATIVE_HOME_ID_IOS
        if (adUnitId.isBlank()) {
            crashReporter.recordException(IllegalStateException("iOS native ad unit id is blank"))
            return
        }
        val loader = NativeAdBridge.loader
        if (loader == null) {
            crashReporter.recordException(IllegalStateException("iOS native ad loader bridge is not installed"))
            return
        }
        crashReporter.log("Requesting iOS native ads count=$AD_POOL_SIZE")
        loader.load(adUnitId, AD_POOL_SIZE) { handle ->
            _nativeAds.update { (it + handle).takeLast(AD_POOL_SIZE) }
            crashReporter.log("iOS native ad handle received. poolSize=${_nativeAds.value.size}")
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
