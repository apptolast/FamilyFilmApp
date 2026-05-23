package com.apptolast.familyfilmapp.ui.screens.home

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.apptolast.familyfilmapp.ads.NativeAdHandle
import com.apptolast.familyfilmapp.ui.components.NativeAdSlot

/** Wrapper that renders a single [NativeAdHandle] as an in-feed poster tile. */
@Composable
fun NativeAdItem(adHandle: NativeAdHandle, modifier: Modifier = Modifier) {
    NativeAdSlot(
        adHandle = adHandle,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(2 / 3.2f)
            .clip(MaterialTheme.shapes.small),
    )
}
