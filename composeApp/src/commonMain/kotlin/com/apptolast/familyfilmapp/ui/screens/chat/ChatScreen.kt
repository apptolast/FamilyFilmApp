package com.apptolast.familyfilmapp.ui.screens.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.apptolast.familyfilmapp.model.local.ChatMessage
import org.koin.compose.viewmodel.koinViewModel

/**
 * Stub Chat screen. Renders messages + input; the rich legacy version
 * (typing indicators, paywall modal, error banners, premium upsell)
 * comes in a post-migration polish pass.
 */
@Composable
fun ChatScreen(viewModel: ChatViewModel = koinViewModel()) {
    val state by viewModel.uiState.collectAsState()
    var input by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
            items(state.allMessages) { msg ->
                Text(text = msg.formatted(), style = MaterialTheme.typography.bodyMedium)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Ask Gemini…") },
                enabled = state.canSend,
            )
            Button(
                onClick = {
                    if (input.isNotBlank()) {
                        viewModel.sendMessage(input)
                        input = ""
                    }
                },
                enabled = state.canSend,
            ) { Text("Send") }
        }
        state.error?.let { Text(text = "Error: $it", color = MaterialTheme.colorScheme.error) }
        state.quota?.let { Text(text = "Quota: ${state.effectiveRemaining}/${state.effectiveLimit}") }
    }
}

private fun ChatMessage.formatted(): String = when (role) {
    ChatMessage.Role.USER -> "You: $content"
    ChatMessage.Role.ASSISTANT -> "Gemini: $content"
}
