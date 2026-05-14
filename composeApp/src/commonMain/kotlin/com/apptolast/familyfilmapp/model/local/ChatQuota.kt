package com.apptolast.familyfilmapp.model.local

/**
 * Monthly quota state as mirrored in Firestore `users/{uid}/chat_usage/{YYYY-MM}`.
 *
 * Cloud Function writes this doc after each successful call. The client observes it to render
 * the "X/Y remaining" banner. [isPremium] comes from the entitlement mirror and determines
 * [limit] (5 for free, 50 for premium).
 */
data class ChatQuota(val count: Int, val limit: Int, val isPremium: Boolean) {
    val remaining: Int get() = (limit - count).coerceAtLeast(0)
    val isExceeded: Boolean get() = count >= limit
}
