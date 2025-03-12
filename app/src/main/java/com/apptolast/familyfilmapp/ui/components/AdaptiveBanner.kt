package com.apptolast.familyfilmapp.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.viewinterop.AndroidView
import com.apptolast.familyfilmapp.BuildConfig
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun AdaptiveBanner() {

    val deviceCurrentWidthDp = LocalConfiguration.current.screenWidthDp
    AndroidView(
        factory = { context ->
            AdView(context).apply {
                setAdSize(
                    AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                        context,
                        deviceCurrentWidthDp
                    )
                )
                adUnitId = if (BuildConfig.DEBUG) {
                    BuildConfig.ADMOB_BANNER_ID_DEBUG
                } else {
                    BuildConfig.ADMOB_BANNER_ID_DEBUG
                }
                loadAd(AdRequest.Builder().build())
            }
        },
    )
}
