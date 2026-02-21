package com.apptolast.familyfilmapp.ui.screens.login

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.testTag
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
import com.apptolast.familyfilmapp.ui.screens.login.components.GoogleButtonContent
import com.apptolast.familyfilmapp.ui.screens.login.uistates.LoginRegisterState
import com.apptolast.familyfilmapp.ui.screens.login.uistates.RecoverPassState
import com.apptolast.familyfilmapp.ui.sharedViewmodel.AuthState
import com.apptolast.familyfilmapp.ui.sharedViewmodel.AuthViewModel
import com.apptolast.familyfilmapp.ui.sharedViewmodel.UsernameValidationState
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import com.apptolast.familyfilmapp.utils.TT_LOGIN_BUTTON
import com.apptolast.familyfilmapp.utils.TT_LOGIN_EMAIL
import com.apptolast.familyfilmapp.utils.TT_LOGIN_GOOGLE_BUTTON
import com.apptolast.familyfilmapp.utils.TT_LOGIN_PASS
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun LoginScreen(navController: NavController, viewModel: AuthViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()
    val email by viewModel.email.collectAsStateWithLifecycle()
    val password by viewModel.password.collectAsStateWithLifecycle()
    val usernameValue by viewModel.username.collectAsStateWithLifecycle()
    val usernameValidationState by viewModel.usernameValidationState.collectAsStateWithLifecycle()
    val isEmailSent by viewModel.isEmailSent.collectAsStateWithLifecycle()
    val recoverPassState by viewModel.recoverPassState.collectAsStateWithLifecycle()
    val shouldPromptForUsername by viewModel.shouldPromptForUsername.collectAsStateWithLifecycle()

    val snackBarHostState = remember { SnackbarHostState() }
    var showLoginInterface by remember { mutableStateOf(false) }

    Scaffold(snackbarHost = { SnackbarHost(hostState = snackBarHostState) }) { innerPadding ->
        MovieAppLoginContent(
            showLoginInterface = showLoginInterface,
            email = email,
            password = password,
            username = usernameValue,
            usernameValidationState = usernameValidationState,
            isEmailSent = isEmailSent,
            screenState = screenState,
            recoverPassState = recoverPassState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            onClick = { emailVal, passVal, usernameVal ->
                when (screenState) {
                    is LoginRegisterState.Login -> viewModel.login(emailVal, passVal)

                    is LoginRegisterState.Register -> viewModel.registerAndSendEmail(
                        emailVal,
                        passVal,
                        usernameVal,
                    )
                }
            },
            onUsernameChange = viewModel::onUsernameChange,
            onClickGoogleButton = { viewModel.googleSignIn(context) },
            onClickScreenState = viewModel::changeScreenState,
            onRecoveryPassUpdate = viewModel::updateRecoveryPasswordState,
            onRecoverPassword = viewModel::recoverPassword,
        )

        // Username setup dialog for new Google users
        if (shouldPromptForUsername) {
            com.apptolast.familyfilmapp.ui.screens.login.components.UsernameSetupDialog(
                usernameValidationState = usernameValidationState,
                onUsernameChange = viewModel::onUsernameChange,
                onConfirm = viewModel::saveUsernameForNewUser,
                onSkip = viewModel::skipUsernameSetup,
            )
        }

        when (authState) {
            is AuthState.Authenticated -> {
                LaunchedEffect(true) {
                    showLoginInterface = false
                    delay(500)
                    navController.navigate(Routes.Home.routes) {
                        popUpTo(Routes.Login.routes) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }

            is AuthState.Error -> {
                val errorMessage = (authState as AuthState.Error).message
                LaunchedEffect(errorMessage) {
                    snackBarHostState.showSnackbar(errorMessage ?: "Error")
                    viewModel.clearFailure()
                }
            }

            AuthState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            AuthState.Unauthenticated -> {
                /* no-op */
                LaunchedEffect(true) {
                    delay(700)
                    showLoginInterface = authState is AuthState.Unauthenticated
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieAppLoginContent(
    showLoginInterface: Boolean,
    email: String,
    password: String,
    username: String,
    usernameValidationState: UsernameValidationState,
    isEmailSent: Boolean,
    screenState: LoginRegisterState,
    recoverPassState: RecoverPassState,
    modifier: Modifier = Modifier,
    onClick: (String, String, String) -> Unit = { _, _, _ -> },
    onUsernameChange: (String) -> Unit = {},
    onClickGoogleButton: () -> Unit = {},
    onClickScreenState: () -> Unit = {},
    onRecoveryPassUpdate: (RecoverPassState) -> Unit = {},
    onRecoverPassword: (String) -> Unit = {},
) {
    var email by rememberSaveable(key = email) { mutableStateOf(email) }
    var password by rememberSaveable(key = password) { mutableStateOf(password) }
    var localUsername by rememberSaveable(key = username) { mutableStateOf(username) }
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
                    colors = listOf(Color(0xDD000000), Color(0x9F000000), Color(0x00000000)),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY,
                ),
            ),
    )

    // Login content
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .imePadding() // Ajusta el espacio cuando aparece el teclado,
            .padding(horizontal = 16.dp)
            .padding(bottom = 24.dp),
    ) {
        // Logo
        Image(
            painter = painterResource(R.drawable.logo_film_family),
            contentDescription = stringResource(R.string.login_snail_logo),
            modifier = Modifier
                .width(130.dp)
                .padding(16.dp),
        )

        // Title
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineLarge.copy(color = Color.White, fontSize = 38.sp),
            modifier = Modifier.padding(bottom = 4.dp),
        )

        // Additional description
        Text(
            text = stringResource(R.string.login_text_app_subtitle),
            style = MaterialTheme.typography.titleLarge.copy(
                color = Color.LightGray,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
            ),
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
            modifier = Modifier.padding(bottom = 24.dp),
        )

        AnimatedVisibility(showLoginInterface) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                // Email Field
                TextField(
                    value = email,
                    onValueChange = { email = it.trim() },
                    label = { Text(text = stringResource(R.string.login_text_field_email)) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(TT_LOGIN_EMAIL),
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
                    visualTransformation = if (passwordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = icon,
                                contentDescription = "Toggle Password Visibility",
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(TT_LOGIN_PASS),
                    shape = MaterialTheme.shapes.small.copy(
                        bottomStart = CornerSize(0.dp),
                        bottomEnd = CornerSize(0.dp),
                    ),
                )

                // Username field - only visible in Register mode
                AnimatedVisibility(visible = screenState is LoginRegisterState.Register) {
                    Column {
                        Spacer(modifier = Modifier.height(12.dp))
                        TextField(
                            value = localUsername,
                            onValueChange = {
                                localUsername = it.trim()
                                onUsernameChange(it.trim())
                            },
                            label = { Text(text = stringResource(R.string.username_label)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            isError = usernameValidationState is UsernameValidationState.Taken ||
                                usernameValidationState is UsernameValidationState.Invalid,
                            trailingIcon = {
                                when (usernameValidationState) {
                                    is UsernameValidationState.Checking ->
                                        CircularProgressIndicator(modifier = Modifier.size(20.dp))

                                    is UsernameValidationState.Available ->
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = stringResource(R.string.username_available),
                                            tint = MaterialTheme.colorScheme.primary,
                                        )

                                    is UsernameValidationState.Taken ->
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = stringResource(R.string.username_taken),
                                            tint = MaterialTheme.colorScheme.error,
                                        )

                                    else -> {}
                                }
                            },
                            supportingText = {
                                when (usernameValidationState) {
                                    is UsernameValidationState.Taken ->
                                        Text(
                                            text = stringResource(R.string.username_taken),
                                            color = MaterialTheme.colorScheme.error,
                                        )

                                    is UsernameValidationState.Invalid ->
                                        Text(
                                            text = (usernameValidationState as UsernameValidationState.Invalid).reason,
                                            color = MaterialTheme.colorScheme.error,
                                        )

                                    is UsernameValidationState.Available ->
                                        Text(
                                            text = stringResource(R.string.username_available),
                                            color = MaterialTheme.colorScheme.primary,
                                        )

                                    else -> {}
                                }
                            },
                            shape = MaterialTheme.shapes.small.copy(
                                bottomStart = CornerSize(0.dp),
                                bottomEnd = CornerSize(0.dp),
                            ),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Login/Register Button
                Button(
                    onClick = { onClick(email, password, localUsername) },
                    enabled = screenState is LoginRegisterState.Login ||
                        usernameValidationState is UsernameValidationState.Available,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag(TT_LOGIN_BUTTON),
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Text(text = stringResource(id = screenState.buttonText))
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    HorizontalDivider(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp),
                    )
                    Text(
                        text = stringResource(R.string.or_else),
                        modifier = Modifier.padding(horizontal = 8.dp),
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
                    )
                    HorizontalDivider(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp),
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onClickGoogleButton,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag(TT_LOGIN_GOOGLE_BUTTON),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.surface),
                ) {
                    Surface {
                        GoogleButtonContent()
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Register text
                Row(modifier = Modifier.clickable { onClickScreenState() }) {
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
                Spacer(modifier = Modifier.height(8.dp))

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

                AnimatedVisibility(isEmailSent) {
                    AlertDialog(
                        onDismissRequest = { /* Handle dismiss if needed */ },
                        confirmButton = { /* Handle dismiss if needed */ },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = Icons.Default.Check.toString(),
                            )
                        },
                        title = {
                            Text(
                                text = stringResource(R.string.login_text_email_sent),
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                            )
                        },
                        text = { Text(stringResource(R.string.login_text_check_your_email_to_verify_your_account)) },
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    FamilyFilmAppTheme {
        MovieAppLoginContent(
            showLoginInterface = true,
            email = "email@something.com",
            password = "123456",
            username = "",
            usernameValidationState = UsernameValidationState.Idle,
            isEmailSent = true,
            screenState = LoginRegisterState.Login(),
            recoverPassState = RecoverPassState(),
            modifier = Modifier.fillMaxSize(),
        )
    }
}
