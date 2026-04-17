package com.apptolast.familyfilmapp.ui.components

/*
 * Jetpack Compose wrappers for Google Mobile Ads Native Advanced Ads.
 *
 * Pattern: [NativeAdView] wraps a real [com.google.android.gms.ads.nativead.NativeAdView]
 * (Android SDK) that in turn contains a [ComposeView]. Each asset wrapper composable
 * ([NativeAdHeadlineView], [NativeAdMediaView], [NativeAdChoicesView], etc.) creates a child
 * view and registers itself with the parent NativeAdView via the appropriate SDK setter, so the
 * AdMob SDK can track impressions and clicks correctly.
 *
 * Callers compose these inside a [NativeAdView] block; [LocalNativeAdView] wires everything
 * together transparently via CompositionLocal.
 *
 * Based on the official Google sample:
 * https://github.com/googleads/googleads-mobile-android-examples/blob/main/kotlin/advanced/
 * JetpackComposeDemo/app/src/main/java/com/google/android/gms/example/jetpackcomposedemo/
 * formats/compose_utils/NativeAdView.kt
 */

import android.view.ViewGroup
import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.nativead.AdChoicesView
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView as SdkNativeAdView

internal val LocalNativeAdView = staticCompositionLocalOf<SdkNativeAdView?> { null }

/**
 * Compose wrapper for the SDK's NativeAdView. Hosts all [content] inside a [ComposeView]
 * child of the real NativeAdView so AdMob can track impressions and handle clicks. All asset
 * wrappers inside [content] receive the NativeAdView via [LocalNativeAdView].
 */
@Composable
fun NativeAdView(nativeAd: NativeAd, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
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

/** Registers a [ComposeView] as the headline asset. */
@Composable
fun NativeAdHeadlineView(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
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

/** Registers a [MediaView] as the media asset. */
@Composable
fun NativeAdMediaView(modifier: Modifier = Modifier, scaleType: ImageView.ScaleType? = null) {
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

/**
 * Registers an [AdChoicesView] as the AdChoices overlay. Required by AdMob policy — must be
 * visible and not obscured. Minimum 15x15dp per Google guidelines.
 */
@Composable
fun NativeAdChoicesView(modifier: Modifier = Modifier) {
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

/**
 * Ad attribution badge (pure Compose, not an SDK asset). Shows a visible "Ad" / "Pub" label
 * as required by AdMob policy for native ads.
 */
@Composable
fun NativeAdAttribution(
    modifier: Modifier = Modifier,
    text: String = "Pub",
    shape: Shape = MaterialTheme.shapes.extraSmall,
    containerColor: Color = Color(0xFFFFCC00),
    contentColor: Color = Color.Black,
    textStyle: TextStyle = MaterialTheme.typography.labelSmall,
    padding: PaddingValues = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
) {
    Box(
        modifier = modifier
            .background(containerColor, shape)
            .padding(padding),
    ) {
        Text(text = text, color = contentColor, style = textStyle)
    }
}
