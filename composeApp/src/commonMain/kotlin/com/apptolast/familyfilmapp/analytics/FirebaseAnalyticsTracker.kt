package com.apptolast.familyfilmapp.analytics

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.analytics.FirebaseAnalytics
import dev.gitlive.firebase.analytics.analytics

// Standard event/param names are hard-coded ([Std]) since GitLive doesn't expose the Java enums.
class FirebaseAnalyticsTracker : AnalyticsTracker {

    private val analytics get() = Firebase.analytics

    override fun setEnabled(enabled: Boolean) {
        analytics.setAnalyticsCollectionEnabled(enabled)
    }

    override fun setUserId(userId: String?) {
        analytics.setUserId(userId)
    }

    override fun setUserProperty(name: String, value: String?) {
        // GitLive doesn't accept null; empty string clears the property in the native SDK.
        analytics.setUserProperty(
            name = name.take(MAX_PROPERTY_NAME_LENGTH),
            value = value?.take(MAX_VALUE_LENGTH).orEmpty(),
        )
    }

    override fun setConsent(analyticsGranted: Boolean, adsGranted: Boolean) {
        analytics.setConsent(
            mapOf(
                FirebaseAnalytics.ConsentType.ANALYTICS_STORAGE to analyticsGranted.toConsent(),
                FirebaseAnalytics.ConsentType.AD_STORAGE to adsGranted.toConsent(),
                FirebaseAnalytics.ConsentType.AD_USER_DATA to adsGranted.toConsent(),
                FirebaseAnalytics.ConsentType.AD_PERSONALIZATION to adsGranted.toConsent(),
            ),
        )
    }

    override fun logEvent(name: String, params: Map<String, Any?>) {
        analytics.logEvent(
            name = name.take(MAX_NAME_LENGTH),
            parameters = params.toAnalyticsParams(),
        )
    }

    override fun logScreenView(screenName: String, screenClass: String?) {
        logEvent(
            Std.SCREEN_VIEW,
            buildMap {
                put(Std.PARAM_SCREEN_NAME, screenName)
                if (screenClass != null) put(Std.PARAM_SCREEN_CLASS, screenClass)
            },
        )
    }

    override fun logLogin(method: String) {
        logEvent(Std.LOGIN, mapOf(Std.PARAM_METHOD to method))
    }

    override fun logSignUp(method: String) {
        logEvent(Std.SIGN_UP, mapOf(Std.PARAM_METHOD to method))
    }

    override fun logSearch(queryLength: Int, resultsCount: Int, filter: String) {
        logEvent(
            Std.SEARCH,
            mapOf(
                AnalyticsEvents.Param.QUERY_LENGTH to queryLength.toLong(),
                AnalyticsEvents.Param.RESULTS_COUNT to resultsCount.toLong(),
                AnalyticsEvents.Param.FILTER to filter,
            ),
        )
    }

    override fun logSelectContent(contentType: String, itemId: String, source: String?) {
        logEvent(
            Std.SELECT_CONTENT,
            buildMap {
                put(Std.PARAM_CONTENT_TYPE, contentType)
                put(Std.PARAM_ITEM_ID, itemId)
                if (source != null) put(AnalyticsEvents.Param.SOURCE, source)
            },
        )
    }

    override fun logBeginCheckout(entitlement: String, value: Double?, currency: String?) {
        logEvent(
            Std.BEGIN_CHECKOUT,
            buildMap {
                put(AnalyticsEvents.Param.ENTITLEMENT, entitlement)
                if (value != null) put(Std.PARAM_VALUE, value)
                if (currency != null) put(Std.PARAM_CURRENCY, currency)
            },
        )
    }

    override fun logPurchase(entitlement: String, transactionId: String?, value: Double?, currency: String?) {
        logEvent(
            Std.PURCHASE,
            buildMap {
                put(AnalyticsEvents.Param.ENTITLEMENT, entitlement)
                if (transactionId != null) put(Std.PARAM_TRANSACTION_ID, transactionId)
                if (value != null) put(Std.PARAM_VALUE, value)
                if (currency != null) put(Std.PARAM_CURRENCY, currency)
            },
        )
    }

    private fun Map<String, Any?>.toAnalyticsParams(): Map<String, Any> = buildMap {
        this@toAnalyticsParams.entries.take(MAX_PARAMS).forEach { (key, raw) ->
            val safeKey = key.take(MAX_PARAM_NAME_LENGTH)
            val value: Any? = when (raw) {
                is String -> raw.take(MAX_VALUE_LENGTH)
                is Int -> raw.toLong()
                is Long -> raw
                is Double -> raw
                is Float -> raw.toDouble()
                is Boolean -> raw.toString()
                null -> null
                else -> raw.toString().take(MAX_VALUE_LENGTH)
            }
            if (value != null) put(safeKey, value)
        }
    }

    private fun Boolean.toConsent(): FirebaseAnalytics.ConsentStatus =
        if (this) FirebaseAnalytics.ConsentStatus.GRANTED else FirebaseAnalytics.ConsentStatus.DENIED

    private object Std {
        // Standard Firebase Analytics event names
        const val SCREEN_VIEW = "screen_view"
        const val LOGIN = "login"
        const val SIGN_UP = "sign_up"
        const val SEARCH = "search"
        const val SELECT_CONTENT = "select_content"
        const val BEGIN_CHECKOUT = "begin_checkout"
        const val PURCHASE = "purchase"

        // Standard Firebase Analytics param names
        const val PARAM_SCREEN_NAME = "screen_name"
        const val PARAM_SCREEN_CLASS = "screen_class"
        const val PARAM_METHOD = "method"
        const val PARAM_CONTENT_TYPE = "content_type"
        const val PARAM_ITEM_ID = "item_id"
        const val PARAM_TRANSACTION_ID = "transaction_id"
        const val PARAM_VALUE = "value"
        const val PARAM_CURRENCY = "currency"
    }

    private companion object {
        // Firebase Analytics hard limits
        const val MAX_NAME_LENGTH = 40
        const val MAX_PARAM_NAME_LENGTH = 40
        const val MAX_PROPERTY_NAME_LENGTH = 24
        const val MAX_VALUE_LENGTH = 100
        const val MAX_PARAMS = 25
    }
}
