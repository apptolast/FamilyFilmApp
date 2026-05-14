package com.apptolast.familyfilmapp.purchases

/**
 * Closed set of purchase failure categories the UI layer cares about.
 *
 * Subclass of [Throwable] so `Result.failure(PurchaseFailure.X)` plays
 * nicely with the rest of the codebase's `runCatching { ... }` flow.
 */
sealed class PurchaseFailure(message: String) : Throwable(message) {
    data object Cancelled : PurchaseFailure("Purchase cancelled by user")
    data class Generic(val reason: String) : PurchaseFailure(reason)
    data object Network : PurchaseFailure("Network error during purchase")
    data object AlreadyOwned : PurchaseFailure("Entitlement already owned")
}
