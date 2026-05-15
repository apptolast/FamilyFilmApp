package com.apptolast.familyfilmapp.ads

// Cinterop-active factory: produces the real iOS native ad manager.
fun createNativeAdManager(): NativeAdManager = IosNativeAdManager()
