package com.apptolast.familyfilmapp.ui.screens.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.apptolast.familyfilmapp.R
import com.apptolast.familyfilmapp.model.local.ChatMessage
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme

@Composable
fun ChatScreen(modifier: Modifier = Modifier, viewModel: ChatViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ChatContent(
        state = state,
        onSend = viewModel::sendMessage,
        onSuggestionClick = viewModel::sendMessage,
        onErrorDismiss = viewModel::clearError,
        modifier = modifier,
    )
}

@Composable
private fun ChatContent(
    state: ChatUiState,
    onSend: (String) -> Unit,
    onSuggestionClick: (String) -> Unit,
    onErrorDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val errorGeneric = stringResource(R.string.chat_error_generic)
    val errorNetwork = stringResource(R.string.chat_error_network)
    val currentOnErrorDismiss by rememberUpdatedState(onErrorDismiss)

    LaunchedEffect(state.error) {
        val message = when (state.error) {
            ChatError.GENERIC, ChatError.QUOTA_EXCEEDED -> errorGeneric
            ChatError.NETWORK -> errorNetwork
            null -> null
        }
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            currentOnErrorDismiss()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                if (state.isEmpty) {
                    EmptyState(
                        onSuggestionClick = onSuggestionClick,
                        modifier = Modifier.align(Alignment.Center),
                    )
                } else {
                    MessageList(
                        messages = state.allMessages,
                        isStreaming = state.isStreaming,
                    )
                }
            }
            ChatInput(
                enabled = !state.isStreaming,
                onSend = onSend,
            )
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        ) { Snackbar(snackbarData = it) }
    }
}

@Composable
private fun MessageList(messages: List<ChatMessage>, isStreaming: Boolean) {
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size, messages.lastOrNull()?.content?.length) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    LazyColumn(
        state = listState,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            horizontal = 16.dp,
            vertical = 12.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(items = messages, key = { it.id }) { message ->
            MessageBubble(
                message = message,
                isStreamingLast = isStreaming && message == messages.lastOrNull(),
            )
        }
    }
}

@Composable
private fun MessageBubble(message: ChatMessage, isStreamingLast: Boolean) {
    val isUser = message.role == ChatMessage.Role.USER
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp,
            ),
            color = if (isUser) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surfaceContainerHigh
            },
            modifier = Modifier.widthIn(max = 320.dp),
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                val displayText = if (message.content.isEmpty() && isStreamingLast) "…" else message.content
                Text(
                    text = displayText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isUser) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                )
            }
        }
    }
}

@Composable
private fun ChatInput(enabled: Boolean, onSend: (String) -> Unit) {
    var text by rememberSaveable { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    Surface(
        tonalElevation = 2.dp,
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.imePadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text(stringResource(R.string.chat_input_placeholder)) },
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
                enabled = enabled,
                maxLines = 5,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (text.isNotBlank()) {
                            onSend(text)
                            text = ""
                        }
                    },
                ),
                shape = RoundedCornerShape(24.dp),
            )
            Spacer(modifier = Modifier.size(8.dp))
            FilledIconButton(
                onClick = {
                    if (text.isNotBlank()) {
                        onSend(text)
                        text = ""
                    }
                },
                enabled = enabled && text.isNotBlank(),
                colors = IconButtonDefaults.filledIconButtonColors(),
                modifier = Modifier.size(52.dp),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = stringResource(R.string.chat_send_content_description),
                )
            }
        }
    }
}

@Composable
private fun EmptyState(onSuggestionClick: (String) -> Unit, modifier: Modifier = Modifier) {
    val suggestions = listOf(
        stringResource(R.string.chat_suggestion_1),
        stringResource(R.string.chat_suggestion_2),
        stringResource(R.string.chat_suggestion_3),
        stringResource(R.string.chat_suggestion_4),
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.chat_empty_state_title),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.chat_empty_state_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(modifier = Modifier.height(24.dp))

        AnimatedVisibility(visible = true) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                suggestions.forEach { suggestion ->
                    SuggestionChip(
                        text = suggestion,
                        onClick = { onSuggestionClick(suggestion) },
                    )
                }
            }
        }
    }
}

@Composable
private fun SuggestionChip(text: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewChatEmpty() {
    FamilyFilmAppTheme {
        ChatContent(
            state = ChatUiState(),
            onSend = {},
            onSuggestionClick = {},
            onErrorDismiss = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewChatWithMessages() {
    FamilyFilmAppTheme {
        ChatContent(
            state = ChatUiState(
                messages = listOf(
                    ChatMessage(
                        id = "1",
                        role = ChatMessage.Role.USER,
                        content = "Recomiéndame películas de ciencia ficción",
                        timestamp = 0L,
                    ),
                    ChatMessage(
                        id = "2",
                        role = ChatMessage.Role.ASSISTANT,
                        content = "Claro, aquí van 5 imprescindibles:\n\n1. Blade Runner 2049\n2. Arrival",
                        timestamp = 0L,
                    ),
                ),
            ),
            onSend = {},
            onSuggestionClick = {},
            onErrorDismiss = {},
        )
    }
}
