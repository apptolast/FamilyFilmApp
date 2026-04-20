package com.apptolast.familyfilmapp.ui.screens.profile

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.google.android.play.core.review.ReviewException
import com.google.android.play.core.review.ReviewManagerFactory
import timber.log.Timber
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.apptolast.familyfilmapp.R
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.navigation.Routes
import com.apptolast.familyfilmapp.ui.components.dialogs.DeleteAccountDialog
import com.apptolast.familyfilmapp.ui.screens.profile.components.CountryPickerDialog
import com.apptolast.familyfilmapp.ui.sharedViewmodel.AuthState
import com.apptolast.familyfilmapp.ui.sharedViewmodel.AuthViewModel
import com.apptolast.familyfilmapp.ui.sharedViewmodel.UsernameValidationState
import com.apptolast.familyfilmapp.utils.countryCodeToFlag
import com.apptolast.familyfilmapp.utils.toErrorString
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import com.apptolast.familyfilmapp.utils.TT_PROFILE_AVATAR
import com.apptolast.familyfilmapp.utils.TT_PROFILE_DELETE_ACCOUNT
import com.apptolast.familyfilmapp.utils.TT_PROFILE_EMAIL
import com.apptolast.familyfilmapp.utils.TT_PROFILE_LOGOUT
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import java.util.Locale

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
    val isPurchaseLoading by profileViewModel.isPurchaseLoading.collectAsStateWithLifecycle()
    val includeAdult by profileViewModel.includeAdult.collectAsStateWithLifecycle()
    val hasRatedApp by profileViewModel.hasRatedApp.collectAsStateWithLifecycle()
    val hasChatPremium by profileViewModel.hasChatPremium.collectAsStateWithLifecycle()

    // State for showing the delete account dialog
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    val snackBarHostState = remember { SnackbarHostState() }

    // Resolve strings in composable scope for use in LaunchedEffect
    val purchaseSuccessMsg = stringResource(R.string.purchase_success)
    val purchaseErrorMsg = stringResource(R.string.purchase_error)
    val restoreSuccessMsg = stringResource(R.string.restore_success)
    val restoreNothingMsg = stringResource(R.string.restore_nothing_found)
    val restoreErrorMsg = stringResource(R.string.restore_error)

    // Collect purchase events and show snackbar messages
    LaunchedEffect(Unit) {
        profileViewModel.purchaseEvent.collect { event ->
            val message = when (event) {
                is PurchaseEvent.PurchaseSuccess -> purchaseSuccessMsg
                is PurchaseEvent.PurchaseError -> purchaseErrorMsg
                is PurchaseEvent.RestoreSuccess -> restoreSuccessMsg
                is PurchaseEvent.RestoreNothingFound -> restoreNothingMsg
                is PurchaseEvent.RestoreError -> restoreErrorMsg
            }
            snackBarHostState.showSnackbar(message)
        }
    }

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
                        includeAdult = includeAdult,
                        hasRatedApp = hasRatedApp,
                        hasChatPremium = hasChatPremium,
                        onIncludeAdultChange = profileViewModel::saveIncludeAdult,
                        onUsernameChange = profileViewModel::onUsernameChange,
                        onSaveUsername = { newUsername ->
                            profileViewModel.saveUsername(user, newUsername)
                        },
                        onCancelEditUsername = profileViewModel::resetValidationState,
                        onSaveLanguage = { languageTag ->
                            profileViewModel.saveLanguage(user, languageTag)
                        },
                        onClickLogOut = { viewModel.logOut() },
                        onRemoveAds = {
                            profileViewModel.purchaseRemoveAds(context as Activity)
                        },
                        onChatPremium = {
                            if (!hasChatPremium) {
                                profileViewModel.purchaseChatPremium(context as Activity)
                            } else {
                                openPlayStoreSubscriptions(context as Activity)
                            }
                        },
                        onRestorePurchase = { profileViewModel.restorePurchases() },
                        onRateApp = {
                            launchRateAppFlow(
                                activity = context as Activity,
                                onRated = { profileViewModel.markAppAsRated() },
                            )
                        },
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

                // Loading dialog while purchase/restore is in progress
                if (isPurchaseLoading) {
                    PurchaseLoadingDialog()
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
    includeAdult: Boolean,
    hasRatedApp: Boolean,
    hasChatPremium: Boolean,
    modifier: Modifier = Modifier,
    onIncludeAdultChange: (Boolean) -> Unit = {},
    onUsernameChange: (String) -> Unit = {},
    onSaveUsername: (String) -> Unit = {},
    onCancelEditUsername: () -> Unit = {},
    onSaveLanguage: (String) -> Unit = {},
    onClickLogOut: () -> Unit = {},
    onRemoveAds: () -> Unit = {},
    onChatPremium: () -> Unit = {},
    onRestorePurchase: () -> Unit = {},
    onRateApp: () -> Unit = {},
    onDeleteUser: () -> Unit = {},
) {
    var isEditingUsername by rememberSaveable { mutableStateOf(false) }
    var usernameEditValue by rememberSaveable { mutableStateOf(user.username.orEmpty()) }
    var showCountryPicker by rememberSaveable { mutableStateOf(false) }

    val currentCountryCode = user.language.substringAfter("-", Locale.getDefault().country)
    val currentFlag = countryCodeToFlag(currentCountryCode)
    val currentCountryName = Locale.Builder().setRegion(currentCountryCode).build()
        .getDisplayCountry(Locale.getDefault())

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

        Text(
            style = MaterialTheme.typography.titleMedium,
            text = user.email,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.testTag(TT_PROFILE_EMAIL),
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Section 1: User profile settings (username, region, adult content)
        ProfileSection(title = stringResource(R.string.profile_section_user)) {
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
                                        text = usernameValidationState.validationError.toErrorString(),
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
                    title = user.username ?: stringResource(R.string.profile_set_username),
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

            HorizontalDivider()

            ProfileItem(
                title = "$currentFlag $currentCountryName",
                onClick = { showCountryPicker = true },
                trailingContent = {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.region_edit),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
            )

            HorizontalDivider()

            ProfileSwitchItem(
                title = stringResource(R.string.adult_content_title),
                subtitle = stringResource(R.string.adult_content_subtitle),
                checked = includeAdult,
                onCheckedChange = onIncludeAdultChange,
            )
        }

        if (showCountryPicker) {
            CountryPickerDialog(
                currentCountryCode = currentCountryCode,
                onSelectRegion = { region ->
                    onSaveLanguage(region.languageTag)
                },
                onDismiss = { showCountryPicker = false },
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section 2: Payments & subscriptions
        ProfileSection(title = stringResource(R.string.subscription_section_title)) {
            if (!user.hasRemovedAds) {
                ProfileItem(
                    title = stringResource(R.string.subscription_remove_ads),
                    subtitle = stringResource(R.string.subscription_remove_ads_subtitle),
                    leadingIcon = Icons.Outlined.Block,
                    onClick = onRemoveAds,
                    trailingContent = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                )
                HorizontalDivider()
            }
            HorizontalDivider()
            ProfileItem(
                title = if (hasChatPremium) {
                    stringResource(R.string.chat_premium_active_title)
                } else {
                    stringResource(R.string.chat_premium_upsell_title)
                },
                subtitle = if (hasChatPremium) {
                    stringResource(R.string.chat_premium_active_subtitle)
                } else {
                    stringResource(R.string.chat_premium_upsell_subtitle)
                },
                leadingIcon = Icons.AutoMirrored.Outlined.Chat,
                onClick = onChatPremium,
                trailingContent = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
            )
            HorizontalDivider()
            ProfileItem(
                title = stringResource(R.string.subscription_restore_purchases),
                subtitle = stringResource(R.string.subscription_restore_purchases_subtitle),
                leadingIcon = Icons.Outlined.Restore,
                onClick = onRestorePurchase,
                trailingContent = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
            )
            if (!hasRatedApp) {
                HorizontalDivider()
                ProfileItem(
                    title = stringResource(R.string.rate_app_title),
                    subtitle = stringResource(R.string.rate_app_subtitle),
                    leadingIcon = Icons.Outlined.StarOutline,
                    onClick = onRateApp,
                    trailingContent = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section 3: Account actions (logout + delete)
        ProfileSection(title = stringResource(R.string.account_title)) {
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
            HorizontalDivider()
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

        Spacer(modifier = Modifier.height(32.dp))
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
    subtitle: String? = null,
    leadingIcon: ImageVector? = null,
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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f),
        ) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp),
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = titleColor,
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        trailingContent?.invoke()
    }
}

@Composable
fun ProfileSwitchItem(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp)
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}

@Composable
private fun PurchaseLoadingDialog() {
    androidx.compose.ui.window.Dialog(onDismissRequest = {}) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        ) {
            Row(
                modifier = Modifier.padding(24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                Text(
                    text = stringResource(R.string.purchase_loading),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
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
                language = "en-US",
                photoUrl = "",
                username = "sophia_clark",
            ),
            usernameValidationState = UsernameValidationState.Idle,
            isSaving = false,
            includeAdult = false,
            hasRatedApp = false,
            hasChatPremium = false,
        )
    }
}

/**
 * Launches the Google Play In-App Review flow.
 *
 * The Play Core API does not tell us whether the user actually rated (or whether
 * the dialog was even shown — Google silently skips it when quota is exhausted).
 * We therefore treat "flow completed" as "user has been given the chance to rate"
 * and mark the app as rated so the entry is hidden from Settings.
 *
 * If `requestReviewFlow()` fails (e.g. Play Store unavailable) we fall back to
 * opening the Play Store listing directly — this way the button never feels broken.
 */
private fun launchRateAppFlow(activity: Activity, onRated: () -> Unit) {
    val manager = ReviewManagerFactory.create(activity)
    manager.requestReviewFlow().addOnCompleteListener { request ->
        if (request.isSuccessful) {
            manager.launchReviewFlow(activity, request.result)
                .addOnCompleteListener {
                    Timber.d("In-app review flow finished")
                    onRated()
                }
        } else {
            val errorCode = (request.exception as? ReviewException)?.errorCode
            Timber.w(request.exception, "requestReviewFlow failed (code=$errorCode). Falling back to Play Store.")
            openPlayStoreListing(activity)
            onRated()
        }
    }
}

private fun openPlayStoreListing(context: Context) {
    val packageName = context.packageName
    val marketIntent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("market://details?id=$packageName"),
    ).apply {
        addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_NO_HISTORY or
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK,
        )
    }
    try {
        context.startActivity(marketIntent)
    } catch (e: ActivityNotFoundException) {
        Timber.w(e, "Play Store app not installed, opening web URL instead")
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$packageName"),
            ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) },
        )
    }
}

/**
 * Opens the Play Store subscription management screen for this app's `chat_premium_monthly` SKU.
 * Users can cancel or change payment method from there — we never handle that ourselves.
 */
private fun openPlayStoreSubscriptions(context: Context) {
    val packageName = context.packageName
    val url = "https://play.google.com/store/account/subscriptions" +
        "?sku=$CHAT_PREMIUM_SKU&package=$packageName"
    try {
        context.startActivity(
            Intent(Intent.ACTION_VIEW, Uri.parse(url))
                .apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) },
        )
    } catch (e: ActivityNotFoundException) {
        Timber.w(e, "Could not open Play Store subscriptions page")
    }
}

private const val CHAT_PREMIUM_SKU = "chat_premium_monthly"
