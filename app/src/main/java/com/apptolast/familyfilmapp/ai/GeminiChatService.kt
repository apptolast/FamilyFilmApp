package com.apptolast.familyfilmapp.ai

import com.apptolast.familyfilmapp.model.local.ChatMessage
import com.apptolast.familyfilmapp.network.TmdbLocaleManager
import com.google.firebase.Firebase
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.functions.functions
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Calls the Cloud Function `chatComplete` which enforces quota server-side
 * and proxies the Gemini call. Returns a [Flow] of [ChatStreamEvent] that mimics
 * streaming by emitting word-by-word deltas from the full response — the callable
 * API returns everything at once, so the streaming is cosmetic but preserves the
 * ViewModel contract from phases 1–2.
 *
 * The [CLOUD_FUNCTION_REGION] must match the region declared in functions/src/chatComplete.ts.
 */
@Singleton
class GeminiChatService @Inject constructor(private val tmdbLocaleManager: TmdbLocaleManager) {

    private val functions = Firebase.functions(CLOUD_FUNCTION_REGION)

    fun sendMessage(history: List<ChatMessage>, prompt: String): Flow<ChatStreamEvent> = flow {
        emit(ChatStreamEvent.Started)

        val payload = mapOf(
            "prompt" to prompt,
            "languageTag" to tmdbLocaleManager.languageTag.value,
            "includeAdult" to tmdbLocaleManager.includeAdult.value,
            "history" to history.map {
                mapOf(
                    "role" to when (it.role) {
                        ChatMessage.Role.USER -> "user"
                        ChatMessage.Role.ASSISTANT -> "assistant"
                    },
                    "content" to it.content,
                )
            },
        )

        val result = runCatching {
            functions.getHttpsCallable(FUNCTION_NAME)
                .call(payload)
                .await()
        }.getOrElse { error ->
            val event = mapErrorToEvent(error)
            emit(event)
            return@flow
        }

        @Suppress("UNCHECKED_CAST")
        val data = result.data as? Map<String, Any?>
        if (data == null) {
            emit(ChatStreamEvent.Failed(IllegalStateException("Empty response from chatComplete")))
            return@flow
        }

        val text = data["text"] as? String
        if (text.isNullOrEmpty()) {
            emit(ChatStreamEvent.Failed(IllegalStateException("chatComplete returned no text")))
            return@flow
        }

        // Cosmetic streaming: chunk the response into word-sized deltas with a tiny delay so
        // the UI still reveals the text progressively. If this ever feels sluggish, lower the
        // delay or emit character-by-character.
        val chunks = splitIntoChunks(text)
        for (chunk in chunks) {
            emit(ChatStreamEvent.Delta(chunk))
            delay(STREAM_DELAY_MS)
        }

        emit(ChatStreamEvent.Completed(text))
    }

    private fun mapErrorToEvent(error: Throwable): ChatStreamEvent {
        if (error is FirebaseFunctionsException) {
            Timber.w(error, "chatComplete failed code=${error.code} details=${error.details}")
            return when (error.code) {
                FirebaseFunctionsException.Code.RESOURCE_EXHAUSTED -> ChatStreamEvent.QuotaExceeded
                else -> ChatStreamEvent.Failed(error)
            }
        }
        Timber.e(error, "chatComplete failed with non-Firebase error")
        return ChatStreamEvent.Failed(error)
    }

    private fun splitIntoChunks(text: String): List<String> {
        // Split on whitespace but keep the trailing space so the UI renders the gaps.
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
