package com.digitalsolution.familyfilmapp.ui.screens.groups.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.digitalsolution.familyfilmapp.R
import com.digitalsolution.familyfilmapp.ui.components.CardHandlerDivider
import com.digitalsolution.familyfilmapp.ui.components.CardSubtitle
import com.digitalsolution.familyfilmapp.ui.components.CardTitle
import com.digitalsolution.familyfilmapp.ui.components.CustomSmallButton
import com.digitalsolution.familyfilmapp.ui.components.CustomSpacer
import com.digitalsolution.familyfilmapp.ui.screens.groups.uistates.AddMemberUiState
import com.digitalsolution.familyfilmapp.ui.screens.login.components.SupportingErrorText

@Composable
fun BottomSheetGroupScreenContent(
    addMemberUiState: AddMemberUiState,
    onCLickAddMember: (Int, String) -> Unit,
) {
    val pattern = rememberSaveable{ Regex("^\\d+$") }
    CustomSpacer(size = 8.dp)
    CardHandlerDivider()
    CustomSpacer(size = 16.dp)
    CardTitle(text = R.string.groups_text_add_member_bottomSheetModal)
    CardSubtitle(text = R.string.groups_text_add_member_bottomSheetModal_help)
    CustomSpacer(size = 16.dp)
    OutlinedTextField(
        value = addMemberUiState.email.value,
        onValueChange = { addMemberUiState.email.value = it.trim() },
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(text = stringResource(id = R.string.login_text_field_email))
        },
        trailingIcon = {
            IconButton(
                onClick = {
                    onCLickAddMember(
                        5,
                        addMemberUiState.email.value,
                    )
                },
            ) {
                Text(text = stringResource(id = R.string.send_text))
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        shape = RoundedCornerShape(25.dp),
        isError = !addMemberUiState.emailErrorMessage?.error.isNullOrBlank(),
        supportingText = {
            SupportingErrorText(addMemberUiState.emailErrorMessage?.error)
        },
    )
    CustomSpacer(size = 10.dp)
    OutlinedTextField(
        value = addMemberUiState.groupID.value,
        onValueChange = {
            if (it.isEmpty() || it.matches(pattern)) {
                addMemberUiState.groupID.value = it
            }
        },
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(text = stringResource(id = R.string.member_added_numeric_value))  // Reemplaza con tu recurso de string
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        shape = RoundedCornerShape(25.dp)
    )

    CustomSpacer(size = 24.dp)
    CustomSmallButton(
        onClick = {
            onCLickAddMember(
                5,
                addMemberUiState.email.value,
            )
        },
        text = R.string.send_text,
        enabled = true,
    )
}
