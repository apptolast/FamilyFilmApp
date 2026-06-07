package com.apptolast.familyfilmapp.model.local

// Mirrored in Firestore users/{uid}/chat_usage/{YYYY-MM} — written by the Cloud Function.
data class ChatQuota(val count: Int, val limit: Int, val isPremium: Boolean) {
    val remaining: Int get() = (limit - count).coerceAtLeast(0)
    val isExceeded: Boolean get() = count >= limit
}
