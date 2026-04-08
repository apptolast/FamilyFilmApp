package com.apptolast.familyfilmapp.purchases

import android.app.Activity
import kotlinx.coroutines.flow.StateFlow

interface PurchaseManager {
    val hasRemovedAds: StateFlow<Boolean>
    fun initialize(userId: String)
    suspend fun purchaseRemoveAds(activity: Activity): Result<Unit>
    suspend fun restorePurchases(): Result<Boolean>
}
