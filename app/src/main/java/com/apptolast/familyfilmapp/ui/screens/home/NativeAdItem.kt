package com.apptolast.familyfilmapp.ui.screens.home

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.apptolast.familyfilmapp.ui.components.NativeAdAttribution
import com.apptolast.familyfilmapp.ui.components.NativeAdChoicesView
import com.apptolast.familyfilmapp.ui.components.NativeAdHeadlineView
import com.apptolast.familyfilmapp.ui.components.NativeAdMediaView
import com.apptolast.familyfilmapp.ui.components.NativeAdView
import com.google.android.gms.ads.nativead.NativeAd

/**
 * Renders a [NativeAd] as a full-bleed movie-poster card that blends into the home grid.
 *
 * Layout:
 *  - [NativeAdMediaView] fills the entire card (CENTER_CROP, matches poster ratio 2:3.2)
 *  - Top row: "Pub" attribution badge (left) + AdChoices overlay (right) — both required by
 *    AdMob policy
 *  - Bottom gradient overlay with headline text
 *
 * All SDK assets ([NativeAdMediaView], [NativeAdHeadlineView], [NativeAdChoicesView]) are
 * registered with the underlying NativeAdView via [LocalNativeAdView], satisfying the AdMob
 * native ad validator requirements (Ad Attribution + AdChoices Overlay).
 */
@Composable
fun NativeAdItem(nativeAd: NativeAd, modifier: Modifier = Modifier) {
    NativeAdView(
        nativeAd = nativeAd,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(2f / 3.2f)
            .clip(MaterialTheme.shapes.small),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Full-bleed media (image or video)
            NativeAdMediaView(
                modifier = Modifier.fillMaxSize(),
                scaleType = ImageView.ScaleType.CENTER_CROP,
            )

            // Top row: attribution badge (left) + AdChoices icon (right) — both required
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

            // Bottom gradient overlay with headline
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
