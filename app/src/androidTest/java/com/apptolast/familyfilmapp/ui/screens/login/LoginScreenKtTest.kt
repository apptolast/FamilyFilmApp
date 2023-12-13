package com.apptolast.familyfilmapp.ui.screens.login

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.apptolast.familyfilmapp.ui.screens.login.uistates.LoginUiState
import com.apptolast.familyfilmapp.ui.screens.login.uistates.RecoverPassUiState
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import com.apptolast.familyfilmapp.utils.Constants
import org.junit.Rule
import org.junit.Test

class LoginScreenKtTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `LoginContent-show_circular_progress_indicator`() {
        composeTestRule.setContent {
            FamilyFilmAppTheme {
                LoginContent(
                    loginUiState = LoginUiState().copy(isLoading = true),
                    recoverPassUIState = RecoverPassUiState(),
                    onClickLogin = { _, _ -> },
                    onCLickRecoverPassword = {},
                    onClickGoogleButton = {},
                    onClickScreenState = {},
                    onRecoveryPassUpdate = {},
                )
            }
        }

        composeTestRule.onNodeWithTag(Constants.CIRCULAR_PROGRESS_INDICATOR).assertIsDisplayed()
    }

    @Test
    fun `LoginContent-hide_circular_progress_indicator`() {
        composeTestRule.setContent {
            FamilyFilmAppTheme {
                LoginContent(
                    loginUiState = LoginUiState().copy(isLoading = false),
                    recoverPassUIState = RecoverPassUiState(),
                    onClickLogin = { _, _ -> },
                    onCLickRecoverPassword = {},
                    onClickGoogleButton = {},
                    onClickScreenState = {},
                    onRecoveryPassUpdate = {},
                )
            }
        }

        composeTestRule.onNodeWithTag(Constants.CIRCULAR_PROGRESS_INDICATOR).assertDoesNotExist()
    }
}
