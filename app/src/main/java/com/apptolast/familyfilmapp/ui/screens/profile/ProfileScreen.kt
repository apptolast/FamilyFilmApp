package com.apptolast.familyfilmapp.ui.screens.profile

import android.widget.Toast
import coil.compose.AsyncImage
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.apptolast.familyfilmapp.R
import com.apptolast.familyfilmapp.navigation.Routes
import com.apptolast.familyfilmapp.ui.components.dialogs.DeleteAccountDialog
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.ui.sharedViewmodel.AuthState
import com.apptolast.familyfilmapp.ui.sharedViewmodel.AuthViewModel
import com.apptolast.familyfilmapp.ui.sharedViewmodel.UsernameValidationState
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import com.apptolast.familyfilmapp.utils.TT_PROFILE_AVATAR
import com.apptolast.familyfilmapp.utils.TT_PROFILE_DELETE_ACCOUNT
import com.apptolast.familyfilmapp.utils.TT_PROFILE_EMAIL
import com.apptolast.familyfilmapp.utils.TT_PROFILE_LOGOUT
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.GoogleAuthProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel(),
    onClickNav: (String) -> Unit = {},
    onBack: () -> Unit = {},
) {
    val context = LocalContext.current
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val provider by viewModel.provider.collectAsStateWithLifecycle()
    val usernameValidationState by profileViewModel.usernameValidationState.collectAsStateWithLifecycle()
    val isSaving by profileViewModel.isSaving.collectAsStateWithLifecycle()

    // State for showing the delete account dialog
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    val snackBarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        contentColor = MaterialTheme.colorScheme.background,
        modifier = modifier,
    ) { paddingValues ->

        when (authState) {
            is AuthState.Authenticated -> {
                Box(
                    modifier = Modifier
                        .consumeWindowInsets(paddingValues)
                        .fillMaxSize(),
                ) {
                    val user = (authState as AuthState.Authenticated).user
                    ProfileContent(
                        user = user,
                        usernameValidationState = usernameValidationState,
                        isSaving = isSaving,
                        onUsernameChange = profileViewModel::onUsernameChange,
                        onSaveUsername = { newUsername ->
                            profileViewModel.saveUsername(user, newUsername)
                        },
                        onCancelEditUsername = profileViewModel::resetValidationState,
                        onClickLogOut = { viewModel.logOut() },
                        onDeleteUser = {
                            // Show dialog only when the user has used email/pass provider
                            // Delete user straight away if user has used google provider
                            when (provider) {
                                GoogleAuthProvider.GOOGLE_SIGN_IN_METHOD -> viewModel.deleteUser()
                                EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD -> showDeleteDialog = true
                            }
                        },
                    )
                }

                // Show delete account dialog if state is true
                if (showDeleteDialog) {
                    DeleteAccountDialog(
                        onDismiss = { showDeleteDialog = false },
                        onConfirm = { email, password ->
                            viewModel.deleteUser(email, password)
                            showDeleteDialog = false
                        },
                    )
                }
            }

            is AuthState.Error -> {
                Toast.makeText(context, (authState as AuthState.Error).message, Toast.LENGTH_LONG).show()
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
                onClickNav(Routes.Login.routes)
            }
        }
    }
}

@Composable
fun ProfileContent(
    user: User,
    usernameValidationState: UsernameValidationState,
    isSaving: Boolean,
    modifier: Modifier = Modifier,
    onUsernameChange: (String) -> Unit = {},
    onSaveUsername: (String) -> Unit = {},
    onCancelEditUsername: () -> Unit = {},
    onClickLogOut: () -> Unit = {},
    onDeleteUser: () -> Unit = {},
) {
    var isEditingUsername by rememberSaveable { mutableStateOf(false) }
    var usernameEditValue by rememberSaveable { mutableStateOf(user.username.orEmpty()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Profile Avatar
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .testTag(TT_PROFILE_AVATAR),
            contentAlignment = Alignment.Center,
        ) {
            if (user.photoUrl.isNotBlank()) {
                AsyncImage(
                    model = user.photoUrl,
                    contentDescription = stringResource(R.string.profile_image_description),
                    placeholder = painterResource(id = R.drawable.profile_avatar),
                    error = painterResource(id = R.drawable.profile_avatar),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.profile_avatar),
                    contentDescription = stringResource(R.string.profile_image_description),
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // User Info
        Text(
            style = MaterialTheme.typography.titleMedium,
            text = user.email,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.testTag(TT_PROFILE_EMAIL),
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Username Section
        ProfileSection(title = stringResource(R.string.profile_section_username)) {
            if (isEditingUsername) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = usernameEditValue,
                        onValueChange = {
                            usernameEditValue = it.trim()
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
                                        text = usernameValidationState.reason,
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
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        TextButton(onClick = {
                            isEditingUsername = false
                            usernameEditValue = user.username.orEmpty()
                            onCancelEditUsername()
                        }) {
                            Text(text = stringResource(R.string.username_cancel))
                        }
                        Button(
                            onClick = {
                                onSaveUsername(usernameEditValue)
                                isEditingUsername = false
                            },
                            enabled = usernameValidationState is UsernameValidationState.Available &&
                                !isSaving,
                        ) {
                            if (isSaving) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp))
                            } else {
                                Text(text = stringResource(R.string.username_save))
                            }
                        }
                    }
                }
            } else {
                ProfileItem(
                    title = user.username?.let { "@$it" }
                        ?: stringResource(R.string.profile_set_username),
                    onClick = {
                        isEditingUsername = true
                        usernameEditValue = user.username.orEmpty()
                    },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(R.string.profile_edit_username),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Account Section
        ProfileSection(title = stringResource(R.string.account_title)) {
            // Log Out
            ProfileItem(
                title = stringResource(R.string.logout),
                modifier = Modifier.testTag(TT_PROFILE_LOGOUT),
                onClick = onClickLogOut,
                trailingContent = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Danger Zone - visually separated destructive action
        ProfileSection(title = stringResource(R.string.delete_account)) {
            ProfileItem(
                title = stringResource(R.string.delete_account),
                modifier = Modifier.testTag(TT_PROFILE_DELETE_ACCOUNT),
                titleColor = MaterialTheme.colorScheme.error,
                onClick = onDeleteUser,
                trailingContent = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                    )
                },
            )
        }
    }
}

@Composable
fun ProfileSection(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            ),
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
fun ProfileItem(
    title: String,
    modifier: Modifier = Modifier,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp)
            .let { if (onClick != null) it.clickable(onClick = onClick) else it }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = titleColor,
        )

        trailingContent?.invoke()
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    FamilyFilmAppTheme {
        ProfileContent(
            user = User(
                id = "1",
                email = "sophia.clark@gmail.com",
                language = "en",
                photoUrl = "",
                username = "sophia_clark",
            ),
            usernameValidationState = UsernameValidationState.Idle,
            isSaving = false,
        )
    }
}
