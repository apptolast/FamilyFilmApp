package com.apptolast.familyfilmapp.ui.screens.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ChatScreen(viewModel: ChatViewModel = koinViewModel()) {
    val state by viewModel.uiState.collectAsState()

    ChatContent(
        state = state,
        onSend = viewModel::sendMessage,
        onSuggestionClick = viewModel::sendMessage,
        onErrorDismiss = viewModel::clearError,
        onUpgradeClick = viewModel::showPaywallManually,
        onPaywallConfirm = viewModel::requestChatPremiumPurchase,
        onPaywallDismiss = viewModel::dismissPaywall,
    )
}
