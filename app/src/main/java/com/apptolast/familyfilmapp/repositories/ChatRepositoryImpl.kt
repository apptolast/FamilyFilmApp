package com.apptolast.familyfilmapp.repositories

import com.apptolast.familyfilmapp.ai.ChatStreamEvent
import com.apptolast.familyfilmapp.ai.GeminiChatService
import com.apptolast.familyfilmapp.model.local.ChatMessage
import com.apptolast.familyfilmapp.model.local.ChatQuota
import com.apptolast.familyfilmapp.model.room.toDomain
import com.apptolast.familyfilmapp.model.room.toTable
import com.apptolast.familyfilmapp.room.chat.ChatMessageDao
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import timber.log.Timber
import java.util.Calendar
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val geminiChatService: GeminiChatService,
    private val chatMessageDao: ChatMessageDao,
    private val firestore: FirebaseFirestore,
) : ChatRepository {

    override fun observeMessages(userId: String): Flow<List<ChatMessage>> =
        chatMessageDao.observeByUserId(userId).map { list -> list.map { it.toDomain() } }

    override fun sendMessage(
        userId: String,
        prompt: String,
        historyWindow: List<ChatMessage>,
        isPremium: Boolean,
    ): Flow<ChatStreamEvent> {
        val userMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            role = ChatMessage.Role.USER,
            content = prompt,
            timestamp = System.currentTimeMillis(),
        )

        return geminiChatService.sendMessage(historyWindow, prompt, isPremium)
            .onStart {
                chatMessageDao.insert(userMessage.toTable(userId))
            }
            .onEach { event ->
                if (event is ChatStreamEvent.Completed) {
                    val assistantMessage = ChatMessage(
                        id = UUID.randomUUID().toString(),
                        role = ChatMessage.Role.ASSISTANT,
                        content = event.fullText,
                        timestamp = System.currentTimeMillis(),
                    )
                    chatMessageDao.insert(assistantMessage.toTable(userId))
                }
            }
            .onCompletion { cause ->
                if (cause != null) Timber.w(cause, "sendMessage flow ended with cause")
            }
    }

    override suspend fun clearHistory(userId: String) {
        chatMessageDao.deleteByUserId(userId)
    }

    override fun observeQuota(userId: String): Flow<ChatQuota?> = callbackFlow {
        val yearMonth = currentYearMonth()
        val listener = firestore
            .collection("users")
            .document(userId)
            .collection("chat_usage")
            .document(yearMonth)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Timber.w(error, "observeQuota snapshot error")
                    return@addSnapshotListener
                }
                val quota = snapshot?.takeIf { it.exists() }?.let {
                    ChatQuota(
                        count = (it.getLong("count") ?: 0L).toInt(),
                        limit = (it.getLong("limit") ?: DEFAULT_FREE_LIMIT).toInt(),
                        isPremium = it.getBoolean("isPremium") ?: false,
                    )
                }
                trySend(quota)
            }
        awaitClose { listener.remove() }
    }

    private fun currentYearMonth(): String {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        return String.format(Locale.US, "%04d-%02d", year, month)
    }

    companion object {
        private const val DEFAULT_FREE_LIMIT = 5L
    }
}
