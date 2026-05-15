package com.apptolast.familyfilmapp.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.LocalUIViewController
import androidx.compose.ui.interop.UIKitView
import androidx.compose.ui.unit.dp
import com.apptolast.familyfilmapp.BuildConfig
import ios.googlemobileads.GADBannerView
import ios.googlemobileads.GADCurrentOrientationAnchoredAdaptiveBannerAdSizeWithWidth
import ios.googlemobileads.GADRequest
import platform.UIKit.UIScreen

@Composable
actual fun AdaptiveBanner() {
    val rootController = LocalUIViewController.current
    val widthDp = remember { UIScreen.mainScreen.bounds.useContents { size.width } }
    val adSize = remember { GADCurrentOrientationAnchoredAdaptiveBannerAdSizeWithWidth(widthDp) }

    UIKitView(
        factory = {
            GADBannerView(adSize = adSize).apply {
                adUnitID = BuildConfig.ADMOB_BOTTOM_BANNER_ID
                rootViewController = rootController
                loadRequest(GADRequest())
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
    )
}
