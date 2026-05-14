package com.apptolast.familyfilmapp.ui.screens.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.filters.MediumTest
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import com.apptolast.familyfilmapp.utils.TT_PROFILE_AVATAR
import com.apptolast.familyfilmapp.utils.TT_PROFILE_DELETE_ACCOUNT
import com.apptolast.familyfilmapp.utils.TT_PROFILE_EMAIL
import com.apptolast.familyfilmapp.utils.TT_PROFILE_LOGOUT
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@MediumTest
class ProfileContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

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
    fun profileContent_deleteAccountClick_invokesCallback() {
        var deleteInvoked = false
        setProfileContent(onDeleteAccount = { deleteInvoked = true })
        composeTestRule.onNodeWithTag(TT_PROFILE_DELETE_ACCOUNT).performClick()
        assertEquals(true, deleteInvoked)
    }

    private fun setProfileContent(
        user: User? = testUser,
        includeAdult: Boolean = false,
        hasChatPremium: Boolean = false,
        onIncludeAdultChange: (Boolean) -> Unit = {},
        onRemoveAds: () -> Unit = {},
        onChatPremium: () -> Unit = {},
        onRestorePurchases: () -> Unit = {},
        onRateApp: () -> Unit = {},
        onLogout: () -> Unit = {},
        onDeleteAccount: () -> Unit = {},
    ) {
        composeTestRule.setContent {
            FamilyFilmAppTheme {
                ProfileContent(
                    user = user,
                    includeAdult = includeAdult,
                    hasChatPremium = hasChatPremium,
                    onIncludeAdultChange = onIncludeAdultChange,
                    onRemoveAds = onRemoveAds,
                    onChatPremium = onChatPremium,
                    onRestorePurchases = onRestorePurchases,
                    onRateApp = onRateApp,
                    onLogout = onLogout,
                    onDeleteAccount = onDeleteAccount,
                )
            }
        }
    }
}
