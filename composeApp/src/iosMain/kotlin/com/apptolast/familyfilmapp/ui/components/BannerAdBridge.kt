package com.apptolast.familyfilmapp.ui.components

import platform.UIKit.UIView

/**
 * Swift implements this protocol and registers it via [BannerAdBridge.factory] at app start.
 * Keeps GoogleMobileAds out of Kotlin: the SDK only ships via SPM into the iOS app target.
 */
interface BannerAdViewFactory {
    fun createBannerView(adUnitId: String, width: Double): UIView
    fun getBannerHeight(width: Double): Double
}

object BannerAdBridge {
    var factory: BannerAdViewFactory? = null
}
