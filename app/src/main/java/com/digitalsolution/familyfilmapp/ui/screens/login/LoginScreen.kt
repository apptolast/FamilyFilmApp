package com.digitalsolution.familyfilmapp.ui.screens.login

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.digitalsolution.familyfilmapp.R
import com.digitalsolution.familyfilmapp.ui.screens.login.components.CardLoginScreen
import com.digitalsolution.familyfilmapp.ui.screens.login.components.CardLoginsButton
import com.digitalsolution.familyfilmapp.ui.theme.FamilyFilmAppTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope: CoroutineScope = rememberCoroutineScope()

    val loginUiState by loginViewModel.state.collectAsStateWithLifecycle(lifecycleOwner)

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
        LoginContent(
            loginUiState = loginUiState,
            onClickLogin = loginViewModel::login,
            //onClickLogin = { email, pass ->
            //    coroutineScope.launch {
            //        snackBarHostState.showSnackbar("User: $email\nPass: $pass", "Close", true, SnackbarDuration.Short)
            //    }
            //},
            onClickGoogleButton = { startForResult.launch(loginViewModel.getGoogleSignInIntent()) },
            modifier = Modifier.padding(innerPadding),
        )
    }

    LaunchedEffect(key1 = true) {
        if (loginUiState.hasError) {
            snackBarHostState.showSnackbar(
                loginUiState.errorMessage,
                "Close",
                true,
                SnackbarDuration.Long
            )
        } else {
            snackBarHostState.showSnackbar(
                "Login: ${loginUiState.userData.isLogin}",
                "Close",
                true,
                SnackbarDuration.Short
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
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CardLoginScreen(
            textFieldEmailState = loginUiState.userData.email,
            textPasswordState = loginUiState.userData.pass,
            onClick = onClickLogin
        )
        CardLoginsButton(
            text = stringResource(R.string.login_text_sign_in_with_google),
            backgroundColor = MaterialTheme.colorScheme.surface,
            paddingVertical = 13.dp,
            contentImage = {
                Image(
                    painter = painterResource(R.drawable.logo_google),
                    contentDescription = stringResource(R.string.login_icon_google),
                    modifier = Modifier
                        .size(30.dp)
                        .padding(end = 6.dp)
                )
            },
            onCLick = onClickGoogleButton

        )
        Text(
            text = stringResource(R.string.login_text_sign_up),
            modifier = Modifier.padding(6.dp)
        )

        // TODO: Revise when review theme colors
        Text(text = stringResource(R.string.login_text_forgot_your_password), color = Color.Blue)
    }
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


