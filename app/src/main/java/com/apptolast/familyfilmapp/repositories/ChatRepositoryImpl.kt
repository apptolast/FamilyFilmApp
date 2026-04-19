package com.apptolast.familyfilmapp.repositories

import com.apptolast.familyfilmapp.ai.ChatStreamEvent
import com.apptolast.familyfilmapp.ai.GeminiChatService
import com.apptolast.familyfilmapp.model.local.ChatMessage
import com.apptolast.familyfilmapp.model.room.toDomain
import com.apptolast.familyfilmapp.model.room.toTable
import com.apptolast.familyfilmapp.room.chat.ChatMessageDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val geminiChatService: GeminiChatService,
    private val chatMessageDao: ChatMessageDao,
) : ChatRepository {

    override fun observeMessages(userId: String): Flow<List<ChatMessage>> =
        chatMessageDao.observeByUserId(userId).map { list -> list.map { it.toDomain() } }

    override fun sendMessage(userId: String, prompt: String, historyWindow: List<ChatMessage>): Flow<ChatStreamEvent> {
        val userMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            role = ChatMessage.Role.USER,
            content = prompt,
            timestamp = System.currentTimeMillis(),
        )

        val fullText = StringBuilder()

        return geminiChatService.sendMessage(historyWindow, prompt)
            .onStart {
                // Persist the user message before streaming starts so it survives app kill.
                chatMessageDao.insert(userMessage.toTable(userId))
            }
            .onEach { event ->
                if (event is ChatStreamEvent.Delta) fullText.append(event.token)
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
}
