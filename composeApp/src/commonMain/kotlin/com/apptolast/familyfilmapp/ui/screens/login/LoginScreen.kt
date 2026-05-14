package com.apptolast.familyfilmapp.ui.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.apptolast.familyfilmapp.ui.screens.login.uistates.LoginRegisterState
import com.apptolast.familyfilmapp.ui.sharedViewmodel.AuthState
import com.apptolast.familyfilmapp.ui.sharedViewmodel.AuthViewModel
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Login + Register form. Pared down to the essentials so the migration
 * compiles and the auth flow exercises the full Koin / Firebase / Repository
 * graph end-to-end. The fully-styled legacy screen (550 lines of branding,
 * dialogs, Google button, recover-password sheet, etc.) is planned as a
 * polish pass after the migration lands.
 */
@Composable
fun LoginScreen(viewModel: AuthViewModel = koinViewModel()) {
    val authState by viewModel.authState.collectAsState()
    val screenState by viewModel.screenState.collectAsState()
    val isEmailSent by viewModel.isEmailSent.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var usernameInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Family Film",
            style = MaterialTheme.typography.displaySmall,
        )
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        if (screenState is LoginRegisterState.Register) {
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = usernameInput,
                onValueChange = {
                    usernameInput = it
                    viewModel.onUsernameChange(it)
                },
                label = { Text("Username") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = {
                when (screenState) {
                    is LoginRegisterState.Login -> viewModel.login(email, password)
                    is LoginRegisterState.Register -> viewModel.registerAndSendEmail(email, password, usernameInput)
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(screenState.buttonText))
        }

        Spacer(Modifier.height(8.dp))
        OutlinedButton(
            onClick = { viewModel.googleSignIn() },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Sign in with Google")
        }

        Spacer(Modifier.height(8.dp))
        TextButton(onClick = { viewModel.changeScreenState() }) {
            Text(
                text = stringResource(screenState.accountText) + " " + stringResource(screenState.signText),
            )
        }

        when (val state = authState) {
            is AuthState.Loading -> {
                Spacer(Modifier.height(16.dp))
                Text("Loading…", style = MaterialTheme.typography.bodySmall)
            }
            is AuthState.Error -> {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = state.message.orEmpty(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            else -> Unit
        }

        if (isEmailSent) {
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Verification email sent. Check your inbox.",
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
@Preview
private fun PreviewLoginScreen() {
    FamilyFilmAppTheme {
        // Preview uses no Koin scope; renders the static skeleton only.
    }
}
