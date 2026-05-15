package com.apptolast.familyfilmapp.ads

// Fallback factory when cinterop is disabled.
fun createNativeAdManager(): NativeAdManager = NoOpNativeAdManager()
