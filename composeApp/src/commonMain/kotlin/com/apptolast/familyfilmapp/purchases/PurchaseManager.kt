package com.apptolast.familyfilmapp.purchases

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Cross-platform entitlement façade. Block 14 (Android) wires RevenueCat
 * behind the `RevenueCatPurchaseManager` actual; block 15 (iOS) wires
 * the iOS RevenueCat SPM module. ViewModels in commonMain only see this
 * interface so they can react to entitlement changes without touching
 * the SDKs directly.
 *
 * Activity / UIWindowScene plumbing for the paywall presentation lives
 * inside each platform implementation (an Activity holder on Android,
 * the current key window on iOS) — the suspending methods here just
 * report the outcome.
 */
interface PurchaseManager {
    val hasRemovedAds: StateFlow<Boolean>
    val hasChatPremium: StateFlow<Boolean>

    suspend fun initialize(userId: String)
    fun setAdsRemoved(value: Boolean)
    fun logout()

    /** Triggers the remove-ads paywall and resolves when it completes. */
    suspend fun purchaseRemoveAds(): Result<Unit>

    /** Triggers the chat premium paywall and resolves when it completes. */
    suspend fun purchaseChatPremium(): Result<Unit>

    /** Restore the user's prior purchases. Returns `true` if remove-ads was restored. */
    suspend fun restorePurchases(): Result<Boolean>
}

/**
 * Placeholder that satisfies the Koin graph until blocks 14/15 publish a
 * real RevenueCat-backed implementation. All paywall calls fail with
 * [PurchaseFailure.Cancelled] (the UI surface treats it as "user
 * cancelled the dialog" — a sensible no-op outcome).
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

    override suspend fun purchaseRemoveAds(): Result<Unit> = Result.failure(PurchaseFailure.Cancelled)
    override suspend fun purchaseChatPremium(): Result<Unit> = Result.failure(PurchaseFailure.Cancelled)
    override suspend fun restorePurchases(): Result<Boolean> = Result.success(false)
}
