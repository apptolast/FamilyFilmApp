package com.digitalsolution.familyfilmapp.ui.screens.login

import app.cash.turbine.test
import com.digitalsolution.familyfilmapp.MainDispatcherRule
import com.digitalsolution.familyfilmapp.model.local.User
import com.digitalsolution.familyfilmapp.ui.screens.login.uistates.LoginRegisterState
import com.digitalsolution.familyfilmapp.ui.screens.login.uistates.LoginUiState
import com.digitalsolution.familyfilmapp.ui.screens.login.usecases.CheckUserLoggedInUseCase
import com.digitalsolution.familyfilmapp.ui.screens.login.usecases.LoginEmailPassUseCase
import com.digitalsolution.familyfilmapp.ui.screens.login.usecases.LoginWithGoogleUseCase
import com.digitalsolution.familyfilmapp.ui.screens.login.usecases.RecoverPassUseCase
import com.digitalsolution.familyfilmapp.ui.screens.login.usecases.RegisterUseCase
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
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
            channelFlow {
                send(
                    LoginUiState().copy(
                        screenState = LoginRegisterState.Login(),
                        user = User(
                            email = email,
                            pass = password,
                        ),
                        isLogged = true,
                        isLoading = false,
                    ),
                )
                awaitClose()
            },
        )

        // Assert
        val job = launch {
            viewModel.state.test {
                awaitItem().let {
                    assertThat(email).isEqualTo(it.user.email)
                    assertThat(password).isEqualTo(it.user.pass)
                }
                cancelAndConsumeRemainingEvents()
            }
        }

        // Act
        viewModel.loginOrRegister(email, password)

        job.join()
        job.cancel()
    }

    @Test
    fun `LoginViewModel - Register User Pass provider - Success`() = runTest {
        // Arrange
        val email = "email"
        val password = "pass"
        viewModel.changeScreenState()

        whenever(registerUseCase(any())).thenReturn(
            channelFlow {
                send(
                    LoginUiState().copy(
                        screenState = LoginRegisterState.Register(),
                        user = User(
                            email = email,
                            pass = password,
                        ),
                        isLogged = true,
                        isLoading = false,
                    ),
                )
                awaitClose()
            },
        )

        // Assert
        val job = launch {
            viewModel.state.test {
                awaitItem().let {
                    assertThat(email).isEqualTo(it.user.email)
                    assertThat(password).isEqualTo(it.user.pass)
                }
                cancelAndConsumeRemainingEvents()
            }
        }

        // Act
        viewModel.loginOrRegister(email, password)

        job.join()
        job.cancel()
    }

    @Test
    fun `LoginViewModel - Login User Pass - Receive catch exception`() = runTest {
        // Arrange
        val email = "email"
        val password = "pass"
        val errorMessage = "Error"

        whenever(loginEmailPassUseCase(any())).thenReturn(
            channelFlow {
                throw Exception(errorMessage)
            },
        )

        // Assert
        val job = launch {
            viewModel.state.test {
                assertThat(awaitItem().errorMessage?.error).isEqualTo(errorMessage)
                cancelAndConsumeRemainingEvents()
            }
        }

        // Act
        viewModel.loginOrRegister(email, password)

        job.join()
        job.cancel()
    }

    @Test
    fun `LoginViewModel - change screen state between Login and Register`() = runTest {
        // Arrange

        // Assert
        val job = launch {
            viewModel.state.test {
                assertThat(awaitItem().screenState).isEqualTo(LoginRegisterState.Register())
                assertThat(awaitItem().screenState).isEqualTo(LoginRegisterState.Login())

                cancelAndConsumeRemainingEvents()
            }
        }

        // Act
        viewModel.changeScreenState()
        delay(100)
        viewModel.changeScreenState()

        job.join()
        job.cancel()
    }
}
