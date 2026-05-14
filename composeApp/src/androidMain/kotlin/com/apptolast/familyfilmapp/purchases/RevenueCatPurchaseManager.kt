package com.apptolast.familyfilmapp.purchases

import android.content.Context
import com.apptolast.familyfilmapp.BuildConfig
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
import com.revenuecat.purchases.purchaseWith
import com.revenuecat.purchases.restorePurchasesWith
import kotlin.coroutines.resume
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine

class RevenueCatPurchaseManager(
    private val context: Context,
    private val activityHolder: CurrentActivityHolder,
    private val crashReporter: CrashReporter,
) : PurchaseManager {

    private val _hasRemovedAds = MutableStateFlow(false)
    override val hasRemovedAds: StateFlow<Boolean> = _hasRemovedAds.asStateFlow()

    private val _hasChatPremium = MutableStateFlow(false)
    override val hasChatPremium: StateFlow<Boolean> = _hasChatPremium.asStateFlow()

    private var configured: Boolean = false

    override suspend fun initialize(userId: String) {
        if (!configured) {
            Purchases.logLevel = LogLevel.WARN
            Purchases.configure(
                PurchasesConfiguration.Builder(context, BuildConfig.REVENUECAT_PLAY_SDK_KEY)
                    .appUserID(userId)
                    .build(),
            )
            Purchases.sharedInstance.updatedCustomerInfoListener =
                UpdatedCustomerInfoListener { info -> mirror(info) }
            configured = true
        } else {
            Purchases.sharedInstance.logInWith(
                userId,
                { error -> crashReporter.recordException(error.asThrowable()) },
                { info, _ -> mirror(info) },
            )
        }
        // Pull current state once.
        Purchases.sharedInstance.getCustomerInfoWith(
            { error -> crashReporter.recordException(error.asThrowable()) },
            { info -> mirror(info) },
        )
    }

    override fun setAdsRemoved(value: Boolean) {
        // RevenueCat is the source of truth; we update the local flow so the Firestore mirror shows immediately.
        _hasRemovedAds.value = value
    }

    override fun logout() {
        if (!configured) return
        Purchases.sharedInstance.logOutWith(
            { error -> crashReporter.recordException(error.asThrowable()) },
            { info -> mirror(info) },
        )
        _hasRemovedAds.value = false
        _hasChatPremium.value = false
    }

    override suspend fun purchaseRemoveAds(): Result<Unit> = purchaseEntitlement(ENTITLEMENT_REMOVE_ADS)

    override suspend fun purchaseChatPremium(): Result<Unit> = purchaseEntitlement(ENTITLEMENT_CHAT_PREMIUM)

    override suspend fun restorePurchases(): Result<Boolean> = suspendCancellableCoroutine { cont ->
        Purchases.sharedInstance.restorePurchasesWith(
            { error -> cont.resume(Result.failure(error.toPurchaseFailure())) },
            { info ->
                mirror(info)
                cont.resume(Result.success(info.entitlements.active.containsKey(ENTITLEMENT_REMOVE_ADS)))
            },
        )
    }

    private suspend fun purchaseEntitlement(entitlement: String): Result<Unit> {
        val activity = activityHolder.current
            ?: return Result.failure(PurchaseFailure.Generic("No Activity available for purchase"))
        val offerings = runCatching { fetchOfferings() }.getOrElse { return Result.failure(it) }
        val pkg = offerings?.findPackageForEntitlement(entitlement)
            ?: return Result.failure(PurchaseFailure.Generic("No package configured for $entitlement"))

        return suspendCancellableCoroutine { cont ->
            Purchases.sharedInstance.purchaseWith(
                com.revenuecat.purchases.PurchaseParams.Builder(activity, pkg).build(),
                { error, userCancelled ->
                    cont.resume(
                        Result.failure(
                            if (userCancelled) PurchaseFailure.Cancelled else error.toPurchaseFailure(),
                        ),
                    )
                },
                { _, customerInfo ->
                    mirror(customerInfo)
                    cont.resume(Result.success(Unit))
                },
            )
        }
    }

    private suspend fun fetchOfferings(): com.revenuecat.purchases.Offerings? =
        suspendCancellableCoroutine { cont ->
            Purchases.sharedInstance.getOfferingsWith(
                { error ->
                    cont.resume(null)
                    crashReporter.recordException(error.asThrowable())
                },
                { offerings -> cont.resume(offerings) },
            )
        }

    private fun com.revenuecat.purchases.Offerings.findPackageForEntitlement(entitlement: String): Package? =
        current?.availablePackages?.firstOrNull { pkg ->
            pkg.product.subscriptionOptions?.any { true } == true ||
                pkg.identifier.contains(entitlement, ignoreCase = true)
        }

    private fun mirror(info: CustomerInfo) {
        _hasRemovedAds.value = info.entitlements.active.containsKey(ENTITLEMENT_REMOVE_ADS)
        _hasChatPremium.value = info.entitlements.active.containsKey(ENTITLEMENT_CHAT_PREMIUM)
    }

    private fun PurchasesError.asThrowable(): Throwable =
        RuntimeException("RevenueCat error: $code ${underlyingErrorMessage ?: message}")

    private fun PurchasesError.toPurchaseFailure(): PurchaseFailure = when (code) {
        PurchasesErrorCode.PurchaseCancelledError -> PurchaseFailure.Cancelled
        PurchasesErrorCode.NetworkError -> PurchaseFailure.Network
        PurchasesErrorCode.ProductAlreadyPurchasedError -> PurchaseFailure.AlreadyOwned
        else -> PurchaseFailure.Generic(message)
    }

    private companion object {
        const val ENTITLEMENT_REMOVE_ADS = "remove_ads"
        const val ENTITLEMENT_CHAT_PREMIUM = "chat_premium"
    }
}
