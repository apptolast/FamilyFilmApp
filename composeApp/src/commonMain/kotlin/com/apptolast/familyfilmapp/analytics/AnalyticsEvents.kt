package com.apptolast.familyfilmapp.analytics

object AnalyticsEvents {

    // Auth
    const val SIGN_UP_FAILED = "sign_up_failed"
    const val LOGIN_FAILED = "login_failed"
    const val EMAIL_VERIFIED = "email_verified"
    const val LOGOUT = "logout"
    const val ACCOUNT_DELETED = "account_deleted"
    const val PASSWORD_RECOVERY_SENT = "password_recovery_sent"

    // Content / discovery
    const val FILTER_CHANGED = "filter_changed"
    const val MARK_AS_WATCHED = "mark_as_watched"
    const val REMOVE_FROM_LIST = "remove_from_list"

    // Groups
    const val GROUP_CREATED = "group_created"
    const val GROUP_DELETED = "group_deleted"
    const val GROUP_SWITCHED = "group_switched"
    const val GROUP_RENAMED = "group_renamed"
    const val GROUP_MEMBER_ADDED = "group_member_added"
    const val GROUP_MEMBER_REMOVED = "group_member_removed"
    const val GROUP_INVITE_SHARED = "group_invite_shared"

    // Chat
    const val CHAT_SESSION_STARTED = "chat_session_started"
    const val CHAT_MESSAGE_SENT = "chat_message_sent"
    const val CHAT_QUOTA_EXHAUSTED = "chat_quota_exhausted"
    const val CHAT_STREAM_FAILED = "chat_stream_failed"
    const val CHAT_HISTORY_CLEARED = "chat_history_cleared"

    // Monetization
    const val PAYWALL_SHOWN = "paywall_shown"
    const val PURCHASE_CANCELLED = "purchase_cancelled"
    const val PURCHASE_FAILED = "purchase_failed"
    const val RESTORE_PURCHASE = "restore_purchase"

    // Profile actions
    const val USERNAME_CHANGED = "username_changed"
    const val RATE_APP_TAPPED = "rate_app_tapped"

    /** Custom event parameter keys (snake_case, max 40 chars). */
    object Param {
        const val METHOD = "method"
        const val ERROR_TYPE = "error_type"
        const val QUERY_LENGTH = "query_length"
        const val RESULTS_COUNT = "results_count"
        const val FILTER = "filter"
        const val CONTENT_TYPE = "content_type"
        const val ITEM_ID = "item_id"
        const val SOURCE = "source"
        const val GROUP_COUNT = "group_count"
        const val PREVIOUS_STATUS = "previous_status"
        const val IS_PREMIUM = "is_premium"
        const val PROMPT_LENGTH = "prompt_length"
        const val HISTORY_SIZE = "history_size"
        const val QUOTA_REMAINING = "quota_remaining"
        const val MESSAGES_COUNT = "messages_count"
        const val ENTITLEMENT = "entitlement"
        const val ENTRY_POINT = "entry_point"
        const val RESULT = "result"
    }

    /** Common values for the [Param.METHOD] parameter. */
    object Method {
        const val EMAIL = "email"
        const val GOOGLE = "google"
    }

    /** Common values for the [Param.ERROR_TYPE] parameter — keep this list closed. */
    object ErrorType {
        const val NETWORK = "network"
        const val INVALID_CREDENTIALS = "invalid_credentials"
        const val USER_DISABLED = "user_disabled"
        const val ALREADY_EXISTS = "already_exists"
        const val WEAK_PASSWORD = "weak_password"
        const val CANCELLED = "cancelled"
        const val QUOTA = "quota"
        const val GENERIC = "generic"
        const val OTHER = "other"
    }

    /** Values for the [Param.ENTITLEMENT] parameter. */
    object Entitlement {
        const val REMOVE_ADS = "remove_ads"
        const val CHAT_PREMIUM = "chat_premium"
    }

    /** Values for the [Param.ENTRY_POINT] parameter on `paywall_shown`. */
    object EntryPoint {
        const val QUOTA_EXHAUSTED = "quota_exhausted"
        const val PROFILE_REMOVE_ADS = "profile_remove_ads"
        const val PROFILE_CHAT_PREMIUM = "profile_chat_premium"
        const val CHAT_MANUAL_UPSELL = "chat_manual_upsell"
    }

    /** Values for the [Param.CONTENT_TYPE] parameter. */
    object ContentType {
        const val MOVIE = "movie"
        const val TV_SHOW = "tv_show"
    }

    /** Values for the [Param.RESULT] parameter on `restore_purchase`. */
    object RestoreResult {
        const val SUCCESS = "success"
        const val NOTHING_FOUND = "nothing_found"
        const val ERROR = "error"
    }

    // Firebase built-in names inlined here because GitLive doesn't expose the SDK enums.
    object Standard {
        const val EVENT_VIEW_ITEM = "view_item"
        const val EVENT_ADD_TO_WISHLIST = "add_to_wishlist"
        const val PARAM_CONTENT_TYPE = "content_type"
        const val PARAM_ITEM_ID = "item_id"
    }
}
