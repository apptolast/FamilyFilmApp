package com.apptolast.familyfilmapp.ui.screens.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.apptolast.familyfilmapp.ui.screens.login.components.UsernameSetupDialog
import com.apptolast.familyfilmapp.ui.screens.login.uistates.LoginRegisterState
import com.apptolast.familyfilmapp.ui.sharedViewmodel.AuthViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(viewModel: AuthViewModel = koinViewModel()) {
    val authState by viewModel.authState.collectAsState()
    val screenState by viewModel.screenState.collectAsState()
    val initialEmail by viewModel.email.collectAsState()
    val initialPassword by viewModel.password.collectAsState()
    val usernameValue by viewModel.username.collectAsState()
    val usernameValidationState by viewModel.usernameValidationState.collectAsState()
    val isEmailSent by viewModel.isEmailSent.collectAsState()
    val recoverPassState by viewModel.recoverPassState.collectAsState()
    val shouldPromptForUsername by viewModel.shouldPromptForUsername.collectAsState()

    LoginContent(
        initialEmail = initialEmail,
        initialPassword = initialPassword,
        username = usernameValue,
        screenState = screenState,
        authState = authState,
        isEmailSent = isEmailSent,
        usernameValidationState = usernameValidationState,
        recoverPassState = recoverPassState,
        onUsernameChange = viewModel::onUsernameChange,
        onPrimaryClick = { emailVal, passVal ->
            when (screenState) {
                is LoginRegisterState.Login -> viewModel.login(emailVal, passVal)
                is LoginRegisterState.Register ->
                    viewModel.registerAndSendEmail(emailVal, passVal, usernameValue)
            }
        },
        onGoogleClick = { viewModel.googleSignIn() },
        onToggleScreenState = viewModel::changeScreenState,
        onRecoveryPassUpdate = viewModel::updateRecoveryPasswordState,
        onRecoverPassword = viewModel::recoverPassword,
        onClearError = viewModel::clearFailure,
    )

    if (shouldPromptForUsername) {
        UsernameSetupDialog(
            usernameValidationState = usernameValidationState,
            onUsernameChange = viewModel::onUsernameChange,
            onConfirm = viewModel::saveUsernameForNewUser,
            onSkip = viewModel::skipUsernameSetup,
        )
    }
}
