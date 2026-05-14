package com.apptolast.familyfilmapp.ui.screens.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ChatScreen(viewModel: ChatViewModel = koinViewModel()) {
    val state by viewModel.uiState.collectAsState()
    var input by remember { mutableStateOf("") }

    ChatContent(
        state = state,
        input = input,
        onInputChange = { input = it },
        onSend = {
            if (input.isNotBlank()) {
                viewModel.sendMessage(input)
                input = ""
            }
        },
    )
}
