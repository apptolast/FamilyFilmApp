package com.apptolast.familyfilmapp.ai

import android.content.Context
import com.apptolast.familyfilmapp.R
import com.apptolast.familyfilmapp.model.local.ChatMessage
import com.apptolast.familyfilmapp.network.TmdbLocaleManager
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Content
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.content
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Stateless wrapper around the Firebase AI Logic Gemini SDK.
 *
 * Builds a fresh [com.google.firebase.ai.GenerativeModel] on every call using the current
 * [TmdbLocaleManager] state (language + adult content flag) so the system instruction is always
 * in sync with the user's preferences.
 *
 * Phase 1: called directly from the Android client. Phase 3 migrates this to a Cloud Function
 * callable so quota enforcement is server-side and non-bypassable.
 */
@Singleton
class GeminiChatService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val tmdbLocaleManager: TmdbLocaleManager,
) {

    /**
     * Streams a response from Gemini as [ChatStreamEvent]s.
     *
     * @param history prior [ChatMessage]s in chronological order (oldest first). Capped to
     *   [HISTORY_WINDOW] messages by the caller.
     * @param prompt the new user prompt to send.
     */
    fun sendMessage(history: List<ChatMessage>, prompt: String): Flow<ChatStreamEvent> {
        val languageTag = tmdbLocaleManager.languageTag.value
        val includeAdult = tmdbLocaleManager.includeAdult.value

        val model = Firebase.ai(backend = GenerativeBackend.vertexAI())
            .generativeModel(
                modelName = MODEL_NAME,
                systemInstruction = buildSystemInstruction(languageTag, includeAdult),
            )

        val contentHistory = history.map { message ->
            content(role = message.role.toGeminiRole()) { text(message.content) }
        }

        val chat = model.startChat(contentHistory)

        return flow {
            val responseFlow = chat.sendMessageStream(prompt)
            val fullText = StringBuilder()
            responseFlow
                .map { ChatStreamEvent.Delta(it.text.orEmpty()) }
                .collect { delta ->
                    if (delta.token.isNotEmpty()) {
                        fullText.append(delta.token)
                        emit(delta as ChatStreamEvent)
                    }
                }
            emit(ChatStreamEvent.Completed(fullText.toString()))
        }
            .onStart { emit(ChatStreamEvent.Started) }
            .catch { error ->
                Timber.e(error, "Gemini sendMessage failed")
                emit(ChatStreamEvent.Failed(error))
            }
            .onCompletion { cause ->
                if (cause != null) Timber.w(cause, "Gemini stream finished with cause")
            }
    }

    private fun buildSystemInstruction(languageTag: String, includeAdult: Boolean): Content {
        val adultClause = if (includeAdult) {
            ""
        } else {
            context.getString(R.string.chat_system_prompt_no_adult)
        }
        val prompt = context.getString(R.string.chat_system_prompt, languageTag, adultClause)
        return content { text(prompt) }
    }

    private fun ChatMessage.Role.toGeminiRole(): String = when (this) {
        ChatMessage.Role.USER -> "user"
        ChatMessage.Role.ASSISTANT -> "model"
    }

    companion object {
        private const val MODEL_NAME = "gemini-2.5-flash"
        const val HISTORY_WINDOW = 20
    }
}
