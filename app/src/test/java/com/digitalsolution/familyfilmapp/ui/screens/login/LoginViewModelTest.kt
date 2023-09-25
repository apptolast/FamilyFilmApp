package com.digitalsolution.familyfilmapp.ui.screens.login

import app.cash.turbine.test
import com.digitalsolution.familyfilmapp.MainDispatcherRule
import com.digitalsolution.familyfilmapp.model.local.UserData
import com.digitalsolution.familyfilmapp.ui.screens.login.uistates.LoginRegisterState
import com.digitalsolution.familyfilmapp.ui.screens.login.uistates.LoginUiState
import com.digitalsolution.familyfilmapp.ui.screens.login.usecases.CheckUserLoggedInUseCase
import com.digitalsolution.familyfilmapp.ui.screens.login.usecases.LoginEmailPassUseCase
import com.digitalsolution.familyfilmapp.ui.screens.login.usecases.LoginWithGoogleUseCase
import com.digitalsolution.familyfilmapp.ui.screens.login.usecases.RecoverPassUseCase
import com.digitalsolution.familyfilmapp.ui.screens.login.usecases.RegisterUseCase
import com.digitalsolution.familyfilmapp.utils.DispatcherProvider
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest {

    private lateinit var viewModel: LoginViewModel

    // Set the main coroutines dispatcher for unit testing.
    @get:Rule
    internal var coroutineRule = MainDispatcherRule()

    // Executes each task synchronously using Architecture Components.
//    @get:Rule
//    internal var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var loginEmailPassUseCase: LoginEmailPassUseCase

    @Mock
    private lateinit var loginWithGoogleUseCase: LoginWithGoogleUseCase

    @Mock
    private lateinit var checkUserLoggedInUseCase: CheckUserLoggedInUseCase

    @Mock
    private lateinit var registerUseCase: RegisterUseCase

    @Mock
    private lateinit var recoverPassUseCase: RecoverPassUseCase

    @Mock
    private lateinit var googleSignInClient: GoogleSignInClient

    @Before
    fun setUp() {
        viewModel = LoginViewModel(
            loginEmailPassUseCase,
            loginWithGoogleUseCase,
            checkUserLoggedInUseCase,
            registerUseCase,
            recoverPassUseCase,
            coroutineRule.testDispatcherProvider,
            googleSignInClient,
        )
    }

    @Test
    fun `LoginViewModel - Sign In User Pass provider - Success`() = runTest {
        // Arrange
        val email = "email"
        val password = "pass"

        whenever(loginEmailPassUseCase(any())).thenReturn(
            flow {
                LoginUiState().copy(
                    screenState = LoginRegisterState.Login(),
                    userData = UserData(
                        email = email,
                        pass = password
                    ),
                    isLogged = true,
                    isLoading = false
                )
            }
        )

        // Act
        viewModel.loginOrRegister(email, password)

        // Assert
        viewModel.state.test {

            awaitItem().let {
                assertThat(awaitItem().userData.email).isEqualTo("")
                assertThat(awaitItem().userData.pass).isEqualTo("")

            }
            awaitItem().let {
                assertThat(awaitItem().userData.email).isEqualTo(email)
                assertThat(awaitItem().userData.pass).isEqualTo(password)

            }

            awaitComplete()

        }

//        viewModel.state.getValueBlockedOrNull(coroutineRule.testDispatcherProvider)?.let { state ->
//            assertThat(state.userData.email).isEqualTo("")
//            assertThat(state.userData.pass).isEqualTo("")
//        }
    }

    @Test
    fun `LoginViewModel - Sign In User Pass provider - Exception`() {
        // Arrange

        // Act

        // Assert
    }

    @Test
    fun `LoginViewModel - Sign Up with Google provider - Success`() {
        // Arrange

        // Act
//        viewModel.handleGoogleSignInResult()

        // Assert

    }

    @Test
    fun `LoginViewModel - Sign Up with Google provider - Exception`() {
        // Arrange

        // Act

        // Assert
    }

}

fun <T> SharedFlow<T>.getValueBlockedOrNull(dispatcher: DispatcherProvider): T? {
    var value: T?
    runBlocking(dispatcher.io()) {
        value = when (this@getValueBlockedOrNull.replayCache.isEmpty()) {
            true -> null
            else -> this@getValueBlockedOrNull.firstOrNull()
        }
    }
    return value
}