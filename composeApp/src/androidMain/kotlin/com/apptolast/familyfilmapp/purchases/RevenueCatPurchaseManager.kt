package com.apptolast.familyfilmapp.purchases

import android.content.Context
import android.content.pm.ApplicationInfo
import com.apptolast.familyfilmapp.BuildConfig
import com.apptolast.familyfilmapp.analytics.AnalyticsEvents
import com.apptolast.familyfilmapp.analytics.AnalyticsTracker
import com.apptolast.familyfilmapp.firebase.CrashReporter
import com.apptolast.familyfilmapp.platform.CurrentActivityHolder
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.LogLevel
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import com.revenuecat.purchases.PurchasesError
import com.revenuecat.purchases.PurchasesErrorCode
import com.revenuecat.purchases.getCustomerInfoWith
import com.revenuecat.purchases.getOfferingsWith
import com.revenuecat.purchases.interfaces.UpdatedCustomerInfoListener
import com.revenuecat.purchases.logInWith
import com.revenuecat.purchases.logOutWith
import com.revenuecat.purchases.models.StoreTransaction
import com.revenuecat.purchases.purchaseWith
import com.revenuecat.purchases.restorePurchasesWith
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

class RevenueCatPurchaseManager(
    private val context: Context,
    private val activityHolder: CurrentActivityHolder,
    private val crashReporter: CrashReporter,
    private val analyticsTracker: AnalyticsTracker,
) : PurchaseManager {

    private val _hasRemovedAds = MutableStateFlow(false)
    override val hasRemovedAds: StateFlow<Boolean> = _hasRemovedAds.asStateFlow()

    private val _hasChatPremium = MutableStateFlow(false)
    override val hasChatPremium: StateFlow<Boolean> = _hasChatPremium.asStateFlow()

    private var isConfigured: Boolean = false
    private var currentAppUserId: String? = null
    private val isDebuggable: Boolean
        get() = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

    override suspend fun initialize(userId: String) {
        if (!isConfigured) {
            val apiKey = if (isDebuggable) {
                BuildConfig.REVENUECAT_PLAY_SDK_KEY_TEST
            } else {
                BuildConfig.REVENUECAT_PLAY_SDK_KEY
            }
            crashReporter.setCustomKey(
                "revenuecat_play_key_present",
                apiKey.isNotBlank().toString(),
            )
            crashReporter.setCustomKey("revenuecat_play_key_mode", if (isDebuggable) "test" else "production")
            if (apiKey.isBlank()) {
                crashReporter.recordException(IllegalStateException("RevenueCat Android API key is blank"))
                return
            }

            Purchases.logLevel = if (isDebuggable) LogLevel.DEBUG else LogLevel.WARN
            crashReporter.log(
                "RevenueCat Android configure requested mode=${if (isDebuggable) "test" else "production"}",
            )
            Purchases.configure(
                PurchasesConfiguration.Builder(context, apiKey)
                    .appUserID(userId)
                    .build(),
            )
            Purchases.sharedInstance.updatedCustomerInfoListener =
                UpdatedCustomerInfoListener { info -> mirror(info) }
            isConfigured = true
            currentAppUserId = userId
        } else if (userId == currentAppUserId) {
            crashReporter.log("RevenueCat Android already configured for current user")
            return
        } else {
            _hasRemovedAds.value = false
            _hasChatPremium.value = false
            currentAppUserId = userId
            crashReporter.log("RevenueCat Android login requested")
            Purchases.sharedInstance.logInWith(
                userId,
                { error -> crashReporter.recordException(error.asThrowable("login failed")) },
                { info, _ -> mirror(info) },
            )
        }
        // Pull current state once.
        Purchases.sharedInstance.getCustomerInfoWith(
            { error -> crashReporter.recordException(error.asThrowable("getCustomerInfo failed")) },
            { info -> mirror(info) },
        )
    }

    override fun setAdsRemoved(value: Boolean) {
        if (value && !_hasRemovedAds.value) {
            _hasRemovedAds.value = true
            crashReporter.log("RevenueCat Android adsRemoved set from persisted state")
        }
    }

    override fun logout() {
        _hasRemovedAds.value = false
        _hasChatPremium.value = false
        currentAppUserId = null
        if (!isConfigured) return
        crashReporter.log("RevenueCat Android logout requested")
        Purchases.sharedInstance.logOutWith(
            { error -> crashReporter.recordException(error.asThrowable("logout failed")) },
            { info -> mirror(info) },
        )
    }

    override suspend fun purchaseRemoveAds(): Result<Unit> = launchPurchaseFlow(
        offeringId = null,
        entitlementId = AD_FREE_ENTITLEMENT,
        analyticsEntitlement = AnalyticsEvents.Entitlement.REMOVE_ADS,
    )

    override suspend fun purchaseChatPremium(): Result<Unit> = launchPurchaseFlow(
        offeringId = CHAT_PREMIUM_OFFERING_ID,
        entitlementId = CHAT_PREMIUM_ENTITLEMENT,
        analyticsEntitlement = AnalyticsEvents.Entitlement.CHAT_PREMIUM,
    )

    override suspend fun restorePurchases(): Result<Boolean> = suspendCancellableCoroutine { cont ->
        if (!isConfigured) {
            cont.resume(Result.failure(PurchaseFailure.NotConfigured))
            return@suspendCancellableCoroutine
        }
        crashReporter.log("RevenueCat Android restore requested")
        Purchases.sharedInstance.restorePurchasesWith(
            { error ->
                crashReporter.recordException(error.asThrowable("restore failed"))
                analyticsTracker.logEvent(
                    AnalyticsEvents.RESTORE_PURCHASE,
                    mapOf(AnalyticsEvents.Param.RESULT to AnalyticsEvents.RestoreResult.ERROR),
                )
                cont.resume(Result.failure(error.toPurchaseFailure()))
            },
            { info ->
                mirror(info)
                val restoredSomething = info.hasAnyRestorableEntitlement()
                analyticsTracker.logEvent(
                    AnalyticsEvents.RESTORE_PURCHASE,
                    mapOf(
                        AnalyticsEvents.Param.RESULT to if (restoredSomething) {
                            AnalyticsEvents.RestoreResult.SUCCESS
                        } else {
                            AnalyticsEvents.RestoreResult.NOTHING_FOUND
                        },
                    ),
                )
                crashReporter.log(
                    "RevenueCat Android restore success removeAds=${_hasRemovedAds.value} " +
                        "chatPremium=${_hasChatPremium.value}",
                )
                cont.resume(Result.success(restoredSomething))
            },
        )
    }

    private suspend fun launchPurchaseFlow(
        offeringId: String?,
        entitlementId: String,
        analyticsEntitlement: String,
    ): Result<Unit> {
        if (!isConfigured) return Result.failure(PurchaseFailure.NotConfigured)
        val activity = activityHolder.current
            ?: return Result.failure(
                PurchaseFailure.Unknown("No Activity available for purchase").also {
                    crashReporter.recordException(
                        IllegalStateException("RevenueCat Android purchase failed: no Activity"),
                    )
                },
            )
        crashReporter.log(
            "RevenueCat Android purchase started entitlement=$entitlementId offering=${offeringId ?: "current"}",
        )
        val offerings = fetchOfferings().getOrElse { return Result.failure(it) }
        val pkg = offerings?.packageForOffering(offeringId)
        if (pkg == null) {
            val failure = PurchaseFailure.OfferingUnavailable(offeringId)
            crashReporter.recordException(IllegalStateException(missingPackageMessage(offeringId, offerings)))
            return Result.failure(failure)
        }

        return withContext(Dispatchers.Main.immediate) {
            suspendCancellableCoroutine { cont ->
                crashReporter.log("RevenueCat Android package selected ${pkg.describe()}")
                val priceAmount = pkg.product.price.amountMicros / 1_000_000.0
                val priceCurrency = pkg.product.price.currencyCode
                val productId = pkg.product.id
                analyticsTracker.logBeginCheckout(
                    entitlement = analyticsEntitlement,
                    value = priceAmount,
                    currency = priceCurrency,
                )
                Purchases.sharedInstance.purchaseWith(
                    com.revenuecat.purchases.PurchaseParams.Builder(activity, pkg).build(),
                    { error, userCancelled ->
                        if (userCancelled) {
                            analyticsTracker.logEvent(
                                AnalyticsEvents.PURCHASE_CANCELLED,
                                mapOf(AnalyticsEvents.Param.ENTITLEMENT to analyticsEntitlement),
                            )
                            crashReporter.log("RevenueCat Android purchase cancelled entitlement=$entitlementId")
                        } else {
                            analyticsTracker.logEvent(
                                AnalyticsEvents.PURCHASE_FAILED,
                                mapOf(
                                    AnalyticsEvents.Param.ENTITLEMENT to analyticsEntitlement,
                                    AnalyticsEvents.Param.ERROR_TYPE to AnalyticsEvents.ErrorType.OTHER,
                                ),
                            )
                            crashReporter.recordException(
                                error.asThrowable("purchase failed entitlement=$entitlementId"),
                            )
                        }
                        cont.resume(
                            Result.failure(
                                if (userCancelled) PurchaseFailure.Cancelled else error.toPurchaseFailure(),
                            ),
                        )
                    },
                    { transaction, customerInfo ->
                        mirror(customerInfo)
                        val isActive = customerInfo.entitlements[entitlementId]?.isActive == true
                        if (isActive) {
                            analyticsTracker.logPurchase(
                                entitlement = analyticsEntitlement,
                                transactionId = transaction.productIdOrFallback(productId),
                                value = priceAmount,
                                currency = priceCurrency,
                            )
                            crashReporter.log(
                                "RevenueCat Android purchase success entitlement=$entitlementId " +
                                    "removeAds=${_hasRemovedAds.value} chatPremium=${_hasChatPremium.value}",
                            )
                            cont.resume(Result.success(Unit))
                        } else {
                            analyticsTracker.logEvent(
                                AnalyticsEvents.PURCHASE_FAILED,
                                mapOf(
                                    AnalyticsEvents.Param.ENTITLEMENT to analyticsEntitlement,
                                    AnalyticsEvents.Param.ERROR_TYPE to "entitlement_not_active",
                                ),
                            )
                            crashReporter.recordException(
                                IllegalStateException(
                                    "RevenueCat Android purchase completed but entitlement=$entitlementId inactive; " +
                                        "customerEntitlements=${customerInfo.entitlements.active.keys}",
                                ),
                            )
                            cont.resume(Result.failure(PurchaseFailure.EntitlementNotActive(entitlementId)))
                        }
                    },
                )
            }
        }
    }

    private suspend fun fetchOfferings(): Result<com.revenuecat.purchases.Offerings?> =
        suspendCancellableCoroutine { cont ->
            Purchases.sharedInstance.getOfferingsWith(
                { error ->
                    crashReporter.recordException(error.asThrowable("getOfferings failed"))
                    cont.resume(Result.failure(error.toPurchaseFailure()))
                },
                { offerings ->
                    crashReporter.log("RevenueCat Android offerings fetched ${offerings.describe()}")
                    cont.resume(Result.success(offerings))
                },
            )
        }

    private fun com.revenuecat.purchases.Offerings.packageForOffering(offeringId: String?): Package? =
        (if (offeringId == null) current else this[offeringId])?.availablePackages?.firstOrNull()

    private fun missingPackageMessage(offeringId: String?, offerings: com.revenuecat.purchases.Offerings?): String {
        val offeringIds = offerings?.all?.keys?.sorted()?.joinToString().orEmpty()
        val packages = offerings?.all
            ?.values
            ?.flatMap { it.availablePackages }
            ?.joinToString { it.describe() }
            .orEmpty()
        return "No RevenueCat package configured for offering=${offeringId ?: "current"} " +
            "offerings=[$offeringIds] packages=[$packages]"
    }

    private fun com.revenuecat.purchases.Offerings.describe(): String {
        val offeringIds = all.keys.sorted().joinToString()
        val packages = all.values.flatMap { it.availablePackages }.joinToString { it.describe() }
        return "offerings=[$offeringIds] packages=[$packages]"
    }

    private fun Package.describe(): String =
        "${presentedOfferingContext.offeringIdentifier.orEmpty()}:$identifier:${product.id}"

    private fun mirror(info: CustomerInfo) {
        _hasRemovedAds.value = info.entitlements[AD_FREE_ENTITLEMENT]?.isActive == true
        _hasChatPremium.value = info.entitlements[CHAT_PREMIUM_ENTITLEMENT]?.isActive == true
        crashReporter.log(
            "RevenueCat Android entitlement update adFree=${_hasRemovedAds.value} " +
                "chatPremium=${_hasChatPremium.value}",
        )
    }

    private fun CustomerInfo.hasAnyRestorableEntitlement(): Boolean =
        entitlements[AD_FREE_ENTITLEMENT]?.isActive == true ||
            entitlements[CHAT_PREMIUM_ENTITLEMENT]?.isActive == true

    private fun PurchasesError.asThrowable(context: String? = null): Throwable =
        RuntimeException("RevenueCat Android ${context.orEmpty()}: $code ${underlyingErrorMessage ?: message}")

    private fun PurchasesError.toPurchaseFailure(): PurchaseFailure = when (code) {
        PurchasesErrorCode.PurchaseCancelledError -> PurchaseFailure.Cancelled
        PurchasesErrorCode.NetworkError -> PurchaseFailure.Network
        PurchasesErrorCode.ProductAlreadyPurchasedError -> PurchaseFailure.AlreadyOwned
        else -> PurchaseFailure.Unknown(message)
    }

    private fun StoreTransaction?.productIdOrFallback(fallback: String): String =
        this?.productIds?.firstOrNull() ?: fallback

    private companion object {
        const val AD_FREE_ENTITLEMENT = "ad_free"
        const val CHAT_PREMIUM_ENTITLEMENT = "chat_premium"
        const val CHAT_PREMIUM_OFFERING_ID = "chat_premium"
    }
}
