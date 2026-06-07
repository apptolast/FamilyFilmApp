package com.apptolast.familyfilmapp.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import androidx.compose.ui.unit.dp
import com.apptolast.familyfilmapp.BuildConfig
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.UIKit.UIScreen

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun AdaptiveBanner() {
    val factory = BannerAdBridge.factory ?: return
    val screenWidth = UIScreen.mainScreen.bounds.useContents { size.width }
    val bannerHeight = remember(screenWidth) { factory.getBannerHeight(screenWidth) }

    UIKitView(
        modifier = Modifier
            .fillMaxWidth()
            .height(bannerHeight.dp),
        factory = {
            factory.createBannerView(BuildConfig.ADMOB_BOTTOM_BANNER_ID_IOS, screenWidth)
        },
    )
}
