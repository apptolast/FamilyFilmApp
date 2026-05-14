package com.apptolast.familyfilmapp.ads

import android.app.Activity
import android.util.DisplayMetrics
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.apptolast.familyfilmapp.BuildConfig
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
actual fun AdaptiveBanner() {
    val context = LocalContext.current
    AndroidView(
        modifier = Modifier.fillMaxWidth(),
        factory = { ctx ->
            AdView(ctx).apply {
                adUnitId = BuildConfig.ADMOB_BOTTOM_BANNER_ID
                setAdSize(adaptiveSize(ctx as? Activity))
                loadAd(AdRequest.Builder().build())
            }
        },
    )
}

private fun adaptiveSize(activity: Activity?): AdSize {
    val display = activity?.windowManager?.defaultDisplay ?: return AdSize.BANNER
    val outMetrics = DisplayMetrics().also { display.getMetrics(it) }
    val widthDp = (outMetrics.widthPixels / outMetrics.density).toInt()
    return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, widthDp)
}
