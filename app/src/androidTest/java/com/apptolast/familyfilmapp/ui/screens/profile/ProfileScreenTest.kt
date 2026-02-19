package com.apptolast.familyfilmapp.ui.screens.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.filters.MediumTest
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import com.apptolast.familyfilmapp.utils.TT_PROFILE_AVATAR
import com.apptolast.familyfilmapp.utils.TT_PROFILE_DELETE_ACCOUNT
import com.apptolast.familyfilmapp.utils.TT_PROFILE_EMAIL
import com.apptolast.familyfilmapp.utils.TT_PROFILE_LOGOUT
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test

@MediumTest
class ProfileScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setProfileContent(
        email: String = "sophia.clark@gmail.com",
        photoUrl: String = "",
        onClickLogOut: () -> Unit = {},
        onDeleteUser: () -> Unit = {},
    ) {
        composeTestRule.setContent {
            FamilyFilmAppTheme {
                ProfileContent(
                    email = email,
                    photoUrl = photoUrl,
                    onClickLogOut = onClickLogOut,
                    onDeleteUser = onDeleteUser,
                )
            }
        }
    }

    @Test
    fun profileContent_displaysAvatar() {
        setProfileContent()
        composeTestRule.onNodeWithTag(TT_PROFILE_AVATAR).assertIsDisplayed()
    }

    @Test
    fun profileContent_displaysEmail() {
        setProfileContent(email = "test@example.com")
        composeTestRule.onNodeWithTag(TT_PROFILE_EMAIL).assertIsDisplayed()
        composeTestRule.onNodeWithTag(TT_PROFILE_EMAIL).assertTextEquals("test@example.com")
    }

    @Test
    fun profileContent_displaysLogoutButton() {
        setProfileContent()
        composeTestRule.onNodeWithTag(TT_PROFILE_LOGOUT).assertIsDisplayed()
    }

    @Test
    fun profileContent_displaysDeleteAccountButton() {
        setProfileContent()
        composeTestRule.onNodeWithTag(TT_PROFILE_DELETE_ACCOUNT).assertIsDisplayed()
    }

    @Test
    fun profileContent_logoutCallsCallback() {
        var loggedOut = false
        setProfileContent(onClickLogOut = { loggedOut = true })
        composeTestRule.onNodeWithTag(TT_PROFILE_LOGOUT).performClick()
        assertThat(loggedOut).isTrue()
    }

    @Test
    fun profileContent_deleteAccountCallsCallback() {
        var deleteRequested = false
        setProfileContent(onDeleteUser = { deleteRequested = true })
        composeTestRule.onNodeWithTag(TT_PROFILE_DELETE_ACCOUNT).performClick()
        assertThat(deleteRequested).isTrue()
    }
}
