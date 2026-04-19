package com.apptolast.familyfilmapp.repositories

import com.apptolast.familyfilmapp.ai.ChatStreamEvent
import com.apptolast.familyfilmapp.model.local.ChatMessage
import com.apptolast.familyfilmapp.model.local.ChatQuota
import kotlinx.coroutines.flow.Flow

/**
 * Repository for the Gemini cinema chatbot.
 *
 * Phase 2 adds local persistence: [observeMessages] exposes the stored history per user,
 * and [sendMessage] now persists both the user prompt and the assistant reply into Room
 * as a single write per message (not per streaming token).
 */
interface ChatRepository {

    /**
     * Observes the chat history for [userId] ordered chronologically (oldest first).
     */
    fun observeMessages(userId: String): Flow<List<ChatMessage>>

    /**
     * Sends [prompt] to Gemini on behalf of [userId] with the last [historyWindow] messages
     * as context, streams the response back as [ChatStreamEvent]s, and persists both
     * the user prompt (immediately) and the assistant response (on completion) to Room.
     */
    fun sendMessage(userId: String, prompt: String, historyWindow: List<ChatMessage>): Flow<ChatStreamEvent>

    /**
     * Removes the entire chat history for [userId]. Used for "clear conversation" actions.
     */
    suspend fun clearHistory(userId: String)

    /**
     * Observes the current month's quota state for [userId] from Firestore. Emits `null`
     * when the user has not made any calls yet (doc doesn't exist).
     */
    fun observeQuota(userId: String): Flow<ChatQuota?>
}
