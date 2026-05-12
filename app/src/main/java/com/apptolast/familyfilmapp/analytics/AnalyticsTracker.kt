package com.apptolast.familyfilmapp.analytics

/**
 * Application-wide analytics façade. Wraps Firebase Analytics so feature code never depends
 * on the SDK directly — easier to mock in tests and to swap backends.
 *
 * Privacy policy: callers must NEVER pass free-text user content (search queries, chat
 * prompts, emails, usernames). Pass metadata only (length, category, opaque IDs).
 */
interface AnalyticsTracker {

    /** Enables or disables event collection. Disabled in debug builds by default. */
    fun setEnabled(enabled: Boolean)

    /** Sets the Firebase Analytics user identifier. Pass `null` on logout. */
    fun setUserId(userId: String?)

    /** Sets a user property. Pass `null` to clear it. */
    fun setUserProperty(name: String, value: String?)

    /**
     * Maps UMP consent state to Firebase Analytics consent (`ANALYTICS_STORAGE`,
     * `AD_STORAGE`). Called from the consent manager after UMP resolves.
     */
    fun setConsent(analyticsGranted: Boolean, adsGranted: Boolean)

    /**
     * Logs a custom event. [params] values must be String, Int, Long, Double or Boolean —
     * other types are skipped. Param keys and string values are truncated to Firebase limits.
     */
    fun logEvent(name: String, params: Map<String, Any?> = emptyMap())

    // Typed helpers for the most common events.

    fun logScreenView(screenName: String, screenClass: String? = null)

    fun logLogin(method: String)

    fun logSignUp(method: String)

    fun logSearch(queryLength: Int, resultsCount: Int, filter: String)

    fun logSelectContent(contentType: String, itemId: String, source: String? = null)

    fun logBeginCheckout(entitlement: String, value: Double?, currency: String?)

    fun logPurchase(entitlement: String, transactionId: String?, value: Double?, currency: String?)
}
