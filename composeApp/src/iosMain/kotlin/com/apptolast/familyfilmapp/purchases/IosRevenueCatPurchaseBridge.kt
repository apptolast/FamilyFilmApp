package com.apptolast.familyfilmapp.purchases

interface IosRevenueCatPurchaseBridge {
    fun logIn(
        userId: String,
        completion: (hasRemovedAds: Boolean, hasChatPremium: Boolean, errorMessage: String?) -> Unit,
    )

    fun logOut(completion: (hasRemovedAds: Boolean, hasChatPremium: Boolean, errorMessage: String?) -> Unit)
    fun purchase(
        entitlement: String,
        completion: (
            hasRemovedAds: Boolean,
            hasChatPremium: Boolean,
            errorMessage: String?,
            userCancelled: Boolean,
        ) -> Unit,
    )

    fun restore(completion: (hasRemovedAds: Boolean, hasChatPremium: Boolean, errorMessage: String?) -> Unit)
}
