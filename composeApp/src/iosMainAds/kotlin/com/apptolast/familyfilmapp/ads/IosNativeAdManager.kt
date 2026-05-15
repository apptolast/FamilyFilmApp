package com.apptolast.familyfilmapp.ads

import com.apptolast.familyfilmapp.BuildConfig
import ios.googlemobileads.GADAdLoader
import ios.googlemobileads.GADAdLoaderDelegateProtocol
import ios.googlemobileads.GADAdLoaderOptions
import ios.googlemobileads.GADMultipleAdsAdLoaderOptions
import ios.googlemobileads.GADNativeAd
import ios.googlemobileads.GADNativeAdLoaderDelegateProtocol
import ios.googlemobileads.GADRequest
import ios.googlemobileads.kGADAdLoaderAdTypeNative
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import platform.Foundation.NSError
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
class IosNativeAdManager : NativeAdManager {

    private val _ads = MutableStateFlow<List<NativeAdHandle>>(emptyList())
    override val nativeAds: StateFlow<List<NativeAdHandle>> = _ads.asStateFlow()

    private var loader: GADAdLoader? = null
    private var delegate: NativeAdDelegate? = null

    override fun loadAds() {
        val options = GADMultipleAdsAdLoaderOptions().apply {
            numberOfAds = AD_POOL_SIZE.toLong()
        }

        val newDelegate = NativeAdDelegate(
            onLoaded = { ad -> _ads.update { (it + ad).takeLast(AD_POOL_SIZE) } },
        )
        delegate = newDelegate

        loader = GADAdLoader(
            adUnitID = BuildConfig.ADMOB_NATIVE_HOME_ID,
            rootViewController = null,
            adTypes = listOf(kGADAdLoaderAdTypeNative),
            options = listOf<GADAdLoaderOptions>(options),
        ).apply {
            this.delegate = newDelegate
            loadRequest(GADRequest())
        }
    }

    override fun destroyAds() {
        _ads.value = emptyList()
        loader = null
        delegate = null
    }

    private class NativeAdDelegate(
        private val onLoaded: (GADNativeAd) -> Unit,
    ) : NSObject(), GADNativeAdLoaderDelegateProtocol, GADAdLoaderDelegateProtocol {

        override fun adLoader(adLoader: GADAdLoader, didReceiveNativeAd: GADNativeAd) {
            onLoaded(didReceiveNativeAd)
        }

        override fun adLoader(adLoader: GADAdLoader, didFailToReceiveAdWithError: NSError) {
            // Silent failure: caller exposes the empty StateFlow until ads arrive.
        }
    }

    private companion object {
        const val AD_POOL_SIZE = 3
    }
}
