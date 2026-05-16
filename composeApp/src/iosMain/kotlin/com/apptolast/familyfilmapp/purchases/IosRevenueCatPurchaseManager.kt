package com.apptolast.familyfilmapp.purchases

import com.apptolast.familyfilmapp.firebase.CrashReporter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import platform.Foundation.NSUserDefaults

// No-op until the RevenueCat cinterop is enabled (see build.gradle.kts).
// Paywall calls resolve to Cancelled so the UI no-ops gracefully.
class IosRevenueCatPurchaseManager(private val crashReporter: CrashReporter) : PurchaseManager {

    private val _hasRemovedAds = MutableStateFlow(NSUserDefaults.standardUserDefaults.boolForKey(ADS_REMOVED_KEY))
    override val hasRemovedAds: StateFlow<Boolean> = _hasRemovedAds.asStateFlow()

    private val _hasChatPremium = MutableStateFlow(false)
    override val hasChatPremium: StateFlow<Boolean> = _hasChatPremium.asStateFlow()

    override suspend fun initialize(userId: String) {
        crashReporter.log("IosRevenueCatPurchaseManager.initialize($userId) — SPM cinterop not wired yet")
    }

    override fun setAdsRemoved(value: Boolean) {
        _hasRemovedAds.value = value
        // Mirror to NSUserDefaults so the Swift-side AppOpenAdManager can gate ads too.
        NSUserDefaults.standardUserDefaults.setBool(value, ADS_REMOVED_KEY)
    }

    override fun logout() {
        _hasRemovedAds.value = false
        _hasChatPremium.value = false
        NSUserDefaults.standardUserDefaults.setBool(false, ADS_REMOVED_KEY)
    }

    override suspend fun purchaseRemoveAds(): Result<Unit> = Result.failure(PurchaseFailure.Cancelled)
    override suspend fun purchaseChatPremium(): Result<Unit> = Result.failure(PurchaseFailure.Cancelled)
    override suspend fun restorePurchases(): Result<Boolean> = Result.success(false)

    private companion object {
        const val ADS_REMOVED_KEY = "ads_removed"
    }
}
