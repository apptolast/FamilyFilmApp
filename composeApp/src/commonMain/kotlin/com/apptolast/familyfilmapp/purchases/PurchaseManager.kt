package com.apptolast.familyfilmapp.purchases

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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

    /** Restore the user's prior purchases. Returns `true` if any entitlement was restored. */
    suspend fun restorePurchases(): Result<Boolean>

    /** Localized pricing for the Chat Premium subscription, or `null` if it can't be resolved. */
    suspend fun getChatPremiumPricing(): SubscriptionPricing?
}

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
    override suspend fun getChatPremiumPricing(): SubscriptionPricing? = null
}
