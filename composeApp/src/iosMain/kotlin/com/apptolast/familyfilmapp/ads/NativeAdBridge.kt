package com.apptolast.familyfilmapp.ads

import platform.UIKit.UIView

/**
 * Loads GADNativeAd instances via Swift (GoogleMobileAds is SPM-only, not exposed to Kotlin).
 * `onLoaded` is invoked once per ad as it arrives; `handle` is an opaque GADNativeAd reference
 * that callers pass back to [NativeAdViewFactory.createNativeAdView] when rendering.
 */
interface NativeAdLoader {
    fun load(adUnitId: String, count: Int, onLoaded: (handle: Any) -> Unit)
    fun destroy(handles: List<Any>)
}

interface NativeAdViewFactory {
    fun createNativeAdView(handle: Any): UIView
}

object NativeAdBridge {
    var loader: NativeAdLoader? = null
    var viewFactory: NativeAdViewFactory? = null
}
