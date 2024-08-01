package com.apptolast.familyfilmapp.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.apptolast.familyfilmapp.R
import com.apptolast.familyfilmapp.navigation.Routes
import com.apptolast.familyfilmapp.ui.screens.login.components.AlertRecoverPassDialog
import com.apptolast.familyfilmapp.ui.screens.login.components.LoginMainContent
import com.apptolast.familyfilmapp.ui.screens.login.uistates.LoginRegisterState
import com.apptolast.familyfilmapp.ui.screens.login.uistates.LoginUiState
import com.apptolast.familyfilmapp.ui.screens.login.uistates.RecoverPassState
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import com.apptolast.familyfilmapp.utils.Constants

@Composable
fun LoginScreen(navController: NavController, viewModel: LoginViewModel = hiltViewModel()) {
    val snackBarHostState = remember { SnackbarHostState() }
    val loginUiState by viewModel.loginState.collectAsStateWithLifecycle()
    val recoverPassUIState by viewModel.recoverPassState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(key1 = loginUiState) {
        if (loginUiState.isLogged) {
            navController.navigate(Routes.Home.routes) {
                popUpTo(Routes.Login.routes) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    if (!loginUiState.errorMessage?.error.isNullOrBlank()) {
        LaunchedEffect(loginUiState.errorMessage) {
            snackBarHostState.showSnackbar(
                loginUiState.errorMessage!!.error,
                "Close",
                true,
                SnackbarDuration.Long,
            )
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackBarHostState) }) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center,
        ) {
            LoginContent(
                loginUiState = loginUiState,
                recoverPassState = recoverPassUIState,
                onClickLogin = { email, pass ->
                    when (loginUiState.screenState) {
                        is LoginRegisterState.Login -> viewModel.login(email, pass)
                        is LoginRegisterState.Register -> viewModel.register(email, pass)
                    }
                },
                onCLickRecoverPassword = {
                    // TODO
                },
                onClickScreenState = viewModel::changeScreenState,
                onClickGoogleButton = { viewModel.handleSignIn(context = context) },
                onRecoveryPassUpdate = {
                    // TODO
                },
            )
        }
    }
}

@Composable
fun LoginContent(
    loginUiState: LoginUiState,
    recoverPassState: RecoverPassState,
    onClickLogin: (String, String) -> Unit,
    onCLickRecoverPassword: (String) -> Unit,
    onClickGoogleButton: () -> Unit,
    onClickScreenState: () -> Unit,
    onRecoveryPassUpdate: (RecoverPassState) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .alpha(
                when (loginUiState.isLoading) {
                    true -> 0.4f
                    false -> 1f
                },
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LoginMainContent(
            loginUiState = loginUiState,
            onClick = onClickLogin,
        )

        Row(
            modifier = Modifier
                .padding(6.dp)
                .clickable { onClickScreenState() },
        ) {
            Text(
                text = stringResource(loginUiState.screenState.accountText),
                modifier = Modifier.padding(end = 4.dp),
            )

            // TODO: Create Typography for this text.
            Text(
                text = stringResource(loginUiState.screenState.signText),
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.Bold,
            )
        }

        Text(
            modifier = Modifier.clickable {
                onRecoveryPassUpdate(
                    recoverPassState.copy(
                        isDialogVisible = true,
                        emailErrorMessage = null,
                        errorMessage = null,
                    ),
                )
            },
            text = stringResource(R.string.login_text_forgot_your_password),
            color = MaterialTheme.colorScheme.outline,
        )

        Button(
            onClick = onClickGoogleButton,
            modifier = Modifier.padding(vertical = 10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(R.drawable.logo_google),
                    contentDescription = stringResource(R.string.login_icon_google),
                    modifier = Modifier
                        .size(30.dp)
                        .padding(end = 6.dp),
                )
                Text(stringResource(R.string.login_text_sign_in_with_google))
            }
        }
    }

    if (loginUiState.isLoading) {
        CircularProgressIndicator(
            modifier = Modifier.testTag(Constants.CIRCULAR_PROGRESS_INDICATOR),
        )
    }

    if (recoverPassState.isDialogVisible) {
        AlertRecoverPassDialog(
            onCLickSend = onCLickRecoverPassword,
            recoverPassState = recoverPassState,
            dismissDialog = {
                onRecoveryPassUpdate(
                    recoverPassState.copy(
                        isDialogVisible = false,
                    ),
                )
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    FamilyFilmAppTheme {
        LoginContent(
            loginUiState = LoginUiState(),
            recoverPassState = RecoverPassState(),
            onClickLogin = { _, _ -> },
            onCLickRecoverPassword = {},
            onClickGoogleButton = {},
            onClickScreenState = {},
            onRecoveryPassUpdate = {},
        )
    }
}
