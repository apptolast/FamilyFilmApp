package com.apptolast.familyfilmapp.rating

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import platform.StoreKit.SKStoreReviewController

/**
 * iOS implementation backed by StoreKit's `SKStoreReviewController`.
 *
 * Uses the no-argument `requestReview()` overload — Apple deprecated
 * it in iOS 14 in favour of the scene-aware variant, but it still
 * works (the system resolves the active window scene internally) and
 * keeps the implementation free of NSSet ↔ Kotlin Set adapters. If
 * we ever want the scene-aware variant we can switch to
 * `SKStoreReviewController.requestReviewInScene(:)` once we have a
 * helper to pick the active `UIWindowScene` cleanly.
 *
 * The system decides whether to actually show the review prompt
 * (Apple throttles them per app per year), so `hasRatedApp` only
 * reflects that we asked.
 *
 * No SPM dependency required — StoreKit is part of the Kotlin/Native
 * default frameworks.
 */
class StoreKitRateAppManager : RateAppManager {

    private val _hasRatedApp = MutableStateFlow(false)
    override val hasRatedApp: StateFlow<Boolean> = _hasRatedApp.asStateFlow()

    override fun markAsRated() {
        @Suppress("DEPRECATION")
        SKStoreReviewController.requestReview()
        _hasRatedApp.value = true
    }
}
