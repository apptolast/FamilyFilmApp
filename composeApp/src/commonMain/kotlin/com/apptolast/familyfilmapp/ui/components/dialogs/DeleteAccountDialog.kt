package com.apptolast.familyfilmapp.ui.components.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import com.apptolast.familyfilmapp.utils.TT_DELETE_ACCOUNT_CONFIRM
import com.apptolast.familyfilmapp.utils.TT_DELETE_ACCOUNT_EMAIL
import com.apptolast.familyfilmapp.utils.TT_DELETE_ACCOUNT_PASSWORD
import familyfilmkmp.composeapp.generated.resources.Res
import familyfilmkmp.composeapp.generated.resources.delete_account_action
import familyfilmkmp.composeapp.generated.resources.delete_account_dialog_description
import familyfilmkmp.composeapp.generated.resources.delete_account_dialog_title
import familyfilmkmp.composeapp.generated.resources.delete_account_email
import familyfilmkmp.composeapp.generated.resources.delete_account_email_error
import familyfilmkmp.composeapp.generated.resources.delete_account_password
import familyfilmkmp.composeapp.generated.resources.delete_account_password_error
import familyfilmkmp.composeapp.generated.resources.dialog_cancel
import org.jetbrains.compose.resources.stringResource

@Composable
fun DeleteAccountDialog(
    onDismiss: () -> Unit = {},
    onConfirm: (email: String, password: String) -> Unit = { _, _ -> },
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isEmailError by remember { mutableStateOf(false) }
    var isPasswordError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(Res.string.delete_account_dialog_title),
                style = MaterialTheme.typography.headlineSmall,
            )
        },
        text = {
            Column {
                Text(
                    text = stringResource(Res.string.delete_account_dialog_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        isEmailError = false
                    },
                    label = { Text(stringResource(Res.string.delete_account_email)) },
                    singleLine = true,
                    isError = isEmailError,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(TT_DELETE_ACCOUNT_EMAIL),
                )

                if (isEmailError) {
                    Text(
                        text = stringResource(Res.string.delete_account_email_error),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp),
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        isPasswordError = false
                    },
                    label = { Text(stringResource(Res.string.delete_account_password)) },
                    singleLine = true,
                    isError = isPasswordError,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(TT_DELETE_ACCOUNT_PASSWORD),
                )

                if (isPasswordError) {
                    Text(
                        text = stringResource(Res.string.delete_account_password_error),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp),
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    when {
                        email.isBlank() -> isEmailError = true
                        password.isBlank() -> isPasswordError = true
                        else -> onConfirm(email, password)
                    }
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error,
                ),
                modifier = Modifier.testTag(TT_DELETE_ACCOUNT_CONFIRM),
            ) {
                Text(stringResource(Res.string.delete_account_action))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.dialog_cancel))
            }
        },
    )
}

@Preview
@Composable
private fun PreviewDeleteAccountDialog() {
    FamilyFilmAppTheme {
        DeleteAccountDialog()
    }
}
