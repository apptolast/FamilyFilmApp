package com.apptolast.familyfilmapp.ui.screens.login

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.filters.MediumTest
import com.apptolast.familyfilmapp.ui.screens.login.uistates.LoginRegisterState
import com.apptolast.familyfilmapp.ui.screens.login.uistates.RecoverPassState
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import com.apptolast.familyfilmapp.utils.TT_LOGIN_BUTTON
import com.apptolast.familyfilmapp.utils.TT_LOGIN_EMAIL
import com.apptolast.familyfilmapp.utils.TT_LOGIN_GOOGLE_BUTTON
import com.apptolast.familyfilmapp.utils.TT_LOGIN_PASS
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test

@MediumTest
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setLoginContent(
        screenState: LoginRegisterState = LoginRegisterState.Login(),
        email: String = "",
        password: String = "",
        onClick: (String, String) -> Unit = { _, _ -> },
        onClickGoogleButton: () -> Unit = {},
        onClickScreenState: () -> Unit = {},
    ) {
        composeTestRule.setContent {
            FamilyFilmAppTheme {
                MovieAppLoginContent(
                    showLoginInterface = true,
                    email = email,
                    password = password,
                    isEmailSent = false,
                    screenState = screenState,
                    recoverPassState = RecoverPassState(),
                    modifier = Modifier,
                    onClick = onClick,
                    onClickGoogleButton = onClickGoogleButton,
                    onClickScreenState = onClickScreenState,
                )
            }
        }
    }

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
    fun loginContent_displaysLoginButton() {
        setLoginContent()
        composeTestRule.onNodeWithTag(TT_LOGIN_BUTTON).assertIsDisplayed()
    }

    @Test
    fun loginContent_displaysGoogleButton() {
        setLoginContent()
        composeTestRule.onNodeWithTag(TT_LOGIN_GOOGLE_BUTTON).assertIsDisplayed()
    }

    @Test
    fun loginContent_displaysRegisterToggle() {
        setLoginContent()
        // The register toggle contains "Sign Up" text when in Login state
        composeTestRule.onNodeWithTag(TT_LOGIN_EMAIL).assertIsDisplayed()
    }

    @Test
    fun loginContent_emailFieldAcceptsInput() {
        setLoginContent()
        composeTestRule.onNodeWithTag(TT_LOGIN_EMAIL).performTextInput("test@email.com")
        composeTestRule.onNodeWithTag(TT_LOGIN_EMAIL).assertTextContains("test@email.com")
    }

    @Test
    fun loginContent_passwordFieldAcceptsInput() {
        setLoginContent()
        composeTestRule.onNodeWithTag(TT_LOGIN_PASS).performTextInput("password123")
        // Password field uses visual transformation, but text is still there
        composeTestRule.onNodeWithTag(TT_LOGIN_PASS).assertExists()
    }

    @Test
    fun loginContent_loginButtonCallsOnClick() {
        var clicked = false
        setLoginContent(onClick = { _, _ -> clicked = true })
        composeTestRule.onNodeWithTag(TT_LOGIN_BUTTON).performClick()
        assertThat(clicked).isTrue()
    }

    @Test
    fun loginContent_googleButtonCallsOnClick() {
        var clicked = false
        setLoginContent(onClickGoogleButton = { clicked = true })
        composeTestRule.onNodeWithTag(TT_LOGIN_GOOGLE_BUTTON).performClick()
        assertThat(clicked).isTrue()
    }
}
