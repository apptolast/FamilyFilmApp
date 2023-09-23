package com.digitalsolution.familyfilmapp.ui.screens.login

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.digitalsolution.familyfilmapp.R
import com.digitalsolution.familyfilmapp.navigation.Routes
import com.digitalsolution.familyfilmapp.ui.screens.login.components.AlertRecoverPassDialog
import com.digitalsolution.familyfilmapp.ui.screens.login.components.LoginMainContent
import com.digitalsolution.familyfilmapp.ui.screens.login.uistates.LoginUiState
import com.digitalsolution.familyfilmapp.ui.screens.login.uistates.RecoverPassUiState
import com.digitalsolution.familyfilmapp.ui.theme.FamilyFilmAppTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel(),
) {

    val snackBarHostState = remember { SnackbarHostState() }
    val loginUiState by viewModel.state.collectAsStateWithLifecycle()
    val recoverPassUIState by viewModel.recoverPassUIState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = loginUiState) {
        if (loginUiState.isLogged) {
            navController.navigate(Routes.Home.routes)
        }
    }

    if (!loginUiState.errorMessage?.error.isNullOrBlank()) {
        LaunchedEffect(loginUiState.errorMessage) {
            snackBarHostState.showSnackbar(
                "Firebase Message : ${loginUiState.errorMessage!!.error}",
                "Close",
                true,
                SnackbarDuration.Long
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
                    viewModel.handleGoogleSignInResult(task)
                }
            }
        }

    Scaffold(snackbarHost = { SnackbarHost(snackBarHostState) }) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            LoginContent(
                loginUiState = loginUiState,
                recoverPassUIState = recoverPassUIState,
                onClickLogin = viewModel::loginOrRegister,
                onCLickRecoverPassword = viewModel::recoverPassword,
                onClickScreenState = viewModel::changeScreenState,
                onClickGoogleButton = { startForResult.launch(viewModel.googleSignInClient.signInIntent) },
                onRecoveryPassUpdate = viewModel::updateRecoveryPasswordState
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
    modifier: Modifier = Modifier,
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .alpha(
                when (loginUiState.isLoading) {
                    true -> 0.4f
                    false -> 1f
                }
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LoginMainContent(
            loginUiState = loginUiState,
            onClick = onClickLogin
        )

        Row(
            modifier = Modifier
                .padding(6.dp)
                .clickable { onClickScreenState() }
        ) {
            Text(
                text = stringResource(loginUiState.screenState.accountText),
                modifier = Modifier.padding(end = 4.dp)
            )

            // TODO: Create Typography for this text.
            Text(
                text = stringResource(loginUiState.screenState.signText),
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            modifier = Modifier.clickable {
                onRecoveryPassUpdate(
                    recoverPassUIState.copy(
                        isDialogVisible = mutableStateOf(true),
                        emailErrorMessage = null,
                        errorMessage = null
                    )
                )
            },
            text = stringResource(R.string.login_text_forgot_your_password),
            color = MaterialTheme.colorScheme.outline
        )

        Button(
            onClick = onClickGoogleButton,
            modifier = Modifier.padding(vertical = 10.dp)
        ) {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.logo_google),
                    contentDescription = stringResource(R.string.login_icon_google),
                    modifier = Modifier
                        .size(30.dp)
                        .padding(end = 6.dp)
                )
                Text(stringResource(R.string.login_text_sign_in_with_google))
            }
        }
    }

    if (loginUiState.isLoading) {
        CircularProgressIndicator()
    }

    if (recoverPassUIState.isDialogVisible.value) {
        AlertRecoverPassDialog(
            onCLickSend = onCLickRecoverPassword,
            recoverPassUIState = recoverPassUIState
        )
    }

}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    FamilyFilmAppTheme {
        LoginContent(
            loginUiState = LoginUiState(),
            recoverPassUIState = RecoverPassUiState(),
            onClickLogin = { _, _ -> },
            onCLickRecoverPassword = {},
            onClickGoogleButton = {},
            onClickScreenState = {},
            modifier = Modifier,
            onRecoveryPassUpdate = {}
        )
    }
}
