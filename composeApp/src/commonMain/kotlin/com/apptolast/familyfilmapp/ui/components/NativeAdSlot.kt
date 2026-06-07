package com.apptolast.familyfilmapp.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.apptolast.familyfilmapp.ads.NativeAdHandle

/**
 * Platform-specific slot that renders a single [NativeAdHandle] as an in-feed ad card.
 *
 * On Android, this reads a [com.google.android.gms.ads.nativead.NativeAd] from the injected
 * [com.apptolast.familyfilmapp.ads.NativeAdManager] and renders it via `AndroidView` + the
 * SDK's `NativeAdView`. On iOS / other platforms, this is currently a no-op.
 */
@Composable
expect fun NativeAdSlot(adHandle: NativeAdHandle, modifier: Modifier = Modifier)
