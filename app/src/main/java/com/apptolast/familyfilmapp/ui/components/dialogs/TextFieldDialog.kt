package com.apptolast.familyfilmapp.ui.components.dialogs

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
import androidx.compose.ui.tooling.preview.Preview
import com.apptolast.familyfilmapp.R
import com.apptolast.familyfilmapp.ui.screens.login.components.SupportingErrorText
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme

@Composable
fun TextFieldDialog(title: String, description: String, onConfirm: (String) -> Unit, onDismiss: () -> Unit) {
    var groupName by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    if (groupName.isNotBlank()) {
                        onConfirm(groupName)
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
                value = groupName,
                onValueChange = {
                    groupName = it
                },
                label = { Text(description) },
                singleLine = true,
                isError = groupName.isBlank(),
                supportingText = {
                    if (groupName.isBlank()) {
                        SupportingErrorText(stringResource(id = R.string.group_dialog_name_empty_error_message))
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
        TextFieldDialog(
            title = "title",
            description = "description",
            onConfirm = {},
            onDismiss = {},
        )
    }
}
