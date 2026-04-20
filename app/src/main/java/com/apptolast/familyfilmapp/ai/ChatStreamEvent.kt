package com.apptolast.familyfilmapp.ai

/**
 * Events emitted while streaming a Gemini response.
 *
 * The UI layer collapses [Delta] events into a single growing string in memory,
 * then persists the final message on [Completed]. One Room write per message, not per token.
 *
 * [QuotaExceeded] is reserved for phase 3 when the Cloud Function enforces the per-month
 * limit. In phase 1 (client-side MVP) it is unused.
 */
sealed interface ChatStreamEvent {
    data object Started : ChatStreamEvent
    data class Delta(val token: String) : ChatStreamEvent
    data class Completed(val fullText: String) : ChatStreamEvent
    data class Failed(val error: Throwable) : ChatStreamEvent
    data object QuotaExceeded : ChatStreamEvent
}
