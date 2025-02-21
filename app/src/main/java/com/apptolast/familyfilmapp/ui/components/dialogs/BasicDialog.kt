package com.apptolast.familyfilmapp.ui.components.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme

@Composable
fun BasicDialog(
    title: String,
    description: String,
    confirmButtonText: String,
    cancelButtonText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
                    onDismiss()
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
private fun BasicDialogPreview() {
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
