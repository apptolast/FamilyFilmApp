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
    private var currentAppUserId: String? = null

    override fun setAdsRemoved(removed: Boolean) {
        if (removed && !_hasRemovedAds.value) {
            _hasRemovedAds.value = true
            Timber.d("PurchaseManager: adsRemoved set to true from persisted state")
        }
    }

    override fun initialize(userId: String) {
        // First call: configure the SDK with this user.
        if (!isConfigured) {
            val apiKey = if (BuildConfig.DEBUG) {
                BuildConfig.REVENUECAT_PLAY_SDK_KEY_TEST
            } else {
                BuildConfig.REVENUECAT_PLAY_SDK_KEY
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
            currentAppUserId = userId
            Timber.d("RevenueCat configured for user: $userId")

            fetchInitialCustomerInfo()
            return
        }

        // Subsequent calls: same user → noop. Different user → switch via logIn().
        if (userId == currentAppUserId) {
            Timber.d("RevenueCat already configured for user: $userId — skipping")
            return
        }

        Timber.d("RevenueCat user switch: $currentAppUserId → $userId")
        // Reset locally so the new user does not see the previous user's entitlements
        // until the SDK callback returns the real CustomerInfo for the new appUserId.
        _hasChatPremium.value = false
        _hasRemovedAds.value = false
        currentAppUserId = userId

        Purchases.sharedInstance.logIn(
            userId,
            object : com.revenuecat.purchases.interfaces.LogInCallback {
                override fun onReceived(customerInfo: CustomerInfo, created: Boolean) {
                    Timber.d("RevenueCat logIn ok (created=$created) for user: $userId")
                    updateEntitlementState(customerInfo)
                }

                override fun onError(error: com.revenuecat.purchases.PurchasesError) {
                    Timber.e("RevenueCat logIn error for $userId: ${error.message}")
                }
            },
        )
    }

    override fun logout() {
        // Reset local cache immediately so the UI does not show stale entitlements
        // while the SDK round-trips. A subsequent initialize(newUserId) will refresh
        // them with the real CustomerInfo for that user.
        _hasChatPremium.value = false
        _hasRemovedAds.value = false
        currentAppUserId = null

        if (!isConfigured) return

        Purchases.sharedInstance.logOut(
            object : com.revenuecat.purchases.interfaces.ReceiveCustomerInfoCallback {
                override fun onReceived(customerInfo: CustomerInfo) {
                    Timber.d("RevenueCat logOut ok — switched to anonymous appUserId")
                    // Anonymous user has no entitlements; reflect that explicitly.
                    updateEntitlementState(customerInfo)
                }

                override fun onError(error: com.revenuecat.purchases.PurchasesError) {
                    Timber.e("RevenueCat logOut error: ${error.message}")
                }
            },
        )
    }

    private fun fetchInitialCustomerInfo() {
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
            entitlementId = AD_FREE_ENTITLEMENT,
            onPurchaseStart = onPurchaseStart,
        )

    override suspend fun purchaseChatPremium(activity: Activity, onPurchaseStart: () -> Unit): Result<Unit> =
        launchPurchaseFlow(
            activity = activity,
            offeringId = CHAT_PREMIUM_OFFERING_ID,
            entitlementId = CHAT_PREMIUM_ENTITLEMENT,
            onPurchaseStart = onPurchaseStart,
        )

    /**
     * Shared purchase flow. When [offeringId] is null we fall back to `offerings.current`
     * for backwards compatibility with the pre-existing Remove Ads purchase.
     *
     * [entitlementId] is the RevenueCat entitlement that a successful purchase is expected
     * to activate. After [PurchaseCallback.onCompleted] we verify the returned `CustomerInfo`
     * actually contains that entitlement as active — otherwise Play reported a successful
     * receipt but the RC Dashboard hasn't mapped the product to the entitlement (or the
     * webhook hasn't synced yet), and granting the feature client-side would diverge from
     * the server-side quota enforcement.
     */
    private suspend fun launchPurchaseFlow(
        activity: Activity,
        offeringId: String?,
        entitlementId: String,
        onPurchaseStart: () -> Unit,
    ): Result<Unit> = suspendCancellableCoroutine { continuation ->
        if (!isConfigured) {
            continuation.resume(Result.failure(PurchaseFailure.NotConfigured))
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
                        continuation.resume(
                            Result.failure(PurchaseFailure.OfferingUnavailable(offeringId)),
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
                                val isActive = customerInfo.entitlements[entitlementId]?.isActive == true
                                if (isActive) {
                                    continuation.resume(Result.success(Unit))
                                } else {
                                    Timber.w(
                                        "Purchase completed but entitlement '%s' is NOT active. " +
                                            "Check the RevenueCat Dashboard: product→entitlement mapping " +
                                            "and the Play/App Store webhook.",
                                        entitlementId,
                                    )
                                    continuation.resume(
                                        Result.failure(PurchaseFailure.EntitlementNotActive(entitlementId)),
                                    )
                                }
                            }

                            override fun onError(
                                error: com.revenuecat.purchases.PurchasesError,
                                userCancelled: Boolean,
                            ) {
                                continuation.resume(
                                    Result.failure(
                                        if (userCancelled) {
                                            PurchaseFailure.Cancelled
                                        } else {
                                            PurchaseFailure.Unknown(error.message)
                                        },
                                    ),
                                )
                            }
                        },
                    )
                }

                override fun onError(error: com.revenuecat.purchases.PurchasesError) {
                    continuation.resume(Result.failure(PurchaseFailure.Unknown(error.message)))
                }
            },
        )
    }

    override suspend fun restorePurchases(): Result<Boolean> = suspendCancellableCoroutine { continuation ->
        if (!isConfigured) {
            continuation.resume(Result.failure(PurchaseFailure.NotConfigured))
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
                    continuation.resume(Result.failure(PurchaseFailure.Unknown(error.message)))
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
