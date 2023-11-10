package com.digitalsolution.familyfilmapp.ui.screens.login.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.digitalsolution.familyfilmapp.R
import com.digitalsolution.familyfilmapp.ui.screens.login.uistates.RecoverPassUiState
import com.digitalsolution.familyfilmapp.ui.theme.FamilyFilmAppTheme

@Composable
fun AlertRecoverPassDialog(
    recoverPassUIState: RecoverPassUiState,
    modifier: Modifier = Modifier,
    onCLickSend: (String) -> Unit,
) {
    var email by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = {
            recoverPassUIState.isDialogVisible.value = !recoverPassUIState.isDialogVisible.value
        },
        title = {
            Text(text = stringResource(id = R.string.login_text_recover_password))
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it.trim() },
                    modifier = modifier.fillMaxWidth(),
                    label = { Text(text = stringResource(R.string.login_text_field_email)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    shape = RoundedCornerShape(25.dp),
                    isError = !recoverPassUIState.emailErrorMessage?.error.isNullOrBlank(),
                    supportingText = {
                        SupportingErrorText(recoverPassUIState.emailErrorMessage?.error)
                    },
                )

                if (recoverPassUIState.isLoading) {
                    CircularProgressIndicator()
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onCLickSend(email)
                },
            ) {
                Text(
                    text = stringResource(R.string.login_text_send_recover_password),
                    fontSize = 20.sp,
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    recoverPassUIState.isDialogVisible.value = !recoverPassUIState.isDialogVisible.value
                },
            ) {
                Text(
                    text = stringResource(R.string.login_text_recover_password_cancel),
                    fontSize = 20.sp,
                )
            }
        },
    )
}

@Preview
@Composable
private fun AlertRecoverPassDialogPreview() {
    FamilyFilmAppTheme {
        AlertRecoverPassDialog(
            recoverPassUIState = RecoverPassUiState().copy(
                isDialogVisible = remember { mutableStateOf(true) },
                isLoading = true,
            ),
        ) {}
    }
}
