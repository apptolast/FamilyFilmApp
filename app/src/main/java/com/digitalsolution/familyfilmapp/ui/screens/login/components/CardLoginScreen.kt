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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.digitalsolution.familyfilmapp.R
import com.digitalsolution.familyfilmapp.ui.screens.login.LoginUiState
import com.digitalsolution.familyfilmapp.ui.theme.FamilyFilmAppTheme

@Composable
fun CardLoginScreen(
    loginUiState: LoginUiState,
    onClick: (String, String) -> Unit
) {
    val (isPasswordVisible, passwordToVisible) = remember { mutableStateOf(false) }

    CardLoginMainContent(
        loginUiState,
        isPasswordVisible = isPasswordVisible,
        passwordToVisible = { passwordToVisible(!isPasswordVisible) },
        onClick = onClick
    )
}

@Composable
fun CardLoginMainContent(
    loginUiState: LoginUiState,
    isPasswordVisible: Boolean,
    passwordToVisible: () -> Unit,
    onClick: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf(loginUiState.userData.email) }
    var pass by remember { mutableStateOf(loginUiState.userData.pass) }

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
            LoginTextField(
                textFieldState = email,
                loginUiState = loginUiState,
                changeTextFieldState = { email = it },
                labelText = stringResource(R.string.login_text_field_email),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = modifier.padding(top = 8.dp),
            )
            Spacer(modifier = modifier.height(2.dp))
            LoginTextField(
                textFieldState = pass,
                loginUiState = loginUiState,
                changeTextFieldState = { pass = it },
                labelText = stringResource(R.string.login_text_field_password),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = modifier.padding(bottom = 10.dp),
                trailingIcon = {
                    TrailingIconPassword(
                        isPasswordVisible = isPasswordVisible,
                        passwordToVisible = passwordToVisible
                    )
                },
                visualTransformation = if (isPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                }
            )
            CardLoginsButton(
                text = stringResource(R.string.login_text_button),
                backgroundColor = MaterialTheme.colorScheme.tertiary,
                paddingVertical = 1.dp,
                textColor = MaterialTheme.colorScheme.surface,
                onCLick = { onClick(email, pass) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CardLoginMainPreview() {
    FamilyFilmAppTheme {
        CardLoginScreen(
            loginUiState = LoginUiState(),
            onClick = { _, _ -> }
        )
    }
}
