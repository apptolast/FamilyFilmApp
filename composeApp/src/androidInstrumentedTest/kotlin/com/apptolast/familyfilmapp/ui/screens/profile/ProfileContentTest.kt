package com.apptolast.familyfilmapp.ui.screens.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.filters.MediumTest
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.testing.createFamilyFilmComposeRule
import com.apptolast.familyfilmapp.ui.sharedViewmodel.UsernameValidationState
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import com.apptolast.familyfilmapp.utils.TT_DELETE_ACCOUNT_CONFIRM
import com.apptolast.familyfilmapp.utils.TT_DELETE_ACCOUNT_EMAIL
import com.apptolast.familyfilmapp.utils.TT_DELETE_ACCOUNT_PASSWORD
import com.apptolast.familyfilmapp.utils.TT_PROFILE_AVATAR
import com.apptolast.familyfilmapp.utils.TT_PROFILE_DELETE_ACCOUNT
import com.apptolast.familyfilmapp.utils.TT_PROFILE_EMAIL
import com.apptolast.familyfilmapp.utils.TT_PROFILE_LOGOUT
import kotlinx.coroutines.flow.emptyFlow
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@MediumTest
class ProfileContentTest {

    @get:Rule
    val composeTestRule = createFamilyFilmComposeRule()

    private val testUser = User(
        id = "u1",
        email = "demo@example.com",
        language = "en-US",
        photoUrl = "",
        username = "demo",
    )

    @Test
    fun profileContent_displaysAvatarAndEmail() {
        setProfileContent()
        composeTestRule.onNodeWithTag(TT_PROFILE_AVATAR).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TT_PROFILE_EMAIL).assertIsDisplayed()
    }

    @Test
    fun profileContent_logoutClick_invokesCallback() {
        var logoutInvoked = false
        setProfileContent(onLogout = { logoutInvoked = true })
        composeTestRule.onNodeWithTag(TT_PROFILE_LOGOUT).performClick()
        assertEquals(true, logoutInvoked)
    }

    @Test
    fun profileContent_deleteAccountDialogConfirm_invokesCallback() {
        var deleteInvoked = false
        setProfileContent(onDeleteAccount = { _, _ -> deleteInvoked = true })
        composeTestRule.onNodeWithTag(TT_PROFILE_DELETE_ACCOUNT).performClick()
        composeTestRule.onNodeWithTag(TT_DELETE_ACCOUNT_EMAIL).performTextInput("demo@example.com")
        composeTestRule.onNodeWithTag(TT_DELETE_ACCOUNT_PASSWORD).performTextInput("secret")
        composeTestRule.onNodeWithTag(TT_DELETE_ACCOUNT_CONFIRM).performClick()
        assertEquals(true, deleteInvoked)
    }

    private fun setProfileContent(
        user: User? = testUser,
        usernameValidationState: UsernameValidationState = UsernameValidationState.Idle,
        isSaving: Boolean = false,
        isPurchaseLoading: Boolean = false,
        hasRatedApp: Boolean = false,
        hasChatPremium: Boolean = false,
        onUsernameChange: (String) -> Unit = {},
        onSaveUsername: (User, String) -> Unit = { _, _ -> },
        onCancelEditUsername: () -> Unit = {},
        onSaveLanguage: (User, String) -> Unit = { _, _ -> },
        onRemoveAds: () -> Unit = {},
        onChatPremium: () -> Unit = {},
        onRestorePurchases: () -> Unit = {},
        onRateApp: () -> Unit = {},
        onLogout: () -> Unit = {},
        onDeleteAccount: (String, String) -> Unit = { _, _ -> },
        onSubscriptionsManage: () -> Unit = {},
    ) {
        composeTestRule.setContent {
            FamilyFilmAppTheme {
                ProfileContent(
                    user = user,
                    usernameValidationState = usernameValidationState,
                    isSaving = isSaving,
                    isPurchaseLoading = isPurchaseLoading,
                    hasRatedApp = hasRatedApp,
                    hasChatPremium = hasChatPremium,
                    purchaseEvents = emptyFlow(),
                    onUsernameChange = onUsernameChange,
                    onSaveUsername = onSaveUsername,
                    onCancelEditUsername = onCancelEditUsername,
                    onSaveLanguage = onSaveLanguage,
                    onRemoveAds = onRemoveAds,
                    onChatPremium = onChatPremium,
                    onRestorePurchases = onRestorePurchases,
                    onRateApp = onRateApp,
                    onLogout = onLogout,
                    onDeleteAccount = onDeleteAccount,
                    onSubscriptionsManage = onSubscriptionsManage,
                )
            }
        }
    }
}
