package com.apptolast.familyfilmapp.purchases

import com.apptolast.familyfilmapp.firebase.CrashReporter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSUserDefaults
import kotlin.coroutines.resume

class IosRevenueCatPurchaseManager(private val crashReporter: CrashReporter) : PurchaseManager {

    private val _hasRemovedAds = MutableStateFlow(NSUserDefaults.standardUserDefaults.boolForKey(ADS_REMOVED_KEY))
    override val hasRemovedAds: StateFlow<Boolean> = _hasRemovedAds.asStateFlow()

    private val _hasChatPremium = MutableStateFlow(false)
    override val hasChatPremium: StateFlow<Boolean> = _hasChatPremium.asStateFlow()

    override suspend fun initialize(userId: String) {
        val bridge = bridge
        if (bridge == null) {
            crashReporter.log("IosRevenueCatPurchaseManager.initialize($userId) skipped: RevenueCat bridge not installed")
            return
        }

        suspendCancellableCoroutine<Unit> { cont ->
            bridge.logIn(userId) { hasRemovedAds, hasChatPremium, errorMessage ->
                if (errorMessage != null) {
                    crashReporter.recordException(PurchaseFailure.Generic(errorMessage))
                }
                mirror(hasRemovedAds, hasChatPremium)
                cont.resume(Unit)
            }
        }
    }

    override fun setAdsRemoved(value: Boolean) {
        _hasRemovedAds.value = value
        // Mirror to NSUserDefaults so the Swift-side AppOpenAdManager can gate ads too.
        NSUserDefaults.standardUserDefaults.setBool(value, ADS_REMOVED_KEY)
    }

    override fun logout() {
        bridge?.logOut { hasRemovedAds, hasChatPremium, errorMessage ->
            if (errorMessage != null) {
                crashReporter.recordException(PurchaseFailure.Generic(errorMessage))
            }
            mirror(hasRemovedAds, hasChatPremium)
        } ?: mirror(hasRemovedAds = false, hasChatPremium = false)
    }

    override suspend fun purchaseRemoveAds(): Result<Unit> = purchaseEntitlement(ENTITLEMENT_REMOVE_ADS)

    override suspend fun purchaseChatPremium(): Result<Unit> = purchaseEntitlement(ENTITLEMENT_CHAT_PREMIUM)

    override suspend fun restorePurchases(): Result<Boolean> {
        val bridge = bridge ?: return Result.failure(PurchaseFailure.Generic("RevenueCat bridge not installed"))
        return suspendCancellableCoroutine { cont ->
            bridge.restore { hasRemovedAds, hasChatPremium, errorMessage ->
                if (errorMessage != null) {
                    cont.resume(Result.failure(PurchaseFailure.Generic(errorMessage)))
                } else {
                    mirror(hasRemovedAds, hasChatPremium)
                    cont.resume(Result.success(hasRemovedAds || hasChatPremium))
                }
            }
        }
    }

    private suspend fun purchaseEntitlement(entitlement: String): Result<Unit> {
        val bridge = bridge ?: return Result.failure(PurchaseFailure.Generic("RevenueCat bridge not installed"))
        return suspendCancellableCoroutine { cont ->
            bridge.purchase(entitlement) { hasRemovedAds, hasChatPremium, errorMessage, userCancelled ->
                when {
                    userCancelled -> cont.resume(Result.failure(PurchaseFailure.Cancelled))
                    errorMessage != null -> cont.resume(Result.failure(PurchaseFailure.Generic(errorMessage)))
                    else -> {
                        mirror(hasRemovedAds, hasChatPremium)
                        cont.resume(Result.success(Unit))
                    }
                }
            }
        }
    }

    private fun mirror(hasRemovedAds: Boolean, hasChatPremium: Boolean) {
        _hasRemovedAds.value = hasRemovedAds
        _hasChatPremium.value = hasChatPremium
        NSUserDefaults.standardUserDefaults.setBool(hasRemovedAds, ADS_REMOVED_KEY)
    }

    companion object {
        private var bridge: IosRevenueCatPurchaseBridge? = null

        fun installBridge(bridge: IosRevenueCatPurchaseBridge) {
            this.bridge = bridge
        }

        const val ADS_REMOVED_KEY = "ads_removed"
        const val ENTITLEMENT_REMOVE_ADS = "remove_ads"
        const val ENTITLEMENT_CHAT_PREMIUM = "chat_premium"
    }
}
