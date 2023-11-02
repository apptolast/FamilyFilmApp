package com.digitalsolution.familyfilmapp.ui.screens.groups.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
fun BottomSheetGroupScreenContent(addMemberUiState: AddMemberUiState) {
    val context = LocalContext.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 36.dp),
    ) {
        CustomSpacer(size = 8.dp)
        CardHandlerDivider()
        CustomSpacer(size = 16.dp)
        CardTitle(text = R.string.groups_text_add_member_bottomSheetModal)
        CardSubtitle(text = R.string.groups_text_add_member_bottomSheetModal_help)
        CustomSpacer(size = 16.dp)
        OutlinedTextField(
            value = addMemberUiState!!.email.value,
            onValueChange = { addMemberUiState!!.email.value = it.trim() },
            modifier = Modifier.fillMaxWidth(),
            label = {
                IconButton(onClick = { }) {
                    Text(text = stringResource(id = R.string.send_text))
                }
            },
            trailingIcon = { Text(text = stringResource(id = R.string.send_text)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            shape = RoundedCornerShape(25.dp),
            isError = !addMemberUiState!!.emailErrorMessage?.error.isNullOrBlank(),
            supportingText = {
                SupportingErrorText(addMemberUiState!!.emailErrorMessage?.error)
            },
        )
        CustomSpacer(size = 24.dp)
        CustomSmallButton(onClick = { }, text = R.string.send_text, enabled = true)
    }
}
