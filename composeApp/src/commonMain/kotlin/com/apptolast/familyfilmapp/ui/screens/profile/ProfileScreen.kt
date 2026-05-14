package com.apptolast.familyfilmapp.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.apptolast.familyfilmapp.ui.sharedViewmodel.AuthState
import com.apptolast.familyfilmapp.ui.sharedViewmodel.AuthViewModel
import org.koin.compose.viewmodel.koinViewModel

/**
 * Stub Profile screen. Exposes the basic auth actions (sign out, delete
 * account) and toggles (include adult). The fully styled legacy version
 * (800 lines: country picker, paywall buttons, restore purchases, rate
 * app, username editor) is a post-migration polish task.
 */
@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel = koinViewModel(),
    profileViewModel: ProfileViewModel = koinViewModel(),
) {
    val authState by authViewModel.authState.collectAsState()
    val includeAdult by profileViewModel.includeAdult.collectAsState()
    val hasChatPremium by profileViewModel.hasChatPremium.collectAsState()

    val user = (authState as? AuthState.Authenticated)?.user

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = "Profile", style = MaterialTheme.typography.titleLarge)
        if (user != null) {
            Text(text = user.displayName, style = MaterialTheme.typography.titleMedium)
            Text(text = user.email, style = MaterialTheme.typography.bodyMedium)
        }
        Text(text = "Chat premium: $hasChatPremium")

        Column {
            Text("Include adult content")
            Switch(checked = includeAdult, onCheckedChange = { profileViewModel.saveIncludeAdult(it) })
        }

        OutlinedButton(onClick = { profileViewModel.purchaseRemoveAds() }) { Text("Remove ads") }
        OutlinedButton(onClick = { profileViewModel.purchaseChatPremium() }) { Text("Buy chat premium") }
        OutlinedButton(onClick = { profileViewModel.restorePurchases() }) { Text("Restore purchases") }
        OutlinedButton(onClick = { profileViewModel.markAppAsRated() }) { Text("Rate the app") }

        OutlinedButton(onClick = { authViewModel.logOut() }) { Text("Sign out") }
        OutlinedButton(onClick = { authViewModel.deleteUser() }) { Text("Delete account") }
    }
}
