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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.apptolast.familyfilmapp.ui.components.dialogs.AlertRecoverPassDialog
import com.apptolast.familyfilmapp.ui.screens.login.components.AppleButtonContent
import com.apptolast.familyfilmapp.ui.screens.login.components.GoogleButtonContent
import com.apptolast.familyfilmapp.ui.screens.login.uistates.LoginRegisterState
import com.apptolast.familyfilmapp.ui.screens.login.uistates.RecoverPassState
import com.apptolast.familyfilmapp.ui.sharedViewmodel.AuthState
import com.apptolast.familyfilmapp.ui.sharedViewmodel.UsernameValidationState
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import com.apptolast.familyfilmapp.utils.TT_LOGIN_APPLE_BUTTON
import com.apptolast.familyfilmapp.utils.TT_LOGIN_BUTTON
import com.apptolast.familyfilmapp.utils.TT_LOGIN_EMAIL
import com.apptolast.familyfilmapp.utils.TT_LOGIN_GOOGLE_BUTTON
import com.apptolast.familyfilmapp.utils.TT_LOGIN_LOADING
import com.apptolast.familyfilmapp.utils.TT_LOGIN_PASS
import com.apptolast.familyfilmapp.utils.toErrorString
import familyfilmkmp.composeapp.generated.resources.Res
import familyfilmkmp.composeapp.generated.resources.app_name
import familyfilmkmp.composeapp.generated.resources.ic_launcher_foreground
import familyfilmkmp.composeapp.generated.resources.login_loading
import familyfilmkmp.composeapp.generated.resources.login_snail_logo
import familyfilmkmp.composeapp.generated.resources.login_text_app_subtitle
import familyfilmkmp.composeapp.generated.resources.login_text_check_your_email_to_verify_your_account
import familyfilmkmp.composeapp.generated.resources.login_text_email_sent
import familyfilmkmp.composeapp.generated.resources.login_text_field_email
import familyfilmkmp.composeapp.generated.resources.login_text_field_password
import familyfilmkmp.composeapp.generated.resources.login_text_forgot_your_password
import familyfilmkmp.composeapp.generated.resources.movie_background
import familyfilmkmp.composeapp.generated.resources.movie_background_10
import familyfilmkmp.composeapp.generated.resources.movie_background_11
import familyfilmkmp.composeapp.generated.resources.movie_background_12
import familyfilmkmp.composeapp.generated.resources.movie_background_2
import familyfilmkmp.composeapp.generated.resources.movie_background_3
import familyfilmkmp.composeapp.generated.resources.movie_background_4
import familyfilmkmp.composeapp.generated.resources.movie_background_5
import familyfilmkmp.composeapp.generated.resources.movie_background_6
import familyfilmkmp.composeapp.generated.resources.movie_background_7
import familyfilmkmp.composeapp.generated.resources.movie_background_8
import familyfilmkmp.composeapp.generated.resources.movie_background_9
import familyfilmkmp.composeapp.generated.resources.or_else
import familyfilmkmp.composeapp.generated.resources.username_available
import familyfilmkmp.composeapp.generated.resources.username_label
import familyfilmkmp.composeapp.generated.resources.username_taken
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginContent(
    initialEmail: String,
    initialPassword: String,
    username: String,
    screenState: LoginRegisterState,
    authState: AuthState,
    isEmailSent: Boolean,
    usernameValidationState: UsernameValidationState,
    recoverPassState: RecoverPassState,
    modifier: Modifier = Modifier,
    onUsernameChange: (String) -> Unit = {},
    onPrimaryClick: (email: String, password: String) -> Unit = { _, _ -> },
    onGoogleClick: () -> Unit = {},
    onAppleClick: () -> Unit = {},
    onToggleScreenState: () -> Unit = {},
    onRecoveryPassUpdate: (RecoverPassState) -> Unit = {},
    onRecoverPassword: (String) -> Unit = {},
    onClearError: () -> Unit = {},
) {
    var email by rememberSaveable(initialEmail) { mutableStateOf(initialEmail) }
    var password by rememberSaveable(initialPassword) { mutableStateOf(initialPassword) }
    val snackBarHostState = remember { SnackbarHostState() }
    var showLoginInterface by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    val isAuthLoading = authState is AuthState.Loading

    val imageList: List<DrawableResource> = listOf(
        Res.drawable.movie_background,
        Res.drawable.movie_background_2,
        Res.drawable.movie_background_3,
        Res.drawable.movie_background_4,
        Res.drawable.movie_background_5,
        Res.drawable.movie_background_6,
        Res.drawable.movie_background_7,
        Res.drawable.movie_background_8,
        Res.drawable.movie_background_9,
        Res.drawable.movie_background_10,
        Res.drawable.movie_background_11,
        Res.drawable.movie_background_12,
    )
    val randomImage = remember { imageList[Random.nextInt(imageList.size)] }

    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthState.Error -> {
                snackBarHostState.showSnackbar(state.message ?: "Error")
                onClearError()
            }

            AuthState.Unauthenticated -> {
                delay(700)
                showLoginInterface = true
            }

            is AuthState.Authenticated -> showLoginInterface = false
            AuthState.Loading -> showLoginInterface = false
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        // Background image must reach screen edges; only interactive content respects safe area.
        contentWindowInsets = WindowInsets(0),
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Image(
                painter = painterResource(randomImage),
                contentDescription = "Background Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.scrim.copy(alpha = 0.9f),
                                MaterialTheme.colorScheme.scrim.copy(alpha = 0.72f),
                                Color.Transparent,
                            ),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY,
                        ),
                    ),
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .safeDrawingPadding()
                    .imePadding()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 24.dp),
            ) {
                Image(
                    painter = painterResource(Res.drawable.ic_launcher_foreground),
                    contentDescription = stringResource(Res.string.login_snail_logo),
                    modifier = Modifier
                        .width(190.dp)
                        .padding(16.dp),
                )

                Text(
                    text = stringResource(Res.string.app_name),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 38.sp,
                    ),
                    modifier = Modifier.padding(bottom = 4.dp),
                )

                Text(
                    text = stringResource(Res.string.login_text_app_subtitle),
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                        TextField(
                            value = email,
                            onValueChange = { email = it.trim() },
                            label = { Text(text = stringResource(Res.string.login_text_field_email)) },
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

                        TextField(
                            value = password,
                            onValueChange = { password = it.trim() },
                            label = { Text(text = stringResource(Res.string.login_text_field_password)) },
                            singleLine = true,
                            visualTransformation = if (passwordVisible) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },
                            trailingIcon = {
                                val icon =
                                    if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
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

                        AnimatedVisibility(visible = screenState is LoginRegisterState.Register) {
                            Column {
                                Spacer(modifier = Modifier.height(12.dp))
                                TextField(
                                    value = username,
                                    onValueChange = { onUsernameChange(it.trim()) },
                                    label = { Text(text = stringResource(Res.string.username_label)) },
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
                                                    contentDescription = stringResource(Res.string.username_available),
                                                    tint = MaterialTheme.colorScheme.primary,
                                                )

                                            is UsernameValidationState.Taken ->
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = stringResource(Res.string.username_taken),
                                                    tint = MaterialTheme.colorScheme.error,
                                                )

                                            else -> {}
                                        }
                                    },
                                    supportingText = {
                                        when (usernameValidationState) {
                                            is UsernameValidationState.Taken ->
                                                Text(
                                                    text = stringResource(Res.string.username_taken),
                                                    color = MaterialTheme.colorScheme.error,
                                                )

                                            is UsernameValidationState.Invalid ->
                                                Text(
                                                    text = usernameValidationState.validationError.toErrorString(),
                                                    color = MaterialTheme.colorScheme.error,
                                                )

                                            is UsernameValidationState.Available ->
                                                Text(
                                                    text = stringResource(Res.string.username_available),
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

                        Button(
                            onClick = { onPrimaryClick(email, password) },
                            enabled = !isAuthLoading &&
                                (
                                    screenState is LoginRegisterState.Login ||
                                        usernameValidationState is UsernameValidationState.Available
                                    ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag(TT_LOGIN_BUTTON),
                            shape = MaterialTheme.shapes.medium,
                        ) {
                            Text(text = stringResource(screenState.buttonText))
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            HorizontalDivider(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 4.dp),
                            )
                            Text(
                                text = stringResource(Res.string.or_else),
                                modifier = Modifier.padding(horizontal = 8.dp),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                ),
                            )
                            HorizontalDivider(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 4.dp),
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = onGoogleClick,
                            enabled = !isAuthLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag(TT_LOGIN_GOOGLE_BUTTON),
                            shape = MaterialTheme.shapes.medium,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                contentColor = MaterialTheme.colorScheme.onSurface,
                            ),
                        ) {
                            GoogleButtonContent()
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = onAppleClick,
                            enabled = !isAuthLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag(TT_LOGIN_APPLE_BUTTON),
                            shape = MaterialTheme.shapes.medium,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                                contentColor = MaterialTheme.colorScheme.onSurface,
                            ),
                        ) {
                            AppleButtonContent()
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.clickable { onToggleScreenState() }) {
                            Text(
                                text = stringResource(screenState.accountText),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                ),
                                modifier = Modifier.padding(end = 4.dp),
                            )
                            Text(
                                text = stringResource(screenState.signText),
                                style = MaterialTheme.typography.bodyMedium
                                    .copy(
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold,
                                    ),
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))

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
                            text = stringResource(Res.string.login_text_forgot_your_password),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                        )

                        if (recoverPassState.isDialogVisible) {
                            AlertRecoverPassDialog(
                                onCLickSend = onRecoverPassword,
                                recoverPassState = recoverPassState,
                                dismissDialog = {
                                    onRecoveryPassUpdate(
                                        recoverPassState.copy(isDialogVisible = false),
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
                                        text = stringResource(Res.string.login_text_email_sent),
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                    )
                                },
                                text = {
                                    Text(stringResource(Res.string.login_text_check_your_email_to_verify_your_account))
                                },
                            )
                        }
                    }
                }
            }
        }
    }

    if (isAuthLoading) {
        LoginLoadingDialog()
    }
}

@Composable
private fun LoginLoadingDialog() {
    Dialog(onDismissRequest = { }) {
        Card(
            modifier = Modifier.testTag(TT_LOGIN_LOADING),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                Text(
                    text = stringResource(Res.string.login_loading),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewLoginContent() {
    FamilyFilmAppTheme {
        LoginContent(
            initialEmail = "email@something.com",
            initialPassword = "123456",
            username = "",
            screenState = LoginRegisterState.Login(),
            authState = AuthState.Unauthenticated,
            isEmailSent = false,
            usernameValidationState = UsernameValidationState.Idle,
            recoverPassState = RecoverPassState(),
        )
    }
}
