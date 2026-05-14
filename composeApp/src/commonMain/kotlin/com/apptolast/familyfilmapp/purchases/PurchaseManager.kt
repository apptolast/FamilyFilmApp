package com.apptolast.familyfilmapp.purchases

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Cross-platform entitlement façade. Block 14 (Android) wires RevenueCat
 * behind the `RevenueCatPurchaseManager` actual; block 15 (iOS) wires
 * the iOS RevenueCat SPM module. ViewModels in commonMain only see this
 * tiny interface so they can react to entitlement changes without
 * touching the SDKs directly.
 */
interface PurchaseManager {
    val hasRemovedAds: StateFlow<Boolean>
    val hasChatPremium: StateFlow<Boolean>

    /** Bind the SDK session to a Firebase user id (typically called on login). */
    suspend fun initialize(userId: String)

    /** Mirror an entitlement value back into the SDK (used after a Firestore sync). */
    fun setAdsRemoved(value: Boolean)

    /** Reset the SDK session on logout / account deletion. */
    fun logout()
}

/**
 * Placeholder that satisfies the Koin graph until blocks 14/15 publish a
 * real RevenueCat-backed implementation. Emits `false` for every
 * entitlement and ignores writes.
 */
class NoOpPurchaseManager : PurchaseManager {
    private val _hasRemovedAds = MutableStateFlow(false)
    private val _hasChatPremium = MutableStateFlow(false)

    override val hasRemovedAds: StateFlow<Boolean> = _hasRemovedAds.asStateFlow()
    override val hasChatPremium: StateFlow<Boolean> = _hasChatPremium.asStateFlow()

    override suspend fun initialize(userId: String) = Unit
    override fun setAdsRemoved(value: Boolean) {
        _hasRemovedAds.value = value
    }

    override fun logout() {
        _hasRemovedAds.value = false
        _hasChatPremium.value = false
    }
}
