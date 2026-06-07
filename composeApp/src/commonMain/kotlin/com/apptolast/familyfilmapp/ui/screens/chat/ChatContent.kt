package com.apptolast.familyfilmapp.ui.screens.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.apptolast.familyfilmapp.model.local.ChatMessage
import com.apptolast.familyfilmapp.model.local.ChatQuota
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import familyfilmkmp.composeapp.generated.resources.Res
import familyfilmkmp.composeapp.generated.resources.chat_empty_state_subtitle
import familyfilmkmp.composeapp.generated.resources.chat_empty_state_title
import familyfilmkmp.composeapp.generated.resources.chat_error_generic
import familyfilmkmp.composeapp.generated.resources.chat_error_network
import familyfilmkmp.composeapp.generated.resources.chat_error_quota_exceeded
import familyfilmkmp.composeapp.generated.resources.chat_input_placeholder
import familyfilmkmp.composeapp.generated.resources.chat_paywall_bullet_cancel
import familyfilmkmp.composeapp.generated.resources.chat_paywall_bullet_priority
import familyfilmkmp.composeapp.generated.resources.chat_paywall_bullet_questions
import familyfilmkmp.composeapp.generated.resources.chat_paywall_cta
import familyfilmkmp.composeapp.generated.resources.chat_paywall_dismiss
import familyfilmkmp.composeapp.generated.resources.chat_paywall_error
import familyfilmkmp.composeapp.generated.resources.chat_paywall_subtitle
import familyfilmkmp.composeapp.generated.resources.chat_paywall_title
import familyfilmkmp.composeapp.generated.resources.chat_quota_banner
import familyfilmkmp.composeapp.generated.resources.chat_quota_banner_premium
import familyfilmkmp.composeapp.generated.resources.chat_quota_exceeded_subtitle
import familyfilmkmp.composeapp.generated.resources.chat_quota_remaining_subtitle
import familyfilmkmp.composeapp.generated.resources.chat_quota_upgrade_cta
import familyfilmkmp.composeapp.generated.resources.chat_send_content_description
import familyfilmkmp.composeapp.generated.resources.chat_suggestion_1
import familyfilmkmp.composeapp.generated.resources.chat_suggestion_2
import familyfilmkmp.composeapp.generated.resources.chat_suggestion_3
import familyfilmkmp.composeapp.generated.resources.chat_suggestion_4
import org.jetbrains.compose.resources.stringResource

@Composable
fun ChatContent(
    state: ChatUiState,
    onSend: (String) -> Unit,
    onSuggestionClick: (String) -> Unit,
    onErrorDismiss: () -> Unit,
    onUpgradeClick: () -> Unit,
    onPaywallConfirm: () -> Unit,
    onPaywallDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val errorGeneric = stringResource(Res.string.chat_error_generic)
    val errorNetwork = stringResource(Res.string.chat_error_network)
    val errorQuota = stringResource(Res.string.chat_error_quota_exceeded)
    val errorPaywall = stringResource(Res.string.chat_paywall_error)
    val currentOnErrorDismiss by rememberUpdatedState(onErrorDismiss)

    LaunchedEffect(state.error) {
        val message = when (state.error) {
            ChatError.QUOTA_EXCEEDED -> errorQuota
            ChatError.GENERIC -> errorGeneric
            ChatError.NETWORK -> errorNetwork
            ChatError.PAYWALL_PURCHASE_FAILED -> errorPaywall
            null -> null
        }
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            currentOnErrorDismiss()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            state.quota?.let {
                QuotaBanner(
                    quota = it,
                    effectivePremium = state.effectivePremium,
                    effectiveLimit = state.effectiveLimit,
                    effectiveRemaining = state.effectiveRemaining,
                    effectiveIsExceeded = state.effectiveIsExceeded,
                    onUpgradeClick = onUpgradeClick,
                )
            }
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
                enabled = state.canSend,
                onSend = onSend,
            )
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        ) { Snackbar(snackbarData = it) }

        if (state.showPaywall) {
            ChatPremiumPaywallDialog(
                isPurchasing = state.isPurchasing,
                onConfirm = onPaywallConfirm,
                onDismiss = onPaywallDismiss,
            )
        }
    }
}

@Composable
private fun ChatPremiumPaywallDialog(isPurchasing: Boolean, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { if (!isPurchasing) onDismiss() },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Chat,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(stringResource(Res.string.chat_paywall_title))
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = stringResource(Res.string.chat_paywall_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                )
                PaywallBullet(stringResource(Res.string.chat_paywall_bullet_questions))
                PaywallBullet(stringResource(Res.string.chat_paywall_bullet_priority))
                PaywallBullet(stringResource(Res.string.chat_paywall_bullet_cancel))
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isPurchasing,
                modifier = Modifier.heightIn(min = 44.dp),
            ) {
                if (isPurchasing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                } else {
                    Text(stringResource(Res.string.chat_paywall_cta))
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isPurchasing) {
                Text(stringResource(Res.string.chat_paywall_dismiss))
            }
        },
    )
}

