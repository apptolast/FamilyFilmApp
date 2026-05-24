package com.apptolast.familyfilmapp.purchases

// Extends Throwable so `Result.failure(PurchaseFailure.X)` flows through runCatching.
sealed class PurchaseFailure(message: String? = null, cause: Throwable? = null) : Throwable(message, cause) {
    data object Cancelled : PurchaseFailure("Purchase cancelled by user")
    data object Network : PurchaseFailure("Network error during purchase")
    data object AlreadyOwned : PurchaseFailure("Entitlement already owned")
    data object NotConfigured : PurchaseFailure("RevenueCat is not configured")
    data class OfferingUnavailable(val offeringId: String?) :
        PurchaseFailure("No packages available in offering '${offeringId ?: "current"}'")

    data class EntitlementNotActive(val entitlementId: String) :
        PurchaseFailure("Purchase completed but entitlement '$entitlementId' is not active")

    data class Unknown(val detail: String?) : PurchaseFailure(detail ?: "Unknown purchase error")
    data class Generic(val reason: String) : PurchaseFailure(reason)
}
