package com.apptolast.familyfilmapp.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAnalyticsTracker @Inject constructor(private val firebaseAnalytics: FirebaseAnalytics) :
    AnalyticsTracker {

    override fun setEnabled(enabled: Boolean) {
        firebaseAnalytics.setAnalyticsCollectionEnabled(enabled)
    }

    override fun setUserId(userId: String?) {
        firebaseAnalytics.setUserId(userId)
    }

    override fun setUserProperty(name: String, value: String?) {
        firebaseAnalytics.setUserProperty(name.take(MAX_PROPERTY_NAME_LENGTH), value?.take(MAX_VALUE_LENGTH))
    }

    override fun setConsent(analyticsGranted: Boolean, adsGranted: Boolean) {
        val consent = mapOf(
            FirebaseAnalytics.ConsentType.ANALYTICS_STORAGE to analyticsGranted.toConsentStatus(),
            FirebaseAnalytics.ConsentType.AD_STORAGE to adsGranted.toConsentStatus(),
            FirebaseAnalytics.ConsentType.AD_USER_DATA to adsGranted.toConsentStatus(),
            FirebaseAnalytics.ConsentType.AD_PERSONALIZATION to adsGranted.toConsentStatus(),
        )
        firebaseAnalytics.setConsent(consent)
    }

    override fun logEvent(name: String, params: Map<String, Any?>) {
        val safeName = name.take(MAX_NAME_LENGTH)
        val bundle = params.toBundle()
        firebaseAnalytics.logEvent(safeName, bundle)
    }

    override fun logScreenView(screenName: String, screenClass: String?) {
        logEvent(
            FirebaseAnalytics.Event.SCREEN_VIEW,
            buildMap {
                put(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
                if (screenClass != null) put(FirebaseAnalytics.Param.SCREEN_CLASS, screenClass)
            },
        )
    }

    override fun logLogin(method: String) {
        logEvent(FirebaseAnalytics.Event.LOGIN, mapOf(FirebaseAnalytics.Param.METHOD to method))
    }

    override fun logSignUp(method: String) {
        logEvent(FirebaseAnalytics.Event.SIGN_UP, mapOf(FirebaseAnalytics.Param.METHOD to method))
    }

    override fun logSearch(queryLength: Int, resultsCount: Int, filter: String) {
        logEvent(
            FirebaseAnalytics.Event.SEARCH,
            mapOf(
                AnalyticsEvents.Param.QUERY_LENGTH to queryLength.toLong(),
                AnalyticsEvents.Param.RESULTS_COUNT to resultsCount.toLong(),
                AnalyticsEvents.Param.FILTER to filter,
            ),
        )
    }

    override fun logSelectContent(contentType: String, itemId: String, source: String?) {
        logEvent(
            FirebaseAnalytics.Event.SELECT_CONTENT,
            buildMap {
                put(FirebaseAnalytics.Param.CONTENT_TYPE, contentType)
                put(FirebaseAnalytics.Param.ITEM_ID, itemId)
                if (source != null) put(AnalyticsEvents.Param.SOURCE, source)
            },
        )
    }

    override fun logBeginCheckout(entitlement: String, value: Double?, currency: String?) {
        logEvent(
            FirebaseAnalytics.Event.BEGIN_CHECKOUT,
            buildMap {
                put(AnalyticsEvents.Param.ENTITLEMENT, entitlement)
                if (value != null) put(FirebaseAnalytics.Param.VALUE, value)
                if (currency != null) put(FirebaseAnalytics.Param.CURRENCY, currency)
            },
        )
    }

    override fun logPurchase(entitlement: String, transactionId: String?, value: Double?, currency: String?) {
        logEvent(
            FirebaseAnalytics.Event.PURCHASE,
            buildMap {
                put(AnalyticsEvents.Param.ENTITLEMENT, entitlement)
                if (transactionId != null) put(FirebaseAnalytics.Param.TRANSACTION_ID, transactionId)
                if (value != null) put(FirebaseAnalytics.Param.VALUE, value)
                if (currency != null) put(FirebaseAnalytics.Param.CURRENCY, currency)
            },
        )
    }

    private fun Map<String, Any?>.toBundle(): Bundle {
        val bundle = Bundle()
        entries.take(MAX_PARAMS).forEach { (key, raw) ->
            val safeKey = key.take(MAX_PARAM_NAME_LENGTH)
            when (raw) {
                is String -> bundle.putString(safeKey, raw.take(MAX_VALUE_LENGTH))
                is Int -> bundle.putLong(safeKey, raw.toLong())
                is Long -> bundle.putLong(safeKey, raw)
                is Double -> bundle.putDouble(safeKey, raw)
                is Float -> bundle.putDouble(safeKey, raw.toDouble())
                is Boolean -> bundle.putString(safeKey, raw.toString())
                null -> Unit
                else -> bundle.putString(safeKey, raw.toString().take(MAX_VALUE_LENGTH))
            }
        }
        return bundle
    }

    private fun Boolean.toConsentStatus(): FirebaseAnalytics.ConsentStatus =
        if (this) FirebaseAnalytics.ConsentStatus.GRANTED else FirebaseAnalytics.ConsentStatus.DENIED

    private companion object {
        // Firebase Analytics hard limits
        const val MAX_NAME_LENGTH = 40
        const val MAX_PARAM_NAME_LENGTH = 40
        const val MAX_PROPERTY_NAME_LENGTH = 24
        const val MAX_VALUE_LENGTH = 100
        const val MAX_PARAMS = 25
    }
}
