package com.digitalsolution.familyfilmapp.ui.components.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.digitalsolution.familyfilmapp.ui.theme.FamilyFilmAppTheme

@Composable
fun BasicDialog(
    title: String,
    description: String,
    confirmButtonText: String,
    cancelButtonText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    var groupName by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    if (groupName.isNotBlank()) {
                        onConfirm()
                        onDismiss()
                    }
                },
            ) {
                Text(confirmButtonText)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
            ) {
                Text(cancelButtonText)
            }
        },
        title = {
            Text(title)
        },
        text = {
            Text(description)
        },
    )
}

@Preview
@Composable
fun BasicDialogPreview() {
    FamilyFilmAppTheme {
        BasicDialog(
            title = "title",
            description = "description",
            confirmButtonText = "ok",
            cancelButtonText = "cancel",
            onConfirm = {},
            onDismiss = {},
        )
    }
}
