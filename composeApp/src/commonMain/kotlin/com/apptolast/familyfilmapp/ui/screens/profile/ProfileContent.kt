package com.apptolast.familyfilmapp.ui.screens.profile

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
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.ui.components.dialogs.DeleteAccountDialog
import com.apptolast.familyfilmapp.ui.screens.profile.components.CountryPickerDialog
import com.apptolast.familyfilmapp.ui.sharedViewmodel.UsernameValidationState
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import com.apptolast.familyfilmapp.utils.TT_PROFILE_AVATAR
import com.apptolast.familyfilmapp.utils.TT_PROFILE_DELETE_ACCOUNT
import com.apptolast.familyfilmapp.utils.TT_PROFILE_EMAIL
import com.apptolast.familyfilmapp.utils.TT_PROFILE_LOGOUT
import com.apptolast.familyfilmapp.utils.countryCodeFromLanguageTag
import com.apptolast.familyfilmapp.utils.countryCodeToFlag
import com.apptolast.familyfilmapp.utils.getCountryDisplayName
import com.apptolast.familyfilmapp.utils.toErrorString
import familyfilmkmp.composeapp.generated.resources.Res
import familyfilmkmp.composeapp.generated.resources.account_title
import familyfilmkmp.composeapp.generated.resources.adult_content_subtitle
import familyfilmkmp.composeapp.generated.resources.adult_content_title
import familyfilmkmp.composeapp.generated.resources.chat_premium_active_subtitle
import familyfilmkmp.composeapp.generated.resources.chat_premium_active_title
import familyfilmkmp.composeapp.generated.resources.chat_premium_upsell_subtitle
import familyfilmkmp.composeapp.generated.resources.chat_premium_upsell_title
import familyfilmkmp.composeapp.generated.resources.delete_account
import familyfilmkmp.composeapp.generated.resources.logout
import familyfilmkmp.composeapp.generated.resources.profile_avatar
import familyfilmkmp.composeapp.generated.resources.profile_edit_username
import familyfilmkmp.composeapp.generated.resources.profile_image_description
import familyfilmkmp.composeapp.generated.resources.profile_section_user
import familyfilmkmp.composeapp.generated.resources.profile_set_username
import familyfilmkmp.composeapp.generated.resources.purchase_error
import familyfilmkmp.composeapp.generated.resources.purchase_loading
import familyfilmkmp.composeapp.generated.resources.purchase_success
import familyfilmkmp.composeapp.generated.resources.rate_app_subtitle
import familyfilmkmp.composeapp.generated.resources.rate_app_title
import familyfilmkmp.composeapp.generated.resources.region_edit
import familyfilmkmp.composeapp.generated.resources.restore_error
import familyfilmkmp.composeapp.generated.resources.restore_nothing_found
import familyfilmkmp.composeapp.generated.resources.restore_success
import familyfilmkmp.composeapp.generated.resources.subscription_remove_ads
import familyfilmkmp.composeapp.generated.resources.subscription_remove_ads_subtitle
import familyfilmkmp.composeapp.generated.resources.subscription_restore_purchases
import familyfilmkmp.composeapp.generated.resources.subscription_restore_purchases_subtitle
import familyfilmkmp.composeapp.generated.resources.subscription_section_title
import familyfilmkmp.composeapp.generated.resources.username_available
import familyfilmkmp.composeapp.generated.resources.username_cancel
import familyfilmkmp.composeapp.generated.resources.username_label
import familyfilmkmp.composeapp.generated.resources.username_save
import familyfilmkmp.composeapp.generated.resources.username_taken
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    user: User?,
    usernameValidationState: UsernameValidationState,
    isSaving: Boolean,
    isPurchaseLoading: Boolean,
    includeAdult: Boolean,
    hasRatedApp: Boolean,
    hasChatPremium: Boolean,
    purchaseEvents: Flow<PurchaseEvent>,
    onIncludeAdultChange: (Boolean) -> Unit,
    onUsernameChange: (String) -> Unit,
    onSaveUsername: (User, String) -> Unit,
    onCancelEditUsername: () -> Unit,
    onSaveLanguage: (User, String) -> Unit,
    onLogout: () -> Unit,
    onRemoveAds: () -> Unit,
    onChatPremium: () -> Unit,
    onRestorePurchases: () -> Unit,
    onRateApp: () -> Unit,
    onDeleteAccount: (email: String, password: String) -> Unit,
    onSubscriptionsManage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    val snackBarHostState = remember { SnackbarHostState() }

    val purchaseSuccessMsg = stringResource(Res.string.purchase_success)
    val purchaseErrorMsg = stringResource(Res.string.purchase_error)
    val restoreSuccessMsg = stringResource(Res.string.restore_success)
    val restoreNothingMsg = stringResource(Res.string.restore_nothing_found)
    val restoreErrorMsg = stringResource(Res.string.restore_error)

    LaunchedEffect(Unit) {
        purchaseEvents.collect { event ->
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
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        modifier = modifier,
    ) { paddingValues ->
        if (user != null) {
            Box(
                modifier = Modifier
                    .consumeWindowInsets(paddingValues)
                    .fillMaxSize(),
            ) {
                ProfileBody(
                    user = user,
                    usernameValidationState = usernameValidationState,
                    isSaving = isSaving,
                    includeAdult = includeAdult,
                    hasRatedApp = hasRatedApp,
                    hasChatPremium = hasChatPremium,
                    onIncludeAdultChange = onIncludeAdultChange,
                    onUsernameChange = onUsernameChange,
                    onSaveUsername = { newUsername -> onSaveUsername(user, newUsername) },
                    onCancelEditUsername = onCancelEditUsername,
                    onSaveLanguage = { languageTag -> onSaveLanguage(user, languageTag) },
                    onClickLogOut = onLogout,
                    onRemoveAds = onRemoveAds,
                    onChatPremium = {
                        if (!hasChatPremium) onChatPremium() else onSubscriptionsManage()
                    },
                    onRestorePurchase = onRestorePurchases,
                    onRateApp = onRateApp,
                    onDeleteUser = { showDeleteDialog = true },
                )
            }

            if (showDeleteDialog) {
                DeleteAccountDialog(
                    onDismiss = { showDeleteDialog = false },
                    onConfirm = { email, password ->
                        onDeleteAccount(email, password)
                        showDeleteDialog = false
                    },
                )
            }

            if (isPurchaseLoading) {
                PurchaseLoadingDialog()
            }
        }
    }
}

