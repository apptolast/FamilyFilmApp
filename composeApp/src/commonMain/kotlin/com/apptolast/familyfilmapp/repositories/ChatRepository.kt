@file:OptIn(ExperimentalUuidApi::class)

package com.apptolast.familyfilmapp.repositories

import com.apptolast.familyfilmapp.ai.ChatStreamEvent
import com.apptolast.familyfilmapp.ai.GeminiChatService
import com.apptolast.familyfilmapp.firebase.CrashReporter
import com.apptolast.familyfilmapp.model.local.ChatMessage
import com.apptolast.familyfilmapp.model.local.ChatQuota
import com.apptolast.familyfilmapp.model.room.toDomain
import com.apptolast.familyfilmapp.model.room.toTable
import com.apptolast.familyfilmapp.room.chat.ChatMessageDao
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.DocumentSnapshot
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface ChatRepository {
    fun observeMessages(userId: String): Flow<List<ChatMessage>>

    // Persists the user prompt immediately and the assistant response on completion.
    fun sendMessage(
        userId: String,
        prompt: String,
        historyWindow: List<ChatMessage>,
        isPremium: Boolean,
    ): Flow<ChatStreamEvent>

    suspend fun clearHistory(userId: String)

    // Emits null when the user hasn't made any calls yet (doc doesn't exist).
    fun observeQuota(userId: String): Flow<ChatQuota?>
}

@OptIn(ExperimentalTime::class)
class ChatRepositoryImpl(
    private val geminiChatService: GeminiChatService,
    private val chatMessageDao: ChatMessageDao,
    private val crashReporter: CrashReporter,
) : ChatRepository {

    private val firestore: FirebaseFirestore get() = Firebase.firestore

    override fun observeMessages(userId: String): Flow<List<ChatMessage>> =
        chatMessageDao.observeByUserId(userId).map { list -> list.map { it.toDomain() } }

    override fun sendMessage(
        userId: String,
        prompt: String,
        historyWindow: List<ChatMessage>,
        isPremium: Boolean,
    ): Flow<ChatStreamEvent> {
        val now = Clock.System.now().toEpochMilliseconds()
        val userMessage = ChatMessage(
            id = Uuid.random().toString(),
            role = ChatMessage.Role.USER,
            content = prompt,
            timestamp = now,
        )
        return geminiChatService.sendMessage(historyWindow, prompt, isPremium)
            .onStart {
                chatMessageDao.insert(userMessage.toTable(userId))
            }
            .onEach { event ->
                if (event is ChatStreamEvent.Completed) {
                    val assistantMessage = ChatMessage(
                        id = Uuid.random().toString(),
                        role = ChatMessage.Role.ASSISTANT,
                        content = event.fullText,
                        timestamp = Clock.System.now().toEpochMilliseconds(),
                    )
                    chatMessageDao.insert(assistantMessage.toTable(userId))
                }
            }
    }

    override suspend fun clearHistory(userId: String) {
        chatMessageDao.deleteByUserId(userId)
    }

    override fun observeQuota(userId: String): Flow<ChatQuota?> = firestore
        .collection("users")
        .document(userId)
        .collection("chat_usage")
        .document(currentYearMonth())
        .snapshots
        .map { snap ->
            if (!snap.exists) return@map null
            try {
                ChatQuota(
                    count = snap.optional<Long>("count")?.toInt() ?: 0,
                    limit = snap.optional<Long>("limit")?.toInt() ?: DEFAULT_FREE_LIMIT,
                    isPremium = snap.optional<Boolean>("isPremium") == true,
                )
            } catch (e: Throwable) {
                crashReporter.recordException(e)
                null
            }
        }

    private fun currentYearMonth(): String {
        // Extract from ISO 8601 toString; avoids version-specific Month.number differences.
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        return today.toString().substring(0, 7)
    }

    private companion object {
        const val DEFAULT_FREE_LIMIT = 5
    }
}

private inline fun <reified T> DocumentSnapshot.optional(field: String): T? =
    if (contains(field)) get<T>(field) else null
