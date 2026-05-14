package com.apptolast.familyfilmapp.rating

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import platform.StoreKit.SKStoreReviewController

// Apple throttles review prompts (~once per app per year), so hasRatedApp only reflects that we asked.
class StoreKitRateAppManager : RateAppManager {

    private val _hasRatedApp = MutableStateFlow(false)
    override val hasRatedApp: StateFlow<Boolean> = _hasRatedApp.asStateFlow()

    override fun markAsRated() {
        @Suppress("DEPRECATION")
        SKStoreReviewController.requestReview()
        _hasRatedApp.value = true
    }
}
