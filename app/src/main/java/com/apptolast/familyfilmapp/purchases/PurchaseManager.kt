package com.apptolast.familyfilmapp.purchases

import android.app.Activity
import kotlinx.coroutines.flow.StateFlow

interface PurchaseManager {
    val hasRemovedAds: StateFlow<Boolean>

    /**
     * Monthly Chat Premium subscription (50 Gemini questions/month).
     * Source of truth: RevenueCat `customerInfo.entitlements["chat_premium"].isActive`.
     */
    val hasChatPremium: StateFlow<Boolean>

    fun initialize(userId: String)
    fun setAdsRemoved(removed: Boolean)
    suspend fun purchaseRemoveAds(activity: Activity, onPurchaseStart: () -> Unit = {}): Result<Unit>

    /**
     * Launches the Play Store purchase flow for the `chat_premium` offering.
     * Fails if the RevenueCat offering is not configured.
     */
    suspend fun purchaseChatPremium(activity: Activity, onPurchaseStart: () -> Unit = {}): Result<Unit>

    suspend fun restorePurchases(): Result<Boolean>
}
