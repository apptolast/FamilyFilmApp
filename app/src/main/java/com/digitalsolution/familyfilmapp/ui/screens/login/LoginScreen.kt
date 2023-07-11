package com.digitalsolution.familyfilmapp.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.digitalsolution.familyfilmapp.R
import com.digitalsolution.familyfilmapp.ui.theme.FamilyFilmAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen() {
    var textEmailState by remember { mutableStateOf("") }
    var textPasswordState by remember { mutableStateOf("") }
    val snackBarHostState = remember { SnackbarHostState() }
//    val scope = rememberCoroutineScope()

    Scaffold(snackbarHost = { SnackbarHost(snackBarHostState) }) { innerPadding ->
        LoginContent(
            innerPadding = innerPadding,
            textFieldEmailState = textEmailState,
            textPasswordState = textPasswordState,
            changeEmailState = { textEmailState = it },
            changePasswordState = { textPasswordState = it }
        )
    }
}

@Composable
fun LoginContent(
    innerPadding: PaddingValues,
    textFieldEmailState: String,
    textPasswordState: String,
    changeEmailState: (String) -> Unit,
    changePasswordState: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
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
            text = "Login",
            backgroundColor = MaterialTheme.colorScheme.tertiary,
            paddingVertical = 8.dp,
            textColor = MaterialTheme.colorScheme.surface
        )
        CardLoginsButton(
            text = "Sign in with Google",
            backgroundColor = MaterialTheme.colorScheme.background,
            paddingVertical = 0.dp,
            contentImage = {
                Image(
                    painter = painterResource(R.drawable.google_logo),
                    contentDescription = "Google Logo",
                    modifier = Modifier
                        .size(32.dp)
                        .padding(end = 4.dp)
                )
            }
        )
        Text(
            text = "Don't have an account? Sign up",
            modifier = Modifier.padding(6.dp),
            color = MaterialTheme.colorScheme.background
        )
        Text(text = "Forgot your password?", color = Color.Blue)
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
            changePasswordState = {}
        )
    }
}


