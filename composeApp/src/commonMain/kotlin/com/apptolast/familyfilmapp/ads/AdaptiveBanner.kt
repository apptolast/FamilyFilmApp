package com.apptolast.familyfilmapp.ads

import androidx.compose.runtime.Composable

/**
 * Renders a platform-native adaptive banner ad. On Android (block 14)
 * this embeds an `AdView` sized via
 * `AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize` and bound
 * to `BuildConfig.ADMOB_BOTTOM_BANNER_ID`. On iOS (block 15) it's a
 * no-op until the GoogleMobileAds SPM module + cinterop are wired.
 */
@Composable
expect fun AdaptiveBanner()
