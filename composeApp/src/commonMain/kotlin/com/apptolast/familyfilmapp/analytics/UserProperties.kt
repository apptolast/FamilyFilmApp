package com.apptolast.familyfilmapp.analytics

/**
 * Centralised catalogue of Firebase Analytics user property names.
 *
 * Snake_case, max 24 chars (Firebase limit). NEVER store PII (email, username text);
 * the Firebase user UID is OK because it is an opaque identifier expected by Analytics.
 */
object UserProperties {
    const val HAS_REMOVED_ADS = "has_removed_ads"
    const val HAS_CHAT_PREMIUM = "has_chat_premium"
    const val GROUPS_COUNT = "groups_count"
    const val IS_EMAIL_VERIFIED = "is_email_verified"
}
