package com.apptolast.familyfilmapp.ui.components.dialogs

import android.util.Patterns
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
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
import com.apptolast.familyfilmapp.utils.UsernameValidator

@Composable
fun EmailFieldDialog(title: String, onConfirm: (String) -> Unit, onDismiss: () -> Unit) {
    var input by rememberSaveable { mutableStateOf("") }

    val isValidEmail = input.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(input).matches()
    val isValidUsername = input.isNotBlank() &&
        UsernameValidator.validate(input) == UsernameValidator.Result.Valid
    val isValid = isValidEmail || isValidUsername

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    if (isValid) {
                        onConfirm(input)
                        onDismiss()
                    }
                },
                enabled = isValid,
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
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
            )
        },
        text = {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it.trim() },
                label = { Text(text = stringResource(R.string.add_member_input_label)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(25.dp),
                isError = input.isNotBlank() && !isValid,
                supportingText = {
                    if (input.isBlank()) {
                        Text(text = stringResource(R.string.add_member_input_hint))
                    } else if (!isValid) {
                        SupportingErrorText(
                            errorMessage = stringResource(R.string.add_member_input_invalid),
                        )
                    }
                },
            )
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun TextFieldDialogPreview() {
    FamilyFilmAppTheme {
        EmailFieldDialog(
            title = "title",
            onConfirm = {},
            onDismiss = {},
        )
    }
}
