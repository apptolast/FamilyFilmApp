package com.digitalsolution.familyfilmapp.ui.screens.login

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.digitalsolution.familyfilmapp.R
import com.digitalsolution.familyfilmapp.ui.screens.login.components.CardLoginScreen
import com.digitalsolution.familyfilmapp.ui.theme.FamilyFilmAppTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val loginUiState by loginViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(loginUiState, loginViewModel.isUserLogIn()) {
        when {
            loginUiState.userData.isLogin -> {
                // TODO: Show progres bar
                snackBarHostState.showSnackbar(
                    "Login... ${loginUiState.userData.isLogin} with account ${loginUiState.userData.email}",
                    "Close",
                    true,
                    SnackbarDuration.Long
                )
            }

            loginUiState.errorMessage?.isNotBlank() == true -> {
                snackBarHostState.showSnackbar(
                    "Firebase Message : ${loginUiState.errorMessage}",
                    "Close",
                    true,
                    SnackbarDuration.Long
                )
            }

        }
    }

    val startForResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                if (intent != null) {
                    val task: Task<GoogleSignInAccount> =
                        GoogleSignIn.getSignedInAccountFromIntent(intent)
                    loginViewModel.handleGoogleSignInResult(task)
                }
            }
        }

    Scaffold(snackbarHost = { SnackbarHost(snackBarHostState) }) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            LoginContent(
                loginUiState = loginUiState,
                onClickLogin = loginViewModel::loginOrRegister,
                onClickGoogleButton = { startForResult.launch(loginViewModel.googleSignInClient.signInIntent) },
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}

@Composable
fun LoginContent(
    loginUiState: LoginUiState,
    onClickLogin: (String, String) -> Unit,
    onClickGoogleButton: () -> Unit,
    modifier: Modifier = Modifier
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
        CardLoginScreen(
            loginUiState,
            onClick = onClickLogin
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onClickGoogleButton) {
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
        Text(
            text = stringResource(R.string.login_text_sign_up),
            modifier = Modifier.padding(6.dp)
        )

        // TODO: Review when review theme colors
        Text(text = stringResource(R.string.login_text_forgot_your_password))
    }

    if (loginUiState.isLoading)
        CircularProgressIndicator()

}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    FamilyFilmAppTheme {
        LoginContent(
            loginUiState = LoginUiState(),
            onClickLogin = { _, _ -> },
            onClickGoogleButton = {},
            modifier = Modifier
        )
    }
}


