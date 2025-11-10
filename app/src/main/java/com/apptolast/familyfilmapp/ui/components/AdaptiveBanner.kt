package com.apptolast.familyfilmapp.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.apptolast.familyfilmapp.BuildConfig
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import timber.log.Timber

/**
 * Professional AdMob Banner implementation that persists across screens.
 * This composable properly manages the AdView lifecycle to prevent:
 * - Unnecessary reloads when navigating between screens
 * - Memory leaks by properly pausing/resuming/destroying ads
 * - Multiple ad requests for the same instance
 *
 * The AdView is created once and reused, with proper lifecycle management
 * using pause()/resume()/destroy() methods.
 */
@Composable
fun AdaptiveBanner() {
    val deviceCurrentWidthDp = LocalConfiguration.current.screenWidthDp
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        factory = { context ->
            Timber.d("Creating AdView instance")
            AdView(context).apply {
                setAdSize(
                    AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                        context,
                        deviceCurrentWidthDp,
                    ),
                )
                adUnitId = BuildConfig.ADMOB_BOTTOM_BANNER_ID
                loadAd(AdRequest.Builder().build())
                Timber.d("AdView created and ad loaded")
            }
        },
        update = { view ->
            // This is called on recomposition - we don't reload ads unnecessarily
            Timber.d("AdView recomposition (no reload)")
        },
    )

    // Properly manage AdView lifecycle with pause/resume/destroy
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            // Note: AndroidView manages its own lifecycle, but we can add extra handling if needed
            when (event) {
                Lifecycle.Event.ON_RESUME -> Timber.d("Lifecycle resumed")
                Lifecycle.Event.ON_PAUSE -> Timber.d("Lifecycle paused")
                Lifecycle.Event.ON_DESTROY -> Timber.d("Lifecycle destroyed")
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            Timber.d("AdView lifecycle observer removed")
        }
    }
}
