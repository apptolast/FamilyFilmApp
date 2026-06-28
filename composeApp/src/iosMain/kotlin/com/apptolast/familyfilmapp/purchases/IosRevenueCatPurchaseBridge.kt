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

    /**
     * Resolves localized pricing for the Chat Premium subscription. `periodUnit` is one of
     * "DAY"/"WEEK"/"MONTH"/"YEAR" (or null), so the shared UI can format it consistently.
     */
    fun fetchChatPremiumPricing(
        completion: (
            priceString: String?,
            periodUnit: String?,
            periodCount: Int,
            errorMessage: String?,
        ) -> Unit,
    )
}
