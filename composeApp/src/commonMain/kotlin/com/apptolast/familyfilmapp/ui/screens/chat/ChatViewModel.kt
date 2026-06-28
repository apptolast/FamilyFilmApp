@file:OptIn(ExperimentalUuidApi::class)

package com.apptolast.familyfilmapp.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptolast.familyfilmapp.ai.ChatStreamEvent
import com.apptolast.familyfilmapp.ai.GeminiChatService
import com.apptolast.familyfilmapp.analytics.AnalyticsEvents
import com.apptolast.familyfilmapp.analytics.AnalyticsTracker
import com.apptolast.familyfilmapp.firebase.CrashReporter
import com.apptolast.familyfilmapp.firebase.CurrentUserIdProvider
import com.apptolast.familyfilmapp.model.local.ChatMessage
import com.apptolast.familyfilmapp.model.local.ChatQuota
import com.apptolast.familyfilmapp.purchases.PurchaseFailure
import com.apptolast.familyfilmapp.purchases.PurchaseManager
import com.apptolast.familyfilmapp.purchases.SubscriptionPricing
import com.apptolast.familyfilmapp.repositories.ChatRepository
import com.apptolast.familyfilmapp.utils.DispatcherProvider
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel(
    private val chatRepository: ChatRepository,
    private val dispatcherProvider: DispatcherProvider,
    private val purchaseManager: PurchaseManager,
    private val analyticsTracker: AnalyticsTracker,
    private val crashReporter: CrashReporter,
    private val currentUserIdProvider: CurrentUserIdProvider,
) : ViewModel() {

    // Snapshot at construction: Compose tears the screen down on logout via AuthState.
    private val userId: String? = currentUserIdProvider.currentUserId()

    init {
        analyticsTracker.logEvent(
            AnalyticsEvents.CHAT_SESSION_STARTED,
            mapOf(AnalyticsEvents.Param.IS_PREMIUM to purchaseManager.hasChatPremium.value),
        )
    }

    private val streamingState = MutableStateFlow(StreamingState())
    private val paywallState = MutableStateFlow(PaywallState())

    private val persistedMessages = userId?.let { chatRepository.observeMessages(it) }
        ?: flowOf(emptyList())

    private val quotaFlow = userId?.let { chatRepository.observeQuota(it) }
        ?: flowOf<ChatQuota?>(null)

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
            pricing = paywall.pricing,
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

        val streamingId = Uuid.random().toString()
        streamingState.update {
            StreamingState(
                streamingMessage = ChatMessage(
                    id = streamingId,
                    role = ChatMessage.Role.ASSISTANT,
                    content = "",
                    timestamp = Clock.System.now().toEpochMilliseconds(),
                ),
                isStreaming = true,
                error = null,
            )
        }

        val history = uiState.value.messages.takeLast(GeminiChatService.HISTORY_WINDOW)
        val isPremium = purchaseManager.hasChatPremium.value

        analyticsTracker.logEvent(
            AnalyticsEvents.CHAT_MESSAGE_SENT,
            mapOf(
                AnalyticsEvents.Param.PROMPT_LENGTH to trimmed.length.toLong(),
                AnalyticsEvents.Param.HISTORY_SIZE to history.size.toLong(),
                AnalyticsEvents.Param.IS_PREMIUM to isPremium,
            ),
        )

        streamJob?.cancel()
        streamJob = viewModelScope.launch(dispatcherProvider.io()) {
            val buffer = StringBuilder()
            chatRepository.sendMessage(currentUserId, trimmed, history, isPremium).collect { event ->
                when (event) {
                    ChatStreamEvent.Started -> Unit

                    is ChatStreamEvent.Delta -> {
                        buffer.append(event.token)
                        val partial = buffer.toString()
                        streamingState.update { state ->
                            state.copy(streamingMessage = state.streamingMessage?.copy(content = partial))
                        }
                    }

                    is ChatStreamEvent.Completed -> streamingState.update { StreamingState() }

                    is ChatStreamEvent.Failed -> {
                        crashReporter.recordException(event.error)
                        // commonMain has no java.io.IOException — class-name heuristic instead.
                        val isNetwork = event.error::class.simpleName
                            ?.contains("IO", ignoreCase = true) == true ||
                            event.error::class.simpleName
                                ?.contains("Network", ignoreCase = true) == true
                        val errorType = if (isNetwork) ChatError.NETWORK else ChatError.GENERIC
                        analyticsTracker.logEvent(
                            AnalyticsEvents.CHAT_STREAM_FAILED,
                            mapOf(
                                AnalyticsEvents.Param.ERROR_TYPE to if (errorType == ChatError.NETWORK) {
                                    AnalyticsEvents.ErrorType.NETWORK
                                } else {
                                    AnalyticsEvents.ErrorType.GENERIC
                                },
                                AnalyticsEvents.Param.IS_PREMIUM to isPremium,
                            ),
                        )
                        streamingState.update { StreamingState(error = errorType) }
                    }

                    ChatStreamEvent.QuotaExceeded -> {
                        streamingState.update { StreamingState(error = ChatError.QUOTA_EXCEEDED) }
                        analyticsTracker.logEvent(
                            AnalyticsEvents.CHAT_QUOTA_EXHAUSTED,
                            mapOf(AnalyticsEvents.Param.IS_PREMIUM to isPremium),
                        )
                        if (!purchaseManager.hasChatPremium.value) {
                            analyticsTracker.logEvent(
                                AnalyticsEvents.PAYWALL_SHOWN,
                                mapOf(
                                    AnalyticsEvents.Param.ENTRY_POINT to AnalyticsEvents.EntryPoint.QUOTA_EXHAUSTED,
                                    AnalyticsEvents.Param.ENTITLEMENT to AnalyticsEvents.Entitlement.CHAT_PREMIUM,
                                ),
                            )
                            paywallState.update { it.copy(shown = true, error = null) }
                            loadChatPremiumPricing()
                        }
                    }
                }
            }
        }
    }

    fun requestChatPremiumPurchase() {
        if (paywallState.value.isPurchasing) return
        viewModelScope.launch(dispatcherProvider.io()) {
            paywallState.update { it.copy(isPurchasing = true, error = null) }
            purchaseManager.purchaseChatPremium()
                .onSuccess { paywallState.update { PaywallState() } }
                .onFailure { error ->
                    paywallState.update {
                        it.copy(
                            isPurchasing = false,
                            error = when (error) {
                                is PurchaseFailure.Cancelled -> null
                                else -> {
                                    crashReporter.recordException(error)
                                    ChatError.PAYWALL_PURCHASE_FAILED
                                }
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
            analyticsTracker.logEvent(
                AnalyticsEvents.PAYWALL_SHOWN,
                mapOf(
                    AnalyticsEvents.Param.ENTRY_POINT to AnalyticsEvents.EntryPoint.CHAT_MANUAL_UPSELL,
                    AnalyticsEvents.Param.ENTITLEMENT to AnalyticsEvents.Entitlement.CHAT_PREMIUM,
                ),
            )
            paywallState.update { it.copy(shown = true, error = null) }
            loadChatPremiumPricing()
        }
    }

    /** Resolve the localized billed amount so the paywall can display it (Guideline 3.1.2(c)). */
    private fun loadChatPremiumPricing() {
        if (paywallState.value.pricing != null) return
        viewModelScope.launch(dispatcherProvider.io()) {
            val pricing = runCatching { purchaseManager.getChatPremiumPricing() }
                .onFailure { crashReporter.recordException(it) }
                .getOrNull()
            if (pricing != null) {
                paywallState.update { it.copy(pricing = pricing) }
            }
        }
    }

    fun clearHistory() {
        val currentUserId = userId ?: return
        viewModelScope.launch(dispatcherProvider.io()) {
            val previousCount = uiState.value.messages.size
            chatRepository.clearHistory(currentUserId)
            analyticsTracker.logEvent(
                AnalyticsEvents.CHAT_HISTORY_CLEARED,
                mapOf(AnalyticsEvents.Param.MESSAGES_COUNT to previousCount.toLong()),
            )
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
        val pricing: SubscriptionPricing? = null,
    )

    private companion object {
        const val STATE_TIMEOUT_MS = 5_000L
    }
}
