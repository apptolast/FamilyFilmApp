package com.apptolast.familyfilmapp.ads

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages the lifecycle of AdMob native ads consumed by the Home screen.
 * Blocks 14 (Android) and 15 (iOS) provide real implementations behind
 * the AdMob SDK / Google Mobile Ads SPM module; ViewModels see only this
 * interface.
 */
interface NativeAdManager {
    val nativeAds: StateFlow<List<NativeAdHandle>>
    fun loadAds()
    fun destroyAds()
}

class NoOpNativeAdManager : NativeAdManager {
    override val nativeAds: StateFlow<List<NativeAdHandle>> =
        MutableStateFlow<List<NativeAdHandle>>(emptyList()).asStateFlow()

    override fun loadAds() = Unit
    override fun destroyAds() = Unit
}
