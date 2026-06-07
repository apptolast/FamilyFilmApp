package com.apptolast.familyfilmapp.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.apptolast.familyfilmapp.ads.AndroidAdUnitIds
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.crashlytics.crashlytics

@Composable
actual fun AdaptiveBanner() {
    val context = LocalContext.current
    val widthDp = LocalConfiguration.current.screenWidthDp
    val lifecycleOwner = LocalLifecycleOwner.current
    val adView = remember(context, widthDp) {
        AdView(context).apply {
            val resolvedAdUnitId = AndroidAdUnitIds.banner()
            setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, widthDp))
            adUnitId = resolvedAdUnitId
            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    Firebase.crashlytics.log("Android banner ad loaded")
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Firebase.crashlytics.recordException(
                        RuntimeException(
                            "Android banner ad failed code=${error.code} domain=${error.domain} " +
                                "message=${error.message} " +
                                "suffix=${AndroidAdUnitIds.suffixSafe(resolvedAdUnitId)}",
                        ),
                    )
                }
            }
            Firebase.crashlytics.log(
                "Android banner ad load requested source=${AndroidAdUnitIds.source()} " +
                    "suffix=${AndroidAdUnitIds.suffixSafe(resolvedAdUnitId)}",
            )
            loadAd(AdRequest.Builder().build())
        }
    }

    AndroidView(
        modifier = Modifier.fillMaxWidth(),
        factory = { adView },
    )

    DisposableEffect(lifecycleOwner, adView) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> adView.resume()
                Lifecycle.Event.ON_PAUSE -> adView.pause()
                Lifecycle.Event.ON_DESTROY -> adView.destroy()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            adView.destroy()
        }
    }
}
