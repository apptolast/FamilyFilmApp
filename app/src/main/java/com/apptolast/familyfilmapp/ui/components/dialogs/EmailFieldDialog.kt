package com.apptolast.familyfilmapp.ui.components.dialogs

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.apptolast.familyfilmapp.R
import com.apptolast.familyfilmapp.ui.screens.login.components.SupportingErrorText
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme

@Composable
fun EmailFieldDialog(title: String, description: String, onConfirm: (String) -> Unit, onDismiss: () -> Unit) {
    val errorMessage = stringResource(id = R.string.email_blank)
    var memberEmail by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    if (memberEmail.isNotBlank()) {
                        onConfirm(memberEmail)
                        onDismiss()
                    }
                },
            ) {
                Text(stringResource(id = android.R.string.ok))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
            ) {
                Text(stringResource(id = android.R.string.cancel))
            }
        },
        title = {
            Text(title)
        },
        text = {
            OutlinedTextField(
                value = memberEmail,
                onValueChange = { memberEmail = it.trim() },
                label = { Text(text = stringResource(R.string.login_text_field_email)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(25.dp),
                isError = memberEmail.isBlank(),
                supportingText = {
                    if (memberEmail.isBlank()) {
                        SupportingErrorText(errorMessage = errorMessage)
                    }
                },
            )
        },
    )
}

@Preview
@Composable
private fun TextFieldDialogPreview() {
    FamilyFilmAppTheme {
        EmailFieldDialog(
            title = "title",
            description = "description",
            onConfirm = {},
            onDismiss = {},
        )
    }
}
