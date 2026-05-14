package com.apptolast.familyfilmapp.ads

/**
 * Opaque handle to a platform-native AdMob native ad object.
 *
 * commonMain never inspects the contents — it only holds and forwards
 * the value to platform-specific rendering composables (block 13+).
 * Using a plain `Any` typealias instead of `expect class` avoids the
 * modality mismatch we hit when mapping to the Android SDK's abstract
 * `NativeAd` via `actual typealias` (`expect class` defaults to `final`,
 * `NativeAd` is `abstract`).
 *
 * - Android: each element is a `com.google.android.gms.ads.nativead.NativeAd`.
 * - iOS: each element will be a `GADNativeAd` once block 15 wires the
 *   GoogleMobileAds SPM module via cinterop.
 */
typealias NativeAdHandle = Any
