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
    val includeAdult by profileViewModel.includeAdult.collectAsState()
    val hasChatPremium by profileViewModel.hasChatPremium.collectAsState()

    val user = (authState as? AuthState.Authenticated)?.user

    ProfileContent(
        user = user,
        includeAdult = includeAdult,
        hasChatPremium = hasChatPremium,
        onIncludeAdultChange = profileViewModel::saveIncludeAdult,
        onRemoveAds = profileViewModel::purchaseRemoveAds,
        onChatPremium = profileViewModel::purchaseChatPremium,
        onRestorePurchases = profileViewModel::restorePurchases,
        onRateApp = profileViewModel::markAppAsRated,
        onLogout = authViewModel::logOut,
        onDeleteAccount = { authViewModel.deleteUser() },
    )
}
