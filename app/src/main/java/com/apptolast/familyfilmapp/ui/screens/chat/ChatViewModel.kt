package com.apptolast.familyfilmapp.ui.screens.chat

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptolast.familyfilmapp.ai.ChatStreamEvent
import com.apptolast.familyfilmapp.ai.GeminiChatService
import com.apptolast.familyfilmapp.model.local.ChatMessage
import com.apptolast.familyfilmapp.model.local.ChatQuota
import com.apptolast.familyfilmapp.purchases.PurchaseFailure
import com.apptolast.familyfilmapp.purchases.PurchaseManager
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
 * Phase 4: integrates the Chat Premium subscription paywall. When the server returns
 * `resource-exhausted` (QuotaExceeded) AND the user is NOT premium, we surface a paywall
 * dialog that drives a RevenueCat purchase flow. The resulting entitlement update flows
 * from the RevenueCat SDK → `PurchaseManager.hasChatPremium` → Firestore mirror (via
 * webhook) so the next server call applies the 50-msg limit.
 */
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val dispatcherProvider: DispatcherProvider,
    private val purchaseManager: PurchaseManager,
    auth: FirebaseAuth,
) : ViewModel() {

    private val userId: String? = auth.uid

    private val streamingState = MutableStateFlow(StreamingState())
    private val paywallState = MutableStateFlow(PaywallState())

    private val persistedMessages = if (userId != null) {
        chatRepository.observeMessages(userId)
    } else {
        flowOf(emptyList())
    }

    private val quotaFlow = if (userId != null) {
        chatRepository.observeQuota(userId)
    } else {
        flowOf<ChatQuota?>(null)
    }

    val uiState: StateFlow<ChatUiState> = combine(
        persistedMessages,
        streamingState,
        quotaFlow,
        purchaseManager.hasChatPremium,
        paywallState,
    ) { messages, streaming, quota, isPremium, paywall ->
        ChatUiState(
            messages = messages,
            streamingMessage = streaming.streamingMessage,
            isStreaming = streaming.isStreaming,
            error = streaming.error ?: paywall.error,
            quota = quota,
            isChatPremium = isPremium,
            showPaywall = paywall.shown,
            isPurchasing = paywall.isPurchasing,
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
                        // Surface the paywall only when the user is NOT already premium.
                        // Server may return QuotaExceeded to premium users too (limit of 50).
                        if (!purchaseManager.hasChatPremium.value) {
                            paywallState.update { it.copy(shown = true, error = null) }
                        }
                    }
                }
            }
        }
    }

    fun requestChatPremiumPurchase(activity: Activity) {
        if (paywallState.value.isPurchasing) return
        viewModelScope.launch(dispatcherProvider.io()) {
            paywallState.update { it.copy(isPurchasing = true, error = null) }
            purchaseManager.purchaseChatPremium(activity)
                .onSuccess {
                    Timber.d("Chat premium purchased successfully")
                    paywallState.update { PaywallState() }
                }
                .onFailure { error ->
                    Timber.w(error, "Chat premium purchase failed")
                    paywallState.update {
                        it.copy(
                            isPurchasing = false,
                            error = when (error) {
                                is PurchaseFailure.Cancelled -> null
                                else -> ChatError.PAYWALL_PURCHASE_FAILED
                            },
                        )
                    }
                }
        }
    }

    fun dismissPaywall() {
        paywallState.update { PaywallState() }
    }

    fun showPaywallManually() {
        if (!purchaseManager.hasChatPremium.value) {
            paywallState.update { it.copy(shown = true, error = null) }
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
        paywallState.update { it.copy(error = null) }
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

    private data class PaywallState(
        val shown: Boolean = false,
        val isPurchasing: Boolean = false,
        val error: ChatError? = null,
    )

    companion object {
        private const val STATE_TIMEOUT_MS = 5_000L
    }
}
