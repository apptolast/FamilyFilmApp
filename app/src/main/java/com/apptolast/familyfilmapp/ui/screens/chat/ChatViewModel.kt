package com.apptolast.familyfilmapp.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptolast.familyfilmapp.ai.ChatStreamEvent
import com.apptolast.familyfilmapp.ai.GeminiChatService
import com.apptolast.familyfilmapp.model.local.ChatMessage
import com.apptolast.familyfilmapp.repositories.ChatRepository
import com.apptolast.familyfilmapp.utils.DispatcherProvider
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

/**
 * ViewModel for the Gemini cinema chat.
 *
 * Phase 2: the persisted history is observed from Room via [ChatRepository.observeMessages].
 * Streaming state (partial assistant reply) lives only in memory; when the stream completes
 * the repository writes the final assistant message to Room and it reappears through the
 * observed flow.
 *
 * Phase 3 will enforce quota via server responses (not implemented yet).
 */
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val dispatcherProvider: DispatcherProvider,
    auth: FirebaseAuth,
) : ViewModel() {

    private val userId: String? = auth.uid

    private val streamingState = MutableStateFlow(StreamingState())

    private val persistedMessages = if (userId != null) {
        chatRepository.observeMessages(userId)
    } else {
        flowOf(emptyList())
    }

    val uiState: StateFlow<ChatUiState> = combine(
        persistedMessages,
        streamingState,
    ) { messages, streaming ->
        ChatUiState(
            messages = messages,
            streamingMessage = streaming.streamingMessage,
            isStreaming = streaming.isStreaming,
            error = streaming.error,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STATE_TIMEOUT_MS),
        initialValue = ChatUiState(),
    )

    private var streamJob: Job? = null

    fun sendMessage(prompt: String) {
        val currentUserId = userId ?: return
        val trimmed = prompt.trim()
        if (trimmed.isEmpty() || streamingState.value.isStreaming) return

        val streamingId = java.util.UUID.randomUUID().toString()
        streamingState.update {
            StreamingState(
                streamingMessage = ChatMessage(
                    id = streamingId,
                    role = ChatMessage.Role.ASSISTANT,
                    content = "",
                    timestamp = System.currentTimeMillis(),
                ),
                isStreaming = true,
                error = null,
            )
        }

        val history = uiState.value.messages.takeLast(GeminiChatService.HISTORY_WINDOW)

        streamJob?.cancel()
        streamJob = viewModelScope.launch(dispatcherProvider.io()) {
            val buffer = StringBuilder()
            chatRepository.sendMessage(currentUserId, trimmed, history).collect { event ->
                when (event) {
                    ChatStreamEvent.Started -> Unit

                    is ChatStreamEvent.Delta -> {
                        buffer.append(event.token)
                        val partial = buffer.toString()
                        streamingState.update { state ->
                            state.copy(
                                streamingMessage = state.streamingMessage?.copy(content = partial),
                            )
                        }
                    }

                    is ChatStreamEvent.Completed -> {
                        // Repository already persisted the assistant message; drop the streaming placeholder.
                        streamingState.update { StreamingState() }
                    }

                    is ChatStreamEvent.Failed -> {
                        Timber.e(event.error, "Chat stream failed")
                        val errorType = if (event.error is IOException) {
                            ChatError.NETWORK
                        } else {
                            ChatError.GENERIC
                        }
                        streamingState.update { StreamingState(error = errorType) }
                    }

                    ChatStreamEvent.QuotaExceeded -> {
                        streamingState.update { StreamingState(error = ChatError.QUOTA_EXCEEDED) }
                    }
                }
            }
        }
    }

    fun clearHistory() {
        val currentUserId = userId ?: return
        viewModelScope.launch(dispatcherProvider.io()) {
            chatRepository.clearHistory(currentUserId)
        }
    }

    fun clearError() {
        streamingState.update { it.copy(error = null) }
    }

    override fun onCleared() {
        super.onCleared()
        streamJob?.cancel()
    }

    private data class StreamingState(
        val streamingMessage: ChatMessage? = null,
        val isStreaming: Boolean = false,
        val error: ChatError? = null,
    )

    companion object {
        private const val STATE_TIMEOUT_MS = 5_000L
    }
}
