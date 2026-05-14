package com.apptolast.familyfilmapp.ads

import androidx.compose.runtime.Composable

/**
 * No-op on iOS until block 15 wires the GoogleMobileAds SPM module via
 * cinterop and exposes the equivalent `GADBannerView` rendering through
 * a `UIKitView` host.
 */
@Composable
actual fun AdaptiveBanner() {
    // Intentionally empty.
}
