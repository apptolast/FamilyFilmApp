package com.apptolast.familyfilmapp.ai

sealed interface ChatStreamEvent {
    data object Started : ChatStreamEvent
    data class Delta(val token: String) : ChatStreamEvent
    data class Completed(val fullText: String) : ChatStreamEvent
    data class Failed(val error: Throwable) : ChatStreamEvent
    data object QuotaExceeded : ChatStreamEvent
}
