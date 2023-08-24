package com.digitalsolution.familyfilmapp.ui.screens.login

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.digitalsolution.familyfilmapp.R
import com.digitalsolution.familyfilmapp.ui.screens.login.components.CardLoginMain
import com.digitalsolution.familyfilmapp.ui.screens.login.components.CardLoginsButton
import com.digitalsolution.familyfilmapp.ui.theme.FamilyFilmAppTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    var textEmailState by remember { mutableStateOf("") }
    var textPasswordState by remember { mutableStateOf("") }
    val snackBarHostState = remember { SnackbarHostState() }
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
            innerPadding = innerPadding,
            textFieldEmailState = textEmailState,
            textPasswordState = textPasswordState,
            changeEmailState = { textEmailState = it },
            changePasswordState = { textPasswordState = it },
        ) {
            startForResult.launch(loginViewModel.googleSignIn())
        }
    }
}

@Composable
fun LoginContent(
    innerPadding: PaddingValues,
    textFieldEmailState: String,
    textPasswordState: String,
    changeEmailState: (String) -> Unit,
    changePasswordState: (String) -> Unit,
    onClickGoogleButton: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CardLoginMain(
            textFieldEmailState = textFieldEmailState,
            changeEmailState = changeEmailState,
            textPasswordState = textPasswordState,
            changePasswordState = changePasswordState
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
            onCLickGoogle = {
                onClickGoogleButton()
            }
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
            innerPadding = PaddingValues(),
            textFieldEmailState = "",
            textPasswordState = "",
            changeEmailState = {},
            changePasswordState = {},
            onClickGoogleButton = {}
        )
    }
}


