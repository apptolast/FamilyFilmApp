package com.apptolast.familyfilmapp.ai

import com.apptolast.familyfilmapp.model.local.ChatMessage
import com.apptolast.familyfilmapp.network.TmdbLocaleManager
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.functions.FirebaseFunctionsException
import dev.gitlive.firebase.functions.FunctionsExceptionCode
import dev.gitlive.firebase.functions.functions
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable

// Streaming is cosmetic: the callable returns full text and we split into word-sized deltas.
// CLOUD_FUNCTION_REGION must match the region declared in functions/src/chatComplete.ts.
class GeminiChatService(private val tmdbLocaleManager: TmdbLocaleManager) {

    private val functions get() = Firebase.functions(region = CLOUD_FUNCTION_REGION)

    fun sendMessage(history: List<ChatMessage>, prompt: String, isPremium: Boolean): Flow<ChatStreamEvent> = flow {
        emit(ChatStreamEvent.Started)

        val payload = ChatCompleteRequest(
            prompt = prompt,
            languageTag = tmdbLocaleManager.languageTag.value,
            includeAdult = false,
            isPremium = isPremium,
            history = history.map { msg ->
                ChatHistoryMessage(
                    role = when (msg.role) {
                        ChatMessage.Role.USER -> "user"
                        ChatMessage.Role.ASSISTANT -> "assistant"
                    },
                    content = msg.content,
                )
            },
        )

        val response = runCatching {
            functions
                .httpsCallable(FUNCTION_NAME)
                .invoke(ChatCompleteRequest.serializer(), payload)
                .data(ChatCompleteResponse.serializer())
        }.getOrElse { error ->
            emit(mapErrorToEvent(error))
            return@flow
        }

        val text = response.text
        if (text.isNullOrEmpty()) {
            emit(ChatStreamEvent.Failed(IllegalStateException("chatComplete returned no text")))
            return@flow
        }

        for (chunk in splitIntoChunks(text)) {
            emit(ChatStreamEvent.Delta(chunk))
            delay(STREAM_DELAY_MS)
        }
        emit(ChatStreamEvent.Completed(text))
    }

    private fun mapErrorToEvent(error: Throwable): ChatStreamEvent {
        if (error is FirebaseFunctionsException && error.code == FunctionsExceptionCode.RESOURCE_EXHAUSTED) {
            return ChatStreamEvent.QuotaExceeded
        }
        return ChatStreamEvent.Failed(error)
    }

    private fun splitIntoChunks(text: String): List<String> {
        val result = mutableListOf<String>()
        val buffer = StringBuilder()
        for (ch in text) {
            buffer.append(ch)
            if (ch == ' ' || ch == '\n') {
                result.add(buffer.toString())
                buffer.setLength(0)
            }
        }
        if (buffer.isNotEmpty()) result.add(buffer.toString())
        return result
    }

    companion object {
        private const val FUNCTION_NAME = "chatComplete"
        private const val CLOUD_FUNCTION_REGION = "europe-west2"
        private const val STREAM_DELAY_MS = 15L
        const val HISTORY_WINDOW = 20
    }
}

@Serializable
private data class ChatCompleteRequest(
    val prompt: String,
    val languageTag: String,
    val includeAdult: Boolean,
    val isPremium: Boolean,
    val history: List<ChatHistoryMessage>,
)

@Serializable
private data class ChatHistoryMessage(val role: String, val content: String)

@Serializable
private data class ChatCompleteResponse(val text: String? = null)
