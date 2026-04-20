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

    /**
     * Premium real percibido por el cliente. Combina la entitlement de RevenueCat
     * ([isChatPremium]) con el mirror en Firestore (`quota.isPremium`). Nos cubre
     * la ventana entre la compra y la sincronización del webhook con el doc de quota.
     */
    val effectivePremium: Boolean
        get() = isChatPremium || quota?.isPremium == true

    val effectiveLimit: Int
        get() = when {
            quota == null -> if (effectivePremium) PREMIUM_LIMIT else FREE_LIMIT
            effectivePremium -> maxOf(quota.limit, PREMIUM_LIMIT)
            else -> quota.limit
        }

    val effectiveRemaining: Int
        get() = quota?.let { (effectiveLimit - it.count).coerceAtLeast(0) } ?: effectiveLimit

    val effectiveIsExceeded: Boolean
        get() = quota != null && quota.count >= effectiveLimit

    val canSend: Boolean
        get() = !isStreaming && !effectiveIsExceeded

    companion object {
        const val FREE_LIMIT = 5
        const val PREMIUM_LIMIT = 50
    }
}

enum class ChatError { GENERIC, NETWORK, QUOTA_EXCEEDED, PAYWALL_PURCHASE_FAILED }
