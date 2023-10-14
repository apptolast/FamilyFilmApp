package com.digitalsolution.familyfilmapp.ui.screens.login.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.digitalsolution.familyfilmapp.ui.screens.login.uistates.LoginUiState
import com.digitalsolution.familyfilmapp.ui.theme.FamilyFilmAppLoginButtonTheme
import com.digitalsolution.familyfilmapp.ui.theme.FamilyFilmAppTheme

@Composable
fun LoginMainContent(
    loginUiState: LoginUiState,
    onClick: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {

    var email by rememberSaveable { mutableStateOf(loginUiState.user.email) }
    var pass by rememberSaveable { mutableStateOf(loginUiState.user.pass) }
    val (isPasswordVisible, passwordToVisible) = remember { mutableStateOf(false) }

    Card {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.logo_film_family),
                contentDescription = stringResource(R.string.login_snail_logo),
                modifier = modifier
                    .width(134.dp)
                    .padding(8.dp),
            )
            Text(
                text = stringResource(R.string.login_text_app_title),
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = stringResource(R.string.login_text_app_subtitle),
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it.trim() },
                modifier = modifier.fillMaxWidth(),
                label = { Text(text = stringResource(R.string.login_text_field_email)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(25.dp),
                isError = !loginUiState.emailErrorMessage?.error.isNullOrBlank(),
                supportingText = {
                    SupportingErrorText(loginUiState.emailErrorMessage?.error)
                }
            )

            Spacer(modifier = modifier.height(4.dp))

            OutlinedTextField(
                value = pass,
                onValueChange = { pass = it.trim() },
                modifier = modifier.fillMaxWidth(),
                trailingIcon = {
                    LoginPasswordIcon(
                        isPasswordVisible = isPasswordVisible,
                        passwordToVisible = { passwordToVisible(!isPasswordVisible) }
                    )
                },
                visualTransformation = when (isPasswordVisible) {
                    false -> PasswordVisualTransformation()
                    true -> VisualTransformation.None
                },
                label = { Text(text = stringResource(R.string.login_text_field_password)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(25.dp),
                isError = !loginUiState.passErrorMessage?.error.isNullOrBlank(),
                supportingText = {
                    SupportingErrorText(loginUiState.passErrorMessage?.error)
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            FamilyFilmAppLoginButtonTheme(dynamicColor = false) {
                Button(
                    onClick = { onClick(email, pass) },
                    modifier = modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(id = loginUiState.screenState.buttonText),
                        modifier = modifier.padding(4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SupportingErrorText(errorMessage: String?, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        errorMessage?.let {
            Icon(
                imageVector = Icons.Filled.Error,
                contentDescription = "Error",
                modifier = modifier.padding(4.dp)
            )
            Text(text = it)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CardLoginMainPreview() {
    FamilyFilmAppTheme {
        LoginMainContent(
            loginUiState = LoginUiState(),
            onClick = { _, _ -> }
        )
    }
}
