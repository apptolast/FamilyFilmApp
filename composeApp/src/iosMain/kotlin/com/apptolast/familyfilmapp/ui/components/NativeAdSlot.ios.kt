package com.apptolast.familyfilmapp.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.apptolast.familyfilmapp.ads.NativeAdHandle

// No-op fallback while `xcode.frameworks.path` is unset (cinterop disabled).
@Composable
actual fun NativeAdSlot(adHandle: NativeAdHandle, modifier: Modifier) = Unit
