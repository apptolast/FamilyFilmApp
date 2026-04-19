package com.apptolast.familyfilmapp.purchases

import android.app.Activity
import android.content.Context
import com.apptolast.familyfilmapp.BuildConfig
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.LogLevel
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import com.revenuecat.purchases.interfaces.UpdatedCustomerInfoListener
import com.revenuecat.purchases.models.StoreTransaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import kotlin.coroutines.resume

class RevenueCatPurchaseManager(private val context: Context) :
    PurchaseManager,
    UpdatedCustomerInfoListener {

    private val _hasRemovedAds = MutableStateFlow(false)
    override val hasRemovedAds: StateFlow<Boolean> = _hasRemovedAds.asStateFlow()

    private val _hasChatPremium = MutableStateFlow(false)
    override val hasChatPremium: StateFlow<Boolean> = _hasChatPremium.asStateFlow()

    private var isConfigured = false

    override fun setAdsRemoved(removed: Boolean) {
        if (removed && !_hasRemovedAds.value) {
            _hasRemovedAds.value = true
            Timber.d("PurchaseManager: adsRemoved set to true from persisted state")
        }
    }

    override fun initialize(userId: String) {
        if (isConfigured) return

        val apiKey = if (BuildConfig.DEBUG) {
            BuildConfig.REVENUECAT_TEST_API_KEY
        } else {
            BuildConfig.REVENUECAT_API_KEY
        }
        if (apiKey.isBlank()) {
            Timber.w("RevenueCat API key is blank, skipping initialization")
            return
        }

        if (BuildConfig.DEBUG) {
            Purchases.logLevel = LogLevel.DEBUG
        }

        Purchases.configure(
            PurchasesConfiguration.Builder(context, apiKey)
                .appUserID(userId)
                .build(),
        )
        Purchases.sharedInstance.updatedCustomerInfoListener = this
        isConfigured = true
        Timber.d("RevenueCat configured for user: $userId")

        // Check initial entitlement status
        Purchases.sharedInstance.getCustomerInfo(
            object : com.revenuecat.purchases.interfaces.ReceiveCustomerInfoCallback {
                override fun onReceived(customerInfo: CustomerInfo) {
                    updateEntitlementState(customerInfo)
                }

                override fun onError(error: com.revenuecat.purchases.PurchasesError) {
                    Timber.e("RevenueCat initial info error: ${error.message}")
                }
            },
        )
    }

    override suspend fun purchaseRemoveAds(activity: Activity, onPurchaseStart: () -> Unit): Result<Unit> =
        launchPurchaseFlow(
            activity = activity,
            offeringId = null, // null → uses offerings.current (legacy remove_ads flow)
            onPurchaseStart = onPurchaseStart,
        )

    override suspend fun purchaseChatPremium(activity: Activity, onPurchaseStart: () -> Unit): Result<Unit> =
        launchPurchaseFlow(
            activity = activity,
            offeringId = CHAT_PREMIUM_OFFERING_ID,
            onPurchaseStart = onPurchaseStart,
        )

    /**
     * Shared purchase flow. When [offeringId] is null we fall back to `offerings.current`
     * for backwards compatibility with the pre-existing Remove Ads purchase.
     */
    private suspend fun launchPurchaseFlow(
        activity: Activity,
        offeringId: String?,
        onPurchaseStart: () -> Unit,
    ): Result<Unit> = suspendCancellableCoroutine { continuation ->
        if (!isConfigured) {
            continuation.resume(Result.failure(IllegalStateException("RevenueCat not configured")))
            return@suspendCancellableCoroutine
        }

        Purchases.sharedInstance.getOfferings(
            object : com.revenuecat.purchases.interfaces.ReceiveOfferingsCallback {
                override fun onReceived(offerings: com.revenuecat.purchases.Offerings) {
                    val offering = if (offeringId != null) {
                        offerings[offeringId]
                    } else {
                        offerings.current
                    }
                    val packageToBuy = offering?.availablePackages?.firstOrNull()
                    if (packageToBuy == null) {
                        val label = offeringId ?: "current"
                        continuation.resume(
                            Result.failure(IllegalStateException("No package in offering '$label'")),
                        )
                        return
                    }

                    onPurchaseStart()
                    Purchases.sharedInstance.purchase(
                        com.revenuecat.purchases.PurchaseParams.Builder(activity, packageToBuy)
                            .build(),
                        object : com.revenuecat.purchases.interfaces.PurchaseCallback {
                            override fun onCompleted(storeTransaction: StoreTransaction, customerInfo: CustomerInfo) {
                                updateEntitlementState(customerInfo)
                                continuation.resume(Result.success(Unit))
                            }

                            override fun onError(
                                error: com.revenuecat.purchases.PurchasesError,
                                userCancelled: Boolean,
                            ) {
                                if (userCancelled) {
                                    continuation.resume(
                                        Result.failure(Exception("Purchase cancelled")),
                                    )
                                } else {
                                    continuation.resume(
                                        Result.failure(Exception(error.message)),
                                    )
                                }
                            }
                        },
                    )
                }

                override fun onError(error: com.revenuecat.purchases.PurchasesError) {
                    continuation.resume(Result.failure(Exception(error.message)))
                }
            },
        )
    }

    override suspend fun restorePurchases(): Result<Boolean> = suspendCancellableCoroutine { continuation ->
        if (!isConfigured) {
            continuation.resume(Result.failure(IllegalStateException("RevenueCat not configured")))
            return@suspendCancellableCoroutine
        }

        Purchases.sharedInstance.restorePurchases(
            object : com.revenuecat.purchases.interfaces.ReceiveCustomerInfoCallback {
                override fun onReceived(customerInfo: CustomerInfo) {
                    updateEntitlementState(customerInfo)
                    val restoredSomething =
                        customerInfo.entitlements[AD_FREE_ENTITLEMENT]?.isActive == true ||
                            customerInfo.entitlements[CHAT_PREMIUM_ENTITLEMENT]?.isActive == true
                    continuation.resume(Result.success(restoredSomething))
                }

                override fun onError(error: com.revenuecat.purchases.PurchasesError) {
                    continuation.resume(Result.failure(Exception(error.message)))
                }
            },
        )
    }

    override fun onReceived(customerInfo: CustomerInfo) {
        updateEntitlementState(customerInfo)
    }

    private fun updateEntitlementState(customerInfo: CustomerInfo) {
        val adFree = customerInfo.entitlements[AD_FREE_ENTITLEMENT]?.isActive == true
        val chatPremium = customerInfo.entitlements[CHAT_PREMIUM_ENTITLEMENT]?.isActive == true
        _hasRemovedAds.value = adFree
        _hasChatPremium.value = chatPremium
        Timber.d("RevenueCat entitlement update: ad_free=$adFree chat_premium=$chatPremium")
    }

    companion object {
        private const val AD_FREE_ENTITLEMENT = "ad_free"
        private const val CHAT_PREMIUM_ENTITLEMENT = "chat_premium"
        private const val CHAT_PREMIUM_OFFERING_ID = "chat_premium"
    }
}
