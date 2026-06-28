package com.apptolast.familyfilmapp.ui.screens.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.apptolast.familyfilmapp.ui.sharedViewmodel.AuthState
import com.apptolast.familyfilmapp.ui.sharedViewmodel.AuthViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel = koinViewModel(),
    profileViewModel: ProfileViewModel = koinViewModel(),
) {
    val authState by authViewModel.authState.collectAsState()
    val usernameValidationState by profileViewModel.usernameValidationState.collectAsState()
    val isSaving by profileViewModel.isSaving.collectAsState()
    val isPurchaseLoading by profileViewModel.isPurchaseLoading.collectAsState()
    val hasRatedApp by profileViewModel.hasRatedApp.collectAsState()
    val hasChatPremium by profileViewModel.hasChatPremium.collectAsState()
    val showChatPremiumPaywall by profileViewModel.showChatPremiumPaywall.collectAsState()
    val chatPremiumPricing by profileViewModel.chatPremiumPricing.collectAsState()

    val user = (authState as? AuthState.Authenticated)?.user

    ProfileContent(
        user = user,
        usernameValidationState = usernameValidationState,
        isSaving = isSaving,
        isPurchaseLoading = isPurchaseLoading,
        hasRatedApp = hasRatedApp,
        hasChatPremium = hasChatPremium,
        showChatPremiumPaywall = showChatPremiumPaywall,
        chatPremiumPricing = chatPremiumPricing,
        purchaseEvents = profileViewModel.purchaseEvent,
        onUsernameChange = profileViewModel::onUsernameChange,
        onSaveUsername = profileViewModel::saveUsername,
        onCancelEditUsername = profileViewModel::resetValidationState,
        onSaveLanguage = profileViewModel::saveLanguage,
        onLogout = authViewModel::logOut,
        onRemoveAds = profileViewModel::purchaseRemoveAds,
        onChatPremium = profileViewModel::showChatPremiumPaywall,
        onChatPremiumPaywallConfirm = profileViewModel::purchaseChatPremium,
        onChatPremiumPaywallDismiss = profileViewModel::dismissChatPremiumPaywall,
        onRestorePurchases = profileViewModel::restorePurchases,
        onRateApp = profileViewModel::markAppAsRated,
        onDeleteAccount = { email, password -> authViewModel.deleteUser(email, password) },
        onSubscriptionsManage = { /* Platform-specific Play Store deep link wired in androidMain */ },
    )
}
