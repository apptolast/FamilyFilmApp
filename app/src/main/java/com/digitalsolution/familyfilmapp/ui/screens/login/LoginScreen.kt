package com.digitalsolution.familyfilmapp.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.digitalsolution.familyfilmapp.R
import com.digitalsolution.familyfilmapp.getGoogleFontFamily
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen() {
    var textEmailState by remember { mutableStateOf("") }
    var textPasswordState by remember { mutableStateOf("") }
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(snackbarHost = { SnackbarHost(snackBarHostState) }) { innerPadding ->
        LoginContent(
            innerPadding = innerPadding,
            textFieldEmailState = textEmailState,
            textPasswordState = textPasswordState,
            changeEmailState = { textEmailState = it },
            changePasswordState = { textPasswordState = it }
        ) {
            scope.launch {
                snackBarHostState.showSnackbar(
                    message = "Implementar lógica de login",
                    actionLabel = "Ok, _rndev ;)"
                )
            }
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
    showSnackBar: () -> Job
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
            showSnackBar = showSnackBar,
            textColor = MaterialTheme.colorScheme.surface
        )
        CardLoginsButton(
            text = "Sign in with Google",
            backgroundColor = MaterialTheme.colorScheme.background,
            paddingVertical = 0.dp,
            showSnackBar = showSnackBar,
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

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun CardLoginMain(
    textFieldEmailState: String,
    changeEmailState: (String) -> Unit,
    textPasswordState: String,
    changePasswordState: (String) -> Unit
) {
    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.logo_film_family),
                contentDescription = "Snail Logo",
                modifier = Modifier
                    .width(134.dp)
                    .padding(8.dp)
            )
            Text(
                text = "Film Family",
                color = MaterialTheme.colorScheme.background,
                textAlign = TextAlign.Center,
                fontFamily = "Alfa Slab One".getGoogleFontFamily(),
                fontSize = 36.sp
            )
            Text(
                text = "Shared your films with your family and friends",
                fontFamily = "Anton".getGoogleFontFamily(),
                textAlign = TextAlign.Center,
                fontSize = 14.sp
            )
            OutlinedTextField(
                value = textFieldEmailState,
                onValueChange = changeEmailState,
                modifier = Modifier.padding(vertical = 4.dp),
                label = {
                    Text(
                        text = "Enter your email",
                        color = Color.Gray
                    )
                }
            )
            OutlinedTextField(
                value = textPasswordState,
                onValueChange = changePasswordState,
                modifier = Modifier.padding(vertical = 4.dp),
                label = {
                    Text(
                        text = "Enter your password",
                        color = Color.Gray
                    )
                }
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun CardLoginsButton(
    text: String,
    backgroundColor: Color,
    paddingVertical: Dp,
    showSnackBar: () -> Job,
    textColor: Color = Color.Unspecified,
    contentImage: @Composable () -> Unit = {}
) {
    Card(
        onClick = { showSnackBar() },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = paddingVertical)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundColor)
                .padding(10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            contentImage()
            Text(text = text, color = textColor)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
//    FamilyFilmAppTheme {
//        LoginContent(
//            textFieldEmailState = "",
//            textPasswordState = "",
//            changeEmailState = {},
//            changePasswordState = {},
//            showSnackBar = {}
//        )
//    }
}


