package com.apptolast.familyfilmapp.rating

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Multiplatform façade over the platform in-app review APIs.
 * - Android (block 14): Play In-App Review (ReviewManager).
 * - iOS (block 15): SKStoreReviewController.
 */
interface RateAppManager {
    val hasRatedApp: StateFlow<Boolean>
    fun markAsRated()
}

class NoOpRateAppManager : RateAppManager {
    private val _hasRated = MutableStateFlow(false)
    override val hasRatedApp: StateFlow<Boolean> = _hasRated.asStateFlow()
    override fun markAsRated() {
        _hasRated.value = true
    }
}
