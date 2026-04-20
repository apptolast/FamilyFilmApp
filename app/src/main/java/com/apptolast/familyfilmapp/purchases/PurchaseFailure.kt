package com.apptolast.familyfilmapp.purchases

/**
 * Typed error hierarchy for the purchase flow, so callers don't need to
 * parse error messages (previously we did `message.contains("cancel")` which
 * was fragile and also mis-classified test-store "simulated failure" events).
 *
 * Wrapped as [Exception] so it fits inside [kotlin.Result].
 */
sealed class PurchaseFailure(message: String? = null, cause: Throwable? = null) : Exception(message, cause) {

    /** User tapped "Cancel" in the Play / Test-store dialog. */
    object Cancelled : PurchaseFailure("Purchase cancelled by user")

    /** `Purchases.configure` was never called (missing API key or no user logged in). */
    object NotConfigured : PurchaseFailure("RevenueCat is not configured")

    /** The requested offering has no packages, or the offering id does not exist. */
    data class OfferingUnavailable(val offeringId: String?) :
        PurchaseFailure(
            "No packages available in offering '${offeringId ?: "current"}'",
        )

    /**
     * Play reported a successful receipt, but RevenueCat's `CustomerInfo` still shows
     * the entitlement as inactive — typically a Dashboard mis-configuration
     * (product → entitlement mapping) or a webhook delay. The caller should treat this
     * as a failure and NOT grant premium features locally, because the server-side
     * quota enforcement keys off the same entitlement.
     */
    data class EntitlementNotActive(val entitlementId: String) :
        PurchaseFailure(
            "Purchase completed but entitlement '$entitlementId' is not active",
        )

    /** Anything else RevenueCat returned as an error. */
    data class Unknown(val detail: String?) : PurchaseFailure(detail ?: "Unknown purchase error")
}