@Composable
private fun PaywallBullet(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Outlined.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp),
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
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
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
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
                val contentColor = if (isUser) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
                if (message.content.isEmpty() && isStreamingLast) {
                    Text(
                        text = "…",
                        style = MaterialTheme.typography.bodyMedium,
                        color = contentColor,
                    )
                } else if (isUser) {
                    Text(
                        text = message.content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = contentColor,
                    )
                } else {
                    Text(
                        text = renderSimpleMarkdown(message.content),
                        style = MaterialTheme.typography.bodyMedium,
                        color = contentColor,
                    )
                }
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
                placeholder = { Text(stringResource(Res.string.chat_input_placeholder)) },
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
                    contentDescription = stringResource(Res.string.chat_send_content_description),
                )
            }
        }
    }
}

@Composable
private fun EmptyState(onSuggestionClick: (String) -> Unit, modifier: Modifier = Modifier) {
    val suggestions = listOf(
        stringResource(Res.string.chat_suggestion_1),
        stringResource(Res.string.chat_suggestion_2),
        stringResource(Res.string.chat_suggestion_3),
        stringResource(Res.string.chat_suggestion_4),
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.chat_empty_state_title),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(Res.string.chat_empty_state_subtitle),
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
private fun QuotaBanner(
    quota: ChatQuota,
    effectivePremium: Boolean,
    effectiveLimit: Int,
    effectiveRemaining: Int,
    effectiveIsExceeded: Boolean,
    onUpgradeClick: () -> Unit,
) {
    val showUpgrade = !effectivePremium
    val bg = when {
        effectiveIsExceeded -> MaterialTheme.colorScheme.errorContainer
        effectivePremium -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.tertiaryContainer
    }
    val fg = when {
        effectiveIsExceeded -> MaterialTheme.colorScheme.onErrorContainer
        effectivePremium -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onTertiaryContainer
    }
    Surface(color = bg, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                if (effectivePremium) {
                    Text(
                        text = stringResource(
                            Res.string.chat_quota_banner_premium,
                            quota.count,
                            effectiveLimit,
                        ),
                        style = MaterialTheme.typography.labelLarge,
                        color = fg,
                    )
                } else {
                    Text(
                        text = stringResource(
                            Res.string.chat_quota_banner,
                            quota.count,
                            effectiveLimit,
                        ),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = fg,
                    )
                    Text(
                        text = if (effectiveIsExceeded) {
                            stringResource(Res.string.chat_quota_exceeded_subtitle)
                        } else {
                            stringResource(Res.string.chat_quota_remaining_subtitle)
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = fg,
                    )
                }
            }
            if (showUpgrade) {
                Spacer(modifier = Modifier.width(12.dp))
                Button(onClick = onUpgradeClick) {
                    Text(stringResource(Res.string.chat_quota_upgrade_cta))
                }
            }
        }
    }
}

/**
 * Minimal Markdown renderer for assistant bubbles.
 */
private fun renderSimpleMarkdown(text: String): AnnotatedString = buildAnnotatedString {
    val lines = text.lines()
    lines.forEachIndexed { index, raw ->
        val line = when {
            raw.startsWith("- ") -> "• " + raw.drop(2)
            raw.startsWith("* ") && !raw.startsWith("**") -> "• " + raw.drop(2)
            else -> raw
        }
        appendMarkdownInline(line)
        if (index != lines.lastIndex) append('\n')
    }
}

private fun AnnotatedString.Builder.appendMarkdownInline(line: String) {
    var i = 0
    while (i < line.length) {
        val c = line[i]
        if (c == '*' && i + 1 < line.length && line[i + 1] == '*') {
            val end = line.indexOf("**", i + 2)
            if (end > 0 && end > i + 2) {
                pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                appendMarkdownInline(line.substring(i + 2, end))
                pop()
                i = end + 2
                continue
            }
        }
        if ((c == '*' || c == '_') && i + 1 < line.length && line[i + 1] != c) {
            val end = line.indexOf(c, i + 1)
            if (end > 0 && end > i + 1) {
                pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                append(line.substring(i + 1, end))
                pop()
                i = end + 1
                continue
            }
        }
        if (c == '`') {
            val end = line.indexOf('`', i + 1)
            if (end > 0) {
                pushStyle(SpanStyle(fontFamily = FontFamily.Monospace))
                append(line.substring(i + 1, end))
                pop()
                i = end + 1
                continue
            }
        }
        append(c)
        i++
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
            onUpgradeClick = {},
            onPaywallConfirm = {},
            onPaywallDismiss = {},
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
                        content = "Recommend me sci-fi films",
                        timestamp = 0L,
                    ),
                    ChatMessage(
                        id = "2",
                        role = ChatMessage.Role.ASSISTANT,
                        content = "Sure, here are 5 must-watch:\n\n1. Blade Runner 2049\n2. Arrival",
                        timestamp = 0L,
                    ),
                ),
            ),
            onSend = {},
            onSuggestionClick = {},
            onErrorDismiss = {},
            onUpgradeClick = {},
            onPaywallConfirm = {},
            onPaywallDismiss = {},
        )
    }
}
