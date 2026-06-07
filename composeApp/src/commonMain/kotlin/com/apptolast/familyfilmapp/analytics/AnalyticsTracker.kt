package com.apptolast.familyfilmapp.analytics

// Privacy: NEVER pass free-text user content (queries, prompts, emails). Metadata only.
interface AnalyticsTracker {

    fun setEnabled(enabled: Boolean)
    fun setUserId(userId: String?)
    fun setUserProperty(name: String, value: String?)
    fun setConsent(analyticsGranted: Boolean, adsGranted: Boolean)

    // Values must be String/Int/Long/Double/Boolean; others are skipped. Truncated to Firebase limits.
    fun logEvent(name: String, params: Map<String, Any?> = emptyMap())

    fun logScreenView(screenName: String, screenClass: String? = null)

    fun logLogin(method: String)

    fun logSignUp(method: String)

    fun logSearch(queryLength: Int, resultsCount: Int, filter: String)

    fun logSelectContent(contentType: String, itemId: String, source: String? = null)

    fun logBeginCheckout(entitlement: String, value: Double?, currency: String?)

    fun logPurchase(entitlement: String, transactionId: String?, value: Double?, currency: String?)
}
