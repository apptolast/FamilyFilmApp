package com.apptolast.familyfilmapp.ui.screens.login

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.filters.MediumTest
import com.apptolast.familyfilmapp.ui.screens.login.uistates.LoginRegisterState
import com.apptolast.familyfilmapp.ui.sharedViewmodel.AuthState
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import com.apptolast.familyfilmapp.utils.TT_LOGIN_BUTTON
import com.apptolast.familyfilmapp.utils.TT_LOGIN_EMAIL
import com.apptolast.familyfilmapp.utils.TT_LOGIN_GOOGLE_BUTTON
import com.apptolast.familyfilmapp.utils.TT_LOGIN_PASS
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@MediumTest
class LoginContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

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
    fun loginContent_primaryClick_invokesCallback() {
        var primaryInvoked = false
        setLoginContent(
            email = "user@example.com",
            password = "secret",
            onPrimaryClick = { primaryInvoked = true },
        )
        composeTestRule.onNodeWithTag(TT_LOGIN_BUTTON).performClick()
        assertEquals(true, primaryInvoked)
    }

    @Test
    fun loginContent_emailInput_propagates() {
        var captured = ""
        setLoginContent(onEmailChange = { captured = it })
        composeTestRule.onNodeWithTag(TT_LOGIN_EMAIL).performTextInput("hello@example.com")
        // The OutlinedTextField forwards each character; only the final captured value matters here.
        assertEquals("hello@example.com", captured)
    }

    private fun setLoginContent(
        email: String = "",
        password: String = "",
        username: String = "",
        screenState: LoginRegisterState = LoginRegisterState.Login(),
        authState: AuthState = AuthState.Unauthenticated,
        isEmailSent: Boolean = false,
        onEmailChange: (String) -> Unit = {},
        onPasswordChange: (String) -> Unit = {},
        onUsernameChange: (String) -> Unit = {},
        onPrimaryClick: () -> Unit = {},
        onGoogleClick: () -> Unit = {},
        onToggleScreenState: () -> Unit = {},
    ) {
        composeTestRule.setContent {
            FamilyFilmAppTheme {
                LoginContent(
                    email = email,
                    password = password,
                    username = username,
                    screenState = screenState,
                    authState = authState,
                    isEmailSent = isEmailSent,
                    onEmailChange = onEmailChange,
                    onPasswordChange = onPasswordChange,
                    onUsernameChange = onUsernameChange,
                    onPrimaryClick = onPrimaryClick,
                    onGoogleClick = onGoogleClick,
                    onToggleScreenState = onToggleScreenState,
                )
            }
        }
    }
}
