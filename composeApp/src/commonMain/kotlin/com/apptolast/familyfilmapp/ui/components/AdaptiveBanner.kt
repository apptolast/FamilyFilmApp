package com.apptolast.familyfilmapp.ui.components

import androidx.compose.runtime.Composable

/**
 * Professional AdMob Banner implementation that persists across screens.
 * This composable properly manages the AdView lifecycle to prevent:
 * - Unnecessary reloads when navigating between screens
 * - Memory leaks by properly pausing/resuming/destroying ads
 * - Multiple ad requests for the same instance
 */
@Composable
expect fun AdaptiveBanner()
