package com.digitalsolution.familyfilmapp.ui.screens.login.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.digitalsolution.familyfilmapp.R
import com.digitalsolution.familyfilmapp.ui.theme.FamilyFilmAppTheme

@Composable
fun CardLoginMain(
    textFieldEmailState: String,
    changeEmailState: (String) -> Unit,
    textPasswordState: String,
    changePasswordState: (String) -> Unit,
) {
    val (isPasswordVisible, passwordToVisible) = remember { mutableStateOf(false) }

    CardLoginMainContent(
        textFieldEmailState = textFieldEmailState,
        changeEmailState = changeEmailState,
        textPasswordState = textPasswordState,
        changePasswordState = changePasswordState,
        isPasswordVisible = isPasswordVisible,
        passwordToVisible = { passwordToVisible(!isPasswordVisible) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardLoginMainContent(
    textFieldEmailState: String,
    changeEmailState: (String) -> Unit,
    textPasswordState: String,
    changePasswordState: (String) -> Unit,
    isPasswordVisible: Boolean,
    passwordToVisible: () -> Unit,
    modifier: Modifier = Modifier
) {

    val textFieldColor = TextFieldDefaults.textFieldColors(
        textColor = Color.DarkGray, containerColor = Color.White
    )

    Card {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.logo_film_family),
                contentDescription = stringResource(R.string.login_snail_logo),
                modifier = modifier
                    .width(134.dp)
                    .padding(8.dp)
            )
            Text(
                text = stringResource(R.string.login_text_app_title),
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = stringResource(R.string.login_text_app_subtitle),
                style = MaterialTheme.typography.titleMedium
            )
            TextField(
                value = textFieldEmailState,
                onValueChange = changeEmailState,
                label = {
                    Text(
                        text = stringResource(R.string.login_text_field_email),
                        color = Color.Gray
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = modifier
                    .padding(top = 20.dp),
                colors = textFieldColor
            )
            Spacer(modifier = modifier.height(2.dp))
            TextField(
                value = textPasswordState,
                onValueChange = changePasswordState,
                modifier = modifier
                    .padding(top = 12.dp)
                    .padding(bottom = 10.dp),
                trailingIcon = {
                    TrailingIconPassword(
                        isPasswordVisible = isPasswordVisible,
                        passwordToVisible = passwordToVisible
                    )
                },
                label = {
                    Text(
                        text = stringResource(R.string.login_text_field_password),
                        color = Color.Gray
                    )
                },
                visualTransformation = if (isPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = textFieldColor
            )
            CardLoginsButton(
                text = stringResource(R.string.login_text_button),
                backgroundColor = MaterialTheme.colorScheme.tertiary,
                paddingVertical = 1.dp,
                textColor = MaterialTheme.colorScheme.surface
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CardLoginMainPreview() {
    FamilyFilmAppTheme {
        CardLoginMain(
            textFieldEmailState = "",
            textPasswordState = "",
            changeEmailState = {},
            changePasswordState = {}
        )
    }
}
