package com.apptolast.familyfilmapp.ui.screens.login

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.apptolast.familyfilmapp.ui.screens.login.uistates.LoginUiState
import com.apptolast.familyfilmapp.ui.screens.login.uistates.RecoverPassUiState
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import com.apptolast.familyfilmapp.utils.Constants
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import timber.log.Timber

@Composable
fun LoginScreen(navController: NavController, viewModel: LoginViewModel = hiltViewModel()) {
    val snackBarHostState = remember { SnackbarHostState() }
    val loginUiState by viewModel.state.collectAsStateWithLifecycle()
    val recoverPassUIState by viewModel.recoverPassUIState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = loginUiState) {
        if (loginUiState.isLogged == true) {
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

    val startForResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                if (intent != null) {
                    val task: Task<GoogleSignInAccount> =
                        GoogleSignIn.getSignedInAccountFromIntent(intent)
                    viewModel.handleGoogleSignInResult(task.result as GoogleSignInAccount)
                }
            } else {
                Timber.d("$result")
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
                recoverPassUIState = recoverPassUIState,
                onClickLogin = viewModel::loginOrRegister,
                onCLickRecoverPassword = viewModel::recoverPassword,
                onClickScreenState = viewModel::changeScreenState,
                onClickGoogleButton = {
                    startForResult.launch(
                        viewModel.googleSignInClient.signInIntent,
                    )
                },
                onRecoveryPassUpdate = viewModel::updateRecoveryPasswordState,
            )
        }
    }
}

@Composable
fun LoginContent(
    loginUiState: LoginUiState,
    recoverPassUIState: RecoverPassUiState,
    onClickLogin: (String, String) -> Unit,
    onCLickRecoverPassword: (String) -> Unit,
    onClickGoogleButton: () -> Unit,
    onClickScreenState: () -> Unit,
    onRecoveryPassUpdate: (RecoverPassUiState) -> Unit,
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
                    recoverPassUIState.copy(
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

    if (recoverPassUIState.isDialogVisible) {
        AlertRecoverPassDialog(
            onCLickSend = onCLickRecoverPassword,
            recoverPassUIState = recoverPassUIState,
            dismissDialog = {
                onRecoveryPassUpdate(
                    recoverPassUIState.copy(
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
            recoverPassUIState = RecoverPassUiState(),
            onClickLogin = { _, _ -> },
            onCLickRecoverPassword = {},
            onClickGoogleButton = {},
            onClickScreenState = {},
            onRecoveryPassUpdate = {},
        )
    }
}