@Composable
private fun ProfileBody(
    user: User,
    usernameValidationState: UsernameValidationState,
    isSaving: Boolean,
    includeAdult: Boolean,
    hasRatedApp: Boolean,
    hasChatPremium: Boolean,
    onIncludeAdultChange: (Boolean) -> Unit,
    onUsernameChange: (String) -> Unit,
    onSaveUsername: (String) -> Unit,
    onCancelEditUsername: () -> Unit,
    onSaveLanguage: (String) -> Unit,
    onClickLogOut: () -> Unit,
    onRemoveAds: () -> Unit,
    onChatPremium: () -> Unit,
    onRestorePurchase: () -> Unit,
    onRateApp: () -> Unit,
    onDeleteUser: () -> Unit,
) {
    var isEditingUsername by rememberSaveable { mutableStateOf(false) }
    var usernameEditValue by rememberSaveable { mutableStateOf(user.username.orEmpty()) }
    var showCountryPicker by rememberSaveable { mutableStateOf(false) }

    val currentCountryCode = countryCodeFromLanguageTag(user.language)
    val currentFlag = countryCodeToFlag(currentCountryCode)
    val currentCountryName = getCountryDisplayName(currentCountryCode)
    val profilePhotoUrl = user.photoUrl.trim()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .testTag(TT_PROFILE_AVATAR),
            contentAlignment = Alignment.Center,
        ) {
            if (profilePhotoUrl.isNotBlank()) {
                AsyncImage(
                    model = profilePhotoUrl,
                    contentDescription = stringResource(Res.string.profile_image_description),
                    placeholder = painterResource(Res.drawable.profile_avatar),
                    error = painterResource(Res.drawable.profile_avatar),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                Image(
                    painter = painterResource(Res.drawable.profile_avatar),
                    contentDescription = stringResource(Res.string.profile_image_description),
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

        ProfileSection(title = stringResource(Res.string.profile_section_user)) {
            if (isEditingUsername) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = usernameEditValue,
                        onValueChange = {
                            usernameEditValue = it.trim()
                            onUsernameChange(it.trim())
                        },
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
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        TextButton(
                            onClick = {
                                isEditingUsername = false
                                usernameEditValue = user.username.orEmpty()
                                onCancelEditUsername()
                            },
                        ) {
                            Text(text = stringResource(Res.string.username_cancel))
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
                                Text(text = stringResource(Res.string.username_save))
                            }
                        }
                    }
                }
            } else {
                ProfileItem(
                    title = user.username ?: stringResource(Res.string.profile_set_username),
                    onClick = {
                        isEditingUsername = true
                        usernameEditValue = user.username.orEmpty()
                    },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(Res.string.profile_edit_username),
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
                        contentDescription = stringResource(Res.string.region_edit),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
            )

            HorizontalDivider()

            ProfileSwitchItem(
                title = stringResource(Res.string.adult_content_title),
                subtitle = stringResource(Res.string.adult_content_subtitle),
                checked = includeAdult,
                onCheckedChange = onIncludeAdultChange,
            )
        }

        if (showCountryPicker) {
            CountryPickerDialog(
                currentCountryCode = currentCountryCode,
                onSelectRegion = { region -> onSaveLanguage(region.languageTag) },
                onDismiss = { showCountryPicker = false },
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        ProfileSection(title = stringResource(Res.string.subscription_section_title)) {
            if (!user.hasRemovedAds) {
                ProfileItem(
                    title = stringResource(Res.string.subscription_remove_ads),
                    subtitle = stringResource(Res.string.subscription_remove_ads_subtitle),
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
                    stringResource(Res.string.chat_premium_active_title)
                } else {
                    stringResource(Res.string.chat_premium_upsell_title)
                },
                subtitle = if (hasChatPremium) {
                    stringResource(Res.string.chat_premium_active_subtitle)
                } else {
                    stringResource(Res.string.chat_premium_upsell_subtitle)
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
                title = stringResource(Res.string.subscription_restore_purchases),
                subtitle = stringResource(Res.string.subscription_restore_purchases_subtitle),
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
                    title = stringResource(Res.string.rate_app_title),
                    subtitle = stringResource(Res.string.rate_app_subtitle),
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

        ProfileSection(title = stringResource(Res.string.account_title)) {
            ProfileItem(
                title = stringResource(Res.string.logout),
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
                title = stringResource(Res.string.delete_account),
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
            Column { content() }
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
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                checkedBorderColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.surface,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                uncheckedBorderColor = MaterialTheme.colorScheme.outline,
            ),
        )
    }
}

@Composable
private fun PurchaseLoadingDialog() {
    Dialog(onDismissRequest = {}) {
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
                    text = stringResource(Res.string.purchase_loading),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewProfileContent() {
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
            isPurchaseLoading = false,
            includeAdult = false,
            hasRatedApp = false,
            hasChatPremium = false,
            purchaseEvents = kotlinx.coroutines.flow.emptyFlow(),
            onIncludeAdultChange = {},
            onUsernameChange = {},
            onSaveUsername = { _, _ -> },
            onCancelEditUsername = {},
            onSaveLanguage = { _, _ -> },
            onLogout = {},
            onRemoveAds = {},
            onChatPremium = {},
            onRestorePurchases = {},
            onRateApp = {},
            onDeleteAccount = { _, _ -> },
            onSubscriptionsManage = {},
        )
    }
}
