package com.apptolast.familyfilmapp.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import androidx.compose.ui.unit.dp
import com.apptolast.familyfilmapp.ads.NativeAdHandle
import ios.googlemobileads.GADNativeAd
import ios.googlemobileads.GADNativeAdView
import platform.CoreGraphics.CGRectMake
import platform.UIKit.UIColor
import platform.UIKit.UIFont
import platform.UIKit.UILabel
import platform.UIKit.UIView
import platform.UIKit.UIViewAutoresizingFlexibleWidth

@Composable
actual fun NativeAdSlot(adHandle: NativeAdHandle, modifier: Modifier) {
    val nativeAd = adHandle as? GADNativeAd ?: return
    UIKitView(
        factory = { buildAdView(nativeAd) },
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp),
        update = { view ->
            (view as? GADNativeAdView)?.nativeAd = nativeAd
            view.applyAdContents(nativeAd)
        },
    )
}

private fun buildAdView(nativeAd: GADNativeAd): GADNativeAdView {
    val view = GADNativeAdView(frame = CGRectMake(0.0, 0.0, 0.0, 120.0))
    view.autoresizingMask = UIViewAutoresizingFlexibleWidth

    val headline = UILabel(frame = CGRectMake(12.0, 8.0, 0.0, 24.0)).apply {
        font = UIFont.boldSystemFontOfSize(16.0)
        textColor = UIColor.labelColor
        autoresizingMask = UIViewAutoresizingFlexibleWidth
    }
    val body = UILabel(frame = CGRectMake(12.0, 36.0, 0.0, 60.0)).apply {
        font = UIFont.systemFontOfSize(13.0)
        textColor = UIColor.secondaryLabelColor
        numberOfLines = 3
        autoresizingMask = UIViewAutoresizingFlexibleWidth
    }

    view.headlineView = headline
    view.bodyView = body
    view.addSubview(headline as UIView)
    view.addSubview(body as UIView)

    view.nativeAd = nativeAd
    view.applyAdContents(nativeAd)
    return view
}

private fun GADNativeAdView.applyAdContents(ad: GADNativeAd) {
    (headlineView as? UILabel)?.text = ad.headline
    (bodyView as? UILabel)?.text = ad.body
}
