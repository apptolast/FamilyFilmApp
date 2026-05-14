package com.apptolast.familyfilmapp.purchases

// Extends Throwable so `Result.failure(PurchaseFailure.X)` flows through runCatching.
sealed class PurchaseFailure(message: String) : Throwable(message) {
    data object Cancelled : PurchaseFailure("Purchase cancelled by user")
    data class Generic(val reason: String) : PurchaseFailure(reason)
    data object Network : PurchaseFailure("Network error during purchase")
    data object AlreadyOwned : PurchaseFailure("Entitlement already owned")
}
