package com.apptolast.familyfilmapp.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import com.apptolast.familyfilmapp.ads.NativeAdBridge
import com.apptolast.familyfilmapp.ads.NativeAdHandle
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun NativeAdSlot(adHandle: NativeAdHandle, modifier: Modifier) {
    val factory = NativeAdBridge.viewFactory ?: return
    UIKitView(
        modifier = modifier.fillMaxSize(),
        factory = { factory.createNativeAdView(adHandle) },
    )
}
