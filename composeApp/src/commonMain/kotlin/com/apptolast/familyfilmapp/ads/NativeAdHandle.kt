package com.apptolast.familyfilmapp.ads

/**
 * Opaque handle to a platform-native AdMob native ad object.
 *
 * - Android (block 14): `actual typealias NativeAdHandle = com.google.android.gms.ads.nativead.NativeAd`
 *   so existing rendering code that already speaks the AdMob API keeps working.
 * - iOS (block 15): `actual typealias NativeAdHandle = cocoapods.GoogleMobileAds.GADNativeAd`
 *   (or the SPM equivalent once block 15 wires the cinterop).
 *
 * commonMain code can keep references in lists / state flows but cannot
 * inspect the contents — rendering happens behind expect/actual composables.
 */
expect class NativeAdHandle
