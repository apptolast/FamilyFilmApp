package com.apptolast.familyfilmapp.ui.screens.chat

import com.apptolast.familyfilmapp.model.local.ChatMessage

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val streamingMessage: ChatMessage? = null,
    val isStreaming: Boolean = false,
    val error: ChatError? = null,
) {
    val allMessages: List<ChatMessage>
        get() = if (streamingMessage != null) messages + streamingMessage else messages

    val isEmpty: Boolean get() = messages.isEmpty() && streamingMessage == null
}

enum class ChatError { GENERIC, NETWORK, QUOTA_EXCEEDED }
