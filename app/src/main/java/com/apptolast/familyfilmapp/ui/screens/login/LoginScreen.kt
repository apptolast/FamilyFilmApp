package com.apptolast.familyfilmapp.ui.screens.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.apptolast.familyfilmapp.R
import com.apptolast.familyfilmapp.navigation.Routes
import com.apptolast.familyfilmapp.ui.components.dialogs.AlertRecoverPassDialog
import com.apptolast.familyfilmapp.ui.screens.login.uistates.LoginRegisterState
import com.apptolast.familyfilmapp.ui.screens.login.uistates.RecoverPassState
import com.apptolast.familyfilmapp.ui.sharedViewmodel.AuthState
import com.apptolast.familyfilmapp.ui.sharedViewmodel.AuthViewModel
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import kotlin.random.Random

@Composable
fun LoginScreen(navController: NavController, viewModel: AuthViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()
    val email by viewModel.email.collectAsStateWithLifecycle()
    val password by viewModel.password.collectAsStateWithLifecycle()
    val recoverPassState by viewModel.recoverPassState.collectAsStateWithLifecycle()

    Scaffold { innerPadding ->
        MovieAppLoginContent(
            email = email,
            password = password,
            screenState = screenState,
            recoverPassState = recoverPassState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            onClick = { email, pass ->
                when (screenState) {
                    is LoginRegisterState.Login -> viewModel.login(email, pass)
                    is LoginRegisterState.Register -> viewModel.register(email, pass)
                }
            },
            onClickScreenState = viewModel::changeScreenState,
            onRecoveryPassUpdate = viewModel::updateRecoveryPasswordState,
            onRecoverPassword = viewModel::recoverPassword,
        )

        when (authState) {
            is AuthState.Authenticated -> {
                navController.navigate(Routes.Home.routes) {
                    popUpTo(Routes.Login.routes) { inclusive = true }
                    launchSingleTop = true
                }
            }

            is AuthState.Error -> {
                Toast.makeText(context, (authState as AuthState.Error).message, Toast.LENGTH_LONG).show()
            }

            AuthState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 180.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            AuthState.Unauthenticated -> {
                /* no-op */
            }
        }
    }
}

@Composable
fun MovieAppLoginContent(
    email: String,
    password: String,
    screenState: LoginRegisterState,
    recoverPassState: RecoverPassState,
    modifier: Modifier = Modifier,
    onClick: (String, String) -> Unit = { _, _ -> },
    onClickScreenState: () -> Unit = {},
    onRecoveryPassUpdate: (RecoverPassState) -> Unit = {},
    onRecoverPassword: (String) -> Unit = {},
) {
    var email by rememberSaveable(key = email) { mutableStateOf(email) }
    var password by rememberSaveable(key = password) { mutableStateOf(password) }
    var passwordVisible by remember { mutableStateOf(false) }

    // Image List of drawable images
    val imageList = listOf(
        R.drawable.movie_background,
        R.drawable.movie_background_2,
        R.drawable.movie_background_3,
        R.drawable.movie_background_4,
        R.drawable.movie_background_5,
        R.drawable.movie_background_6,
        R.drawable.movie_background_7,
        R.drawable.movie_background_8,
        R.drawable.movie_background_9,
        R.drawable.movie_background_10,
        R.drawable.movie_background_11,
        R.drawable.movie_background_12,
    )

    // Select a random image at the start of the composition
    val randomImageId = remember { imageList[Random.nextInt(imageList.size)] }

    // Background Image with random movie theme
    Image(
        painter = painterResource(id = randomImageId),
        contentDescription = "Background Image",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize(),
    )

    // Gradient layer for better readability
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0x00000000), Color(0x66000000), Color(0xD2000000)),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY,
                ),
            ),
    )

    // Login content
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = modifier.padding(bottom = 36.dp),
    ) {
        // Logo
        Image(
            painter = painterResource(R.drawable.logo_film_family),
            contentDescription = stringResource(R.string.login_snail_logo),
            modifier = Modifier
                .width(130.dp)
                .padding(12.dp),
        )

        // Title
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineMedium.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                textAlign = TextAlign.Center,
            ),
            modifier = Modifier.padding(bottom = 8.dp),
        )

        // Additional description
        Text(
            text = stringResource(R.string.login_text_app_subtitle),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.LightGray,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
            ),
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
            modifier = Modifier.padding(bottom = 24.dp),
        )

        // Email Field
        TextField(
            value = email,
            onValueChange = { email = it.trim() },
            label = { Text(text = stringResource(R.string.login_text_field_email)) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = MaterialTheme.shapes.small.copy(
                bottomStart = CornerSize(0.dp),
                bottomEnd = CornerSize(0.dp),
            ),
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Password field
        TextField(
            value = password,
            onValueChange = { password = it.trim() },
            label = { Text(text = stringResource(R.string.login_text_field_password)) },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = icon,
                        contentDescription = "Toggle Password Visibility",
//                        tint = Color.White,
                    )
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = MaterialTheme.shapes.small.copy(
                bottomStart = CornerSize(0.dp),
                bottomEnd = CornerSize(0.dp),
            ),
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Login Button
        Button(
            onClick = { onClick(email, password) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = MaterialTheme.shapes.medium,
        ) {
            Text(
                text = stringResource(id = screenState.buttonText),
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Register text
        Row(
            modifier = Modifier
                .padding(6.dp)
                .clickable { onClickScreenState() },
        ) {
            Text(
                text = stringResource(screenState.accountText),
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White),

                modifier = Modifier.padding(end = 4.dp),
            )

            // TODO: Create Typography for this text.
            Text(
                text = stringResource(screenState.signText),
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                    .copy(fontWeight = FontWeight.Bold),
            )
        }
        // Actualiza el Text clickable para la recuperación de contraseña
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
            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
        )

        // Agrega el diálogo de recuperación de contraseña
        if (recoverPassState.isDialogVisible) {
            AlertRecoverPassDialog(
                onCLickSend = onRecoverPassword,
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
//    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    FamilyFilmAppTheme {
        MovieAppLoginContent(
            email = "email@something.com",
            password = "123456",
            screenState = LoginRegisterState.Login(),
            recoverPassState = RecoverPassState(),
            modifier = Modifier.fillMaxSize(),
        )
    }
}
