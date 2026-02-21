package com.apptolast.familyfilmapp.ui.screens.login.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.apptolast.familyfilmapp.R
import com.apptolast.familyfilmapp.ui.sharedViewmodel.UsernameValidationState
import com.apptolast.familyfilmapp.utils.toErrorString

@Composable
fun UsernameSetupDialog(
    usernameValidationState: UsernameValidationState,
    onUsernameChange: (String) -> Unit,
    onConfirm: (String) -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var localUsername by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { /* Don't dismiss on outside tap */ },
        modifier = modifier,
        title = { Text(text = stringResource(R.string.username_prompt_title)) },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.username_prompt_description),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = localUsername,
                    onValueChange = {
                        localUsername = it.trim()
                        onUsernameChange(it.trim())
                    },
                    label = { Text(text = stringResource(R.string.username_label)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = usernameValidationState is UsernameValidationState.Taken ||
                        usernameValidationState is UsernameValidationState.Invalid,
                    trailingIcon = {
                        when (usernameValidationState) {
                            is UsernameValidationState.Checking ->
                                CircularProgressIndicator(modifier = Modifier.size(20.dp))

                            is UsernameValidationState.Available ->
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = stringResource(R.string.username_available),
                                    tint = MaterialTheme.colorScheme.primary,
                                )

                            is UsernameValidationState.Taken ->
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = stringResource(R.string.username_taken),
                                    tint = MaterialTheme.colorScheme.error,
                                )

                            else -> {}
                        }
                    },
                    supportingText = {
                        when (usernameValidationState) {
                            is UsernameValidationState.Taken ->
                                Text(
                                    text = stringResource(R.string.username_taken),
                                    color = MaterialTheme.colorScheme.error,
                                )

                            is UsernameValidationState.Invalid ->
                                Text(
                                    text = usernameValidationState.validationError.toErrorString(),
                                    color = MaterialTheme.colorScheme.error,
                                )

                            is UsernameValidationState.Available ->
                                Text(
                                    text = stringResource(R.string.username_available),
                                    color = MaterialTheme.colorScheme.primary,
                                )

                            else -> {}
                        }
                    },
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(localUsername) },
                enabled = usernameValidationState is UsernameValidationState.Available,
            ) {
                Text(text = stringResource(R.string.username_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onSkip) {
                Text(text = stringResource(R.string.username_prompt_skip))
            }
        },
    )
}
