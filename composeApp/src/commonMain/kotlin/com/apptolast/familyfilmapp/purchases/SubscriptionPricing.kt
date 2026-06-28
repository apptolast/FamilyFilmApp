package com.apptolast.familyfilmapp.purchases

/**
 * Localized pricing for a store subscription, resolved at runtime from RevenueCat/StoreKit.
 * Exposed to the UI so the paywall can show the actual billed amount and billing period,
 * as required by App Store Review Guideline 3.1.2(c).
 */
data class SubscriptionPricing(
    /** Localized billed amount in the user's currency, e.g. "€4.99". */
    val priceString: String,
    val periodUnit: PeriodUnit,
    val periodCount: Int,
)

enum class PeriodUnit { DAY, WEEK, MONTH, YEAR, UNKNOWN }
