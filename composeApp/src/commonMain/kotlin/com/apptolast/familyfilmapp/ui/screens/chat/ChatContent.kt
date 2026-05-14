package com.apptolast.familyfilmapp.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.apptolast.familyfilmapp.model.local.ChatMessage
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme

@Composable
fun ChatContent(
    state: ChatUiState,
    input: String,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (state.isEmpty) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                Text(
                    text = "Ask Gemini for film and TV recommendations",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(state.allMessages, key = { it.id }) { message ->
                    ChatBubble(message = message)
                }
            }
        }

        state.error?.let {
            Text(text = "Error: $it", color = MaterialTheme.colorScheme.error)
        }
        state.quota?.let {
            Text(text = "Quota: ${state.effectiveRemaining}/${state.effectiveLimit}")
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = input,
                onValueChange = onInputChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Ask Gemini…") },
                enabled = state.canSend,
            )
            Button(onClick = onSend, enabled = state.canSend && input.isNotBlank()) {
                Text("Send")
            }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    val isUser = message.role == ChatMessage.Role.USER
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (isUser) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
                )
                .padding(12.dp),
        ) {
            Text(
                text = message.content,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isUser) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
            )
        }
    }
}

@Composable
@Preview
private fun PreviewChatContentEmpty() {
    FamilyFilmAppTheme {
        ChatContent(
            state = ChatUiState(),
            input = "",
            onInputChange = {},
            onSend = {},
        )
    }
}

@Composable
@Preview
private fun PreviewChatContentWithMessages() {
    FamilyFilmAppTheme {
        ChatContent(
            state = ChatUiState(
                messages = listOf(
                    ChatMessage(id = "1", role = ChatMessage.Role.USER, content = "Suggest a sci-fi film", timestamp = 0),
                    ChatMessage(
                        id = "2",
                        role = ChatMessage.Role.ASSISTANT,
                        content = "How about Arrival, Blade Runner 2049, or Interstellar?",
                        timestamp = 0,
                    ),
                ),
            ),
            input = "",
            onInputChange = {},
            onSend = {},
        )
    }
}
