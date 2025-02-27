package com.apptolast.familyfilmapp.ui.components.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.apptolast.familyfilmapp.R
import com.apptolast.familyfilmapp.ui.screens.login.uistates.RecoverPassState
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme

@Composable
fun AlertRecoverPassDialog(
    recoverPassState: RecoverPassState,
    onCLickSend: (String) -> Unit,
    dismissDialog: () -> Unit,
) {
    var email by remember { mutableStateOf(recoverPassState.email) }
    var isEmailValid by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = dismissDialog,
        title = {
            Text(text = stringResource(R.string.login_text_forgot_your_password))
        },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.login_text_recover_password_description),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                TextField(
                    value = email,
                    onValueChange = {
                        email = it.trim()
                        isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() || email.isEmpty()
                    },
                    label = { Text(text = stringResource(R.string.login_text_field_email)) },
                    singleLine = true,
                    isError = !isEmailValid || recoverPassState.emailErrorMessage != null,
                    supportingText = {
                        if (!isEmailValid) {
                            Text(text = stringResource(R.string.login_text_email_invalid))
                        }
                        recoverPassState.emailErrorMessage?.let {
                            Text(text = it)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                if (recoverPassState.errorMessage != null) {
                    Text(
                        text = recoverPassState.errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                if (recoverPassState.isSuccessful) {
                    Text(
                        text = stringResource(R.string.login_text_recover_password_sent),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                if (recoverPassState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (email.isNotEmpty() && isEmailValid) {
                        onCLickSend(email)
                    } else {
                        isEmailValid = false
                    }
                },
                enabled = !recoverPassState.isLoading && !recoverPassState.isSuccessful
            ) {
                Text(text = stringResource(R.string.login_button_send))
            }
        },
        dismissButton = {
            Button(
                onClick = dismissDialog,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text(text = stringResource(R.string.login_button_cancel))
            }
        }
    )
}

@Preview
@Composable
private fun AlertRecoverPassDialogPreview() {
    FamilyFilmAppTheme {
        AlertRecoverPassDialog(
            recoverPassState = RecoverPassState().copy(
                isDialogVisible = true,
                isLoading = true,
            ),
            onCLickSend = {},
            dismissDialog = {},
        )
    }
}
