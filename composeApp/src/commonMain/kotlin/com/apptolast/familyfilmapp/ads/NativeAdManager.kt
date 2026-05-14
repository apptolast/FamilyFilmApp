package com.apptolast.familyfilmapp.ads

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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
