package com.apptolast.familyfilmapp.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import com.apptolast.familyfilmapp.utils.TT_PROFILE_AVATAR
import com.apptolast.familyfilmapp.utils.TT_PROFILE_DELETE_ACCOUNT
import com.apptolast.familyfilmapp.utils.TT_PROFILE_EMAIL
import com.apptolast.familyfilmapp.utils.TT_PROFILE_LOGOUT

@Composable
fun ProfileContent(
    user: User?,
    includeAdult: Boolean,
    hasChatPremium: Boolean,
    onIncludeAdultChange: (Boolean) -> Unit,
    onRemoveAds: () -> Unit,
    onChatPremium: () -> Unit,
    onRestorePurchases: () -> Unit,
    onRateApp: () -> Unit,
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (user != null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Card(
                    shape = CircleShape,
                    modifier = Modifier
                        .size(96.dp)
                        .testTag(TT_PROFILE_AVATAR),
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = user.displayName.take(1).uppercase().ifBlank { "?" },
                            style = MaterialTheme.typography.headlineMedium,
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(user.displayName, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .clip(CircleShape)
                        .testTag(TT_PROFILE_EMAIL),
                )
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Subscription", style = MaterialTheme.typography.titleSmall)
                Text("Chat premium: ${if (hasChatPremium) "active" else "inactive"}")
                OutlinedButton(onClick = onRemoveAds, modifier = Modifier.fillMaxWidth()) {
                    Text("Remove ads")
                }
                OutlinedButton(onClick = onChatPremium, modifier = Modifier.fillMaxWidth()) {
                    Text("Buy chat premium")
                }
                OutlinedButton(onClick = onRestorePurchases, modifier = Modifier.fillMaxWidth()) {
                    Text("Restore purchases")
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Preferences", style = MaterialTheme.typography.titleSmall)
                Row(checked = includeAdult, onCheckedChange = onIncludeAdultChange)
                OutlinedButton(onClick = onRateApp, modifier = Modifier.fillMaxWidth()) {
                    Text("Rate the app")
                }
            }
        }

        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(TT_PROFILE_LOGOUT),
        ) { Text("Sign out") }
        OutlinedButton(
            onClick = onDeleteAccount,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(TT_PROFILE_DELETE_ACCOUNT),
        ) { Text("Delete account") }
    }
}

@Composable
private fun Row(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("Include adult content")
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
@Preview
private fun PreviewProfileContent() {
    FamilyFilmAppTheme {
        ProfileContent(
            user = User(
                id = "u1",
                email = "demo@example.com",
                language = "en-US",
                photoUrl = "",
                username = "demo",
            ),
            includeAdult = false,
            hasChatPremium = true,
            onIncludeAdultChange = {},
            onRemoveAds = {},
            onChatPremium = {},
            onRestorePurchases = {},
            onRateApp = {},
            onLogout = {},
            onDeleteAccount = {},
        )
    }
}
