package com.apptolast.familyfilmapp.purchases

import android.app.Activity
import kotlinx.coroutines.flow.StateFlow

interface PurchaseManager {
    val hasRemovedAds: StateFlow<Boolean>
    fun initialize(userId: String)
    fun setAdsRemoved(removed: Boolean)
    suspend fun purchaseRemoveAds(activity: Activity, onPurchaseStart: () -> Unit = {}): Result<Unit>
    suspend fun restorePurchases(): Result<Boolean>
}
