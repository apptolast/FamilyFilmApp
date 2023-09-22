package com.digitalsolution.familyfilmapp.ui.screens.login.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.digitalsolution.familyfilmapp.R
import com.digitalsolution.familyfilmapp.ui.screens.login.LoginUiState


@Composable
fun AlertRecoverPassDialog(
    openDialog: MutableState<Boolean>,
    onCLickSend: (String) -> Unit,
    loginUiState: LoginUiState,
    modifier: Modifier = Modifier
) {

    var email by rememberSaveable { mutableStateOf("") }

    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = !openDialog.value
            },
            title = {
                Text(text = stringResource(id = R.string.login_text_recover_password))
            },
            text = {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it.trim() },
                    modifier = modifier.fillMaxWidth(),
                    label = { Text(text = stringResource(R.string.login_text_field_email)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    shape = RoundedCornerShape(25.dp),
                    isError = !loginUiState.emailErrorMessage?.error.isNullOrBlank(),
                    supportingText = {
                        SupportingErrorText(loginUiState.emailErrorMessage?.error)
                    }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onCLickSend(email)
                    openDialog.value = !openDialog.value
                }) {
                    Text(
                        text = stringResource(R.string.login_text_send_recover_password),
                        fontSize = 20.sp
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    openDialog.value = !openDialog.value
                }) {
                    Text(
                        text = stringResource(R.string.login_text_recover_password_cancel),
                        fontSize = 20.sp
                    )
                }
            }
        )
    }
}