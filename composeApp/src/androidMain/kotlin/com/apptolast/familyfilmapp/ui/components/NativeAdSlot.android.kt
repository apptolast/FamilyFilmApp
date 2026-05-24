package com.apptolast.familyfilmapp.ui.components

import android.view.ViewGroup
import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.apptolast.familyfilmapp.ads.NativeAdHandle
import com.google.android.gms.ads.nativead.AdChoicesView
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView as SdkNativeAdView

/**
 * Android implementation of [NativeAdSlot]. The provided [adHandle] is expected to be a
 * [com.google.android.gms.ads.nativead.NativeAd] produced by `AdMobNativeAdManager`.
 */
@Composable
actual fun NativeAdSlot(adHandle: NativeAdHandle, modifier: Modifier) {
    val nativeAd = adHandle as? NativeAd ?: return
    NativeAdView(
        nativeAd = nativeAd,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(2f / 3.2f)
            .clip(MaterialTheme.shapes.small),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            NativeAdMediaView(
                modifier = Modifier.fillMaxSize(),
                scaleType = ImageView.ScaleType.CENTER_CROP,
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .align(Alignment.TopStart),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                NativeAdAttribution()
                NativeAdChoicesView(modifier = Modifier.size(20.dp))
            }

            nativeAd.headline?.let { headline ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f)),
                            ),
                        )
                        .padding(horizontal = 6.dp, vertical = 8.dp),
                ) {
                    NativeAdHeadlineView {
                        Text(
                            text = headline,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}

private val LocalNativeAdView = staticCompositionLocalOf<SdkNativeAdView?> { null }

@Composable
private fun NativeAdView(nativeAd: NativeAd, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val nativeAdViewRef = remember { mutableStateOf<SdkNativeAdView?>(null) }

    AndroidView(
        factory = { context ->
            val composeView = ComposeView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
            }
            SdkNativeAdView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
                addView(composeView)
                nativeAdViewRef.value = this
            }
        },
        modifier = modifier,
        update = { view ->
            (view.getChildAt(0) as? ComposeView)?.setContent {
                CompositionLocalProvider(LocalNativeAdView provides view) { content() }
            }
        },
    )

    val currentNativeAd by rememberUpdatedState(nativeAd)
    SideEffect { nativeAdViewRef.value?.setNativeAd(currentNativeAd) }
}

@Composable
private fun NativeAdHeadlineView(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val adView = LocalNativeAdView.current ?: return
    AndroidView(
        factory = { context -> ComposeView(context) },
        modifier = modifier,
        update = { view ->
            adView.headlineView = view
            view.setContent(content)
        },
    )
}

@Composable
private fun NativeAdMediaView(modifier: Modifier = Modifier, scaleType: ImageView.ScaleType? = null) {
    val adView = LocalNativeAdView.current ?: return
    AndroidView(
        factory = { context -> MediaView(context) },
        modifier = modifier,
        update = { view ->
            adView.mediaView = view
            scaleType?.let { view.setImageScaleType(it) }
        },
    )
}

@Composable
private fun NativeAdChoicesView(modifier: Modifier = Modifier) {
    val adView = LocalNativeAdView.current ?: return
    AndroidView(
        factory = { context ->
            AdChoicesView(context).apply {
                minimumWidth = 15
                minimumHeight = 15
            }
        },
        modifier = modifier,
        update = { view -> adView.adChoicesView = view },
    )
}

@Composable
private fun NativeAdAttribution(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(Color(0xFFFFCC00), MaterialTheme.shapes.extraSmall)
            .padding(horizontal = 4.dp, vertical = 2.dp),
    ) {
        Text(
            text = "Pub",
            color = Color.Black,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}
