package com.apptolast.familyfilmapp.ui.screens.chat

import com.apptolast.familyfilmapp.model.local.ChatMessage
import com.apptolast.familyfilmapp.model.local.ChatQuota

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val streamingMessage: ChatMessage? = null,
    val isStreaming: Boolean = false,
    val error: ChatError? = null,
    val quota: ChatQuota? = null,
    val isChatPremium: Boolean = false,
    val showPaywall: Boolean = false,
    val isPurchasing: Boolean = false,
) {
    val allMessages: List<ChatMessage>
        get() = if (streamingMessage != null) messages + streamingMessage else messages

    val isEmpty: Boolean get() = messages.isEmpty() && streamingMessage == null

    val canSend: Boolean
        get() = !isStreaming && quota?.isExceeded != true
}

enum class ChatError { GENERIC, NETWORK, QUOTA_EXCEEDED, PAYWALL_PURCHASE_FAILED }
