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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
        isPasswordVisible = isPasswordVisible
    ) { passwordToVisible(!isPasswordVisible) }
}

@Composable
fun CardLoginMainContent(
    textFieldEmailState: String,
    changeEmailState: (String) -> Unit,
    textPasswordState: String,
    changePasswordState: (String) -> Unit,
    isPasswordVisible: Boolean,
    passwordToVisible: () -> Unit,
) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.logo_film_family),
                contentDescription = stringResource(R.string.snail_logo),
                modifier = Modifier
                    .width(134.dp)
                    .padding(8.dp)
            )
            Text(
                text = stringResource(R.string.film_family),
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = stringResource(R.string.shared_your_films_with_your_family_and_friends),
                style = MaterialTheme.typography.titleMedium
            )
            LoginTextField(
                textFieldState = textFieldEmailState,
                changeTextFieldState = changeEmailState,
                labelText = stringResource(R.string.enter_your_email),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.padding(top = 8.dp),
            )
            Spacer(modifier = Modifier.height(2.dp))
            LoginTextField(
                textFieldState = textPasswordState,
                changeTextFieldState = changePasswordState,
                labelText = stringResource(R.string.enter_your_password),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.padding(bottom = 10.dp),
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
                text = stringResource(R.string.login),
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
