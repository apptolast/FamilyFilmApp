package com.apptolast.familyfilmapp.ui.screens.login

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.filters.MediumTest
import com.apptolast.familyfilmapp.testing.createFamilyFilmComposeRule
import com.apptolast.familyfilmapp.ui.screens.login.uistates.LoginRegisterState
import com.apptolast.familyfilmapp.ui.screens.login.uistates.RecoverPassState
import com.apptolast.familyfilmapp.ui.sharedViewmodel.AuthState
import com.apptolast.familyfilmapp.ui.sharedViewmodel.UsernameValidationState
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import com.apptolast.familyfilmapp.utils.TT_LOGIN_BUTTON
import com.apptolast.familyfilmapp.utils.TT_LOGIN_EMAIL
import com.apptolast.familyfilmapp.utils.TT_LOGIN_GOOGLE_BUTTON
import com.apptolast.familyfilmapp.utils.TT_LOGIN_LOADING
import com.apptolast.familyfilmapp.utils.TT_LOGIN_PASS
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@MediumTest
class LoginContentTest {

    @get:Rule
    val composeTestRule = createFamilyFilmComposeRule()

    @Test
    fun loginContent_displaysEmailField() {
        setLoginContent()
        composeTestRule.onNodeWithTag(TT_LOGIN_EMAIL).assertIsDisplayed()
    }

    @Test
    fun loginContent_displaysPasswordField() {
        setLoginContent()
        composeTestRule.onNodeWithTag(TT_LOGIN_PASS).assertIsDisplayed()
    }

    @Test
    fun loginContent_displaysPrimaryButton() {
        setLoginContent()
        composeTestRule.onNodeWithTag(TT_LOGIN_BUTTON).assertIsDisplayed()
    }

    @Test
    fun loginContent_displaysGoogleButton() {
        setLoginContent()
        composeTestRule.onNodeWithTag(TT_LOGIN_GOOGLE_BUTTON).assertIsDisplayed()
    }

    @Test
    fun loginContent_loadingState_displaysBlockingProgressDialog() {
        setLoginContent(authState = AuthState.Loading)
        composeTestRule.onNodeWithTag(TT_LOGIN_LOADING).assertIsDisplayed()
    }

    @Test
    fun loginContent_primaryClick_invokesCallback() {
        var primaryInvoked = false
        setLoginContent(
            initialEmail = "user@example.com",
            initialPassword = "secret",
            onPrimaryClick = { _, _ -> primaryInvoked = true },
        )
        composeTestRule.onNodeWithTag(TT_LOGIN_BUTTON).performClick()
        assertEquals(true, primaryInvoked)
    }

    @Test
    fun loginContent_primaryClick_forwardsTypedEmail() {
        var captured = ""
        setLoginContent(onPrimaryClick = { email, _ -> captured = email })
        composeTestRule.onNodeWithTag(TT_LOGIN_EMAIL).performTextInput("hello@example.com")
        composeTestRule.onNodeWithTag(TT_LOGIN_BUTTON).performClick()
        assertEquals("hello@example.com", captured)
    }

    private fun setLoginContent(
        initialEmail: String = "",
        initialPassword: String = "",
        username: String = "",
        screenState: LoginRegisterState = LoginRegisterState.Login(),
        authState: AuthState = AuthState.Unauthenticated,
        isEmailSent: Boolean = false,
        usernameValidationState: UsernameValidationState = UsernameValidationState.Idle,
        recoverPassState: RecoverPassState = RecoverPassState(),
        onUsernameChange: (String) -> Unit = {},
        onPrimaryClick: (String, String) -> Unit = { _, _ -> },
        onGoogleClick: () -> Unit = {},
        onAppleClick: () -> Unit = {},
        onToggleScreenState: () -> Unit = {},
    ) {
        composeTestRule.setContent {
            FamilyFilmAppTheme {
                LoginContent(
                    initialEmail = initialEmail,
                    initialPassword = initialPassword,
                    username = username,
                    screenState = screenState,
                    authState = authState,
                    isEmailSent = isEmailSent,
                    usernameValidationState = usernameValidationState,
                    recoverPassState = recoverPassState,
                    onUsernameChange = onUsernameChange,
                    onPrimaryClick = onPrimaryClick,
                    onGoogleClick = onGoogleClick,
                    onAppleClick = onAppleClick,
                    onToggleScreenState = onToggleScreenState,
                )
            }
        }
        composeTestRule.mainClock.advanceTimeBy(1_200)
        composeTestRule.waitForIdle()
    }
}
