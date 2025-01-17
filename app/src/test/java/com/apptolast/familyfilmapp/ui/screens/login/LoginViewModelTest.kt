package com.apptolast.familyfilmapp.ui.screens.login

import app.cash.turbine.test
import com.apptolast.familyfilmapp.MainDispatcherRule
import com.apptolast.familyfilmapp.repositories.BackendRepository
import com.apptolast.familyfilmapp.repositories.FirebaseRepository
import com.apptolast.familyfilmapp.repositories.LocalRepository
import com.apptolast.familyfilmapp.ui.screens.login.uistates.LoginRegisterState
import com.apptolast.familyfilmapp.ui.screens.login.uistates.RecoverPassState
import com.apptolast.familyfilmapp.ui.screens.login.usecases.LoginWithGoogleUseCase
import com.apptolast.familyfilmapp.ui.screens.login.usecases.RecoverPassUseCase
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.common.truth.Truth.assertThat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest {

    private lateinit var viewModel: LoginViewModel

    @get:Rule
    internal var coroutineRule = MainDispatcherRule()

    @Mock
    private lateinit var loginWithGoogleUseCase: LoginWithGoogleUseCase

    @Mock
    private lateinit var recoverPassUseCase: RecoverPassUseCase

    @Mock
    private lateinit var backendRepository: BackendRepository

    @Mock
    private lateinit var firebaseRepository: FirebaseRepository

    @Mock
    private lateinit var localRepository: LocalRepository

    @Mock
    private lateinit var firebaseAuth: FirebaseAuth

    @Mock
    private lateinit var googleSignInClient: GoogleSignInClient

    @Mock
    private lateinit var firebaseUser: FirebaseUser

    @Before
    fun setUp() = runTest(coroutineRule.testDispatcherProvider.io()) {
        viewModel = LoginViewModel(
            backendRepository,
            firebaseRepository,
            localRepository,
            recoverPassUseCase,
            coroutineRule.testDispatcherProvider,
            googleSignInClient,
        )
    }

    @Test
    fun `LoginViewModel - Sign In User Pass provider - Success`() = runTest(coroutineRule.testDispatcherProvider.io()) {
        // Arrange
        val email = "email"
        val password = "pass"

        // Assert
//        val job = launch {
//            viewModel.loginState.test {
//                awaitItem().let {
//                    assertThat((it.user.email)).isEqualTo("")
//                    assertThat((it.user.pass)).isEqualTo("")
//                }
//                awaitItem().let {
//                    assertThat((it.user.email)).isEqualTo(email)
//                    assertThat((it.user.pass)).isEqualTo(password)
//                }
//                cancelAndConsumeRemainingEvents()
//            }
//        }
//
//        // Act
//        viewModel.loginOrRegister(email, password)
//
//        job.join()
//        job.cancel()
    }

    @Test
    fun `LoginViewModel - Register User Pass provider - Success`() =
        runTest(coroutineRule.testDispatcherProvider.io()) {
            // Arrange
            val email = "email"
            val password = "pass"

//            viewModel.changeScreenState()
//
//            // Assert
//            val job = launch {
//                viewModel.loginState.test {
//                    // println("----" + awaitItem().user.email)
//                    // println("----" + awaitItem().user.email)
//                    // println("----" + awaitItem().user.email)
//
//                    awaitItem().let {
//                        assertThat(it.user.email).isEqualTo("")
//                        assertThat(it.user.pass).isEqualTo("")
//                    }
//                    awaitItem().let {
//                        assertThat(it.user.email).isEqualTo("")
//                        assertThat(it.user.pass).isEqualTo("")
//                    }
//                    awaitItem().let {
//                        assertThat(it.user.email).isEqualTo(email)
//                        assertThat(it.user.pass).isEqualTo(password)
//                    }
//
//                    cancelAndConsumeRemainingEvents()
//                }
//            }
//
//            // Act
//            viewModel.loginOrRegister(email, password)
//
//            job.join()
//            job.cancel()
        }

    @Test
    fun `LoginViewModel - Login User Pass - Receive catch exception`() =
        runTest(coroutineRule.testDispatcherProvider.io()) {
            // Arrange
            val email = "email"
            val password = "pass"
            val errorMessage = "Error"

//            // Assert
//            val job = launch {
//                viewModel.loginState.test {
//                    assertThat(awaitItem().errorMessage?.error).isNull()
//                    assertThat(awaitItem().errorMessage?.error).isEqualTo(errorMessage)
//                    cancelAndConsumeRemainingEvents()
//                }
//            }
//
//            // Act
//            viewModel.loginOrRegister(email, password)
//
//            job.join()
        }

    @Test
    fun `LoginViewModel - Change screen state between Login and Register`() =
        runTest(coroutineRule.testDispatcherProvider.io()) {
            // Arrange

            // Assert
            val job = launch {
                viewModel.loginState.test {
                    assertThat(awaitItem().screenState).isEqualTo(LoginRegisterState.Login())
                    assertThat(awaitItem().screenState).isEqualTo(LoginRegisterState.Register())
                    assertThat(awaitItem().screenState).isEqualTo(LoginRegisterState.Login())

                    cancelAndConsumeRemainingEvents()
                }
            }

            // Act
            viewModel.changeScreenState()
            viewModel.changeScreenState()

            job.join()
            job.cancel()
        }

    @Test
    fun `LoginViewModel - Recovery Password - Success`() = runTest(coroutineRule.testDispatcherProvider.io()) {
        // Arrange
        val email = "email"
        val isDialogVisible = false

//        whenever(recoverPassUseCase(any())).thenReturn(
//            channelFlow {
//                send(
//                    RecoverPassUiState().copy(
//                        isDialogVisible = isDialogVisible,
//                        recoveryPassResponse = true,
//                        isLoading = false,
//                    ),
//                )
//                awaitClose()
//            },
//        )
//
//        // Assert
//        val job = launch {
//            viewModel.recoverPassUIState.test {
//                awaitItem().let {
//                    assertThat(it.isDialogVisible).isEqualTo(false)
//                    assertThat(it.isLoading).isFalse()
//                    assertThat(it.recoveryPassResponse).isFalse()
//                }
//                awaitItem().let {
//                    assertThat(it.isDialogVisible).isEqualTo(isDialogVisible)
//                    assertThat(it.isLoading).isFalse()
//                    assertThat(it.recoveryPassResponse).isTrue()
//                }
//
//                cancelAndConsumeRemainingEvents()
//            }
//        }
//
//        // Act
//        viewModel.recoverPassword(email)
//
//        job.join()
//        job.cancel()
    }

    @Test
    fun `LoginViewModel - Recovery Pass - Receive catch exception`() =
        runTest(coroutineRule.testDispatcherProvider.io()) {
            // Arrange
//            val errorMessage = "Error"
//
//            // Assert
//            val job = launch {
//                viewModel.recoverPassState.test {
//                    assertThat(awaitItem().errorMessage?.error).isNull()
//                    assertThat(awaitItem().errorMessage?.error).isEqualTo(errorMessage)
//
//                    cancelAndConsumeRemainingEvents()
//                }
//            }
//
//            // Act
//            viewModel.recoverPassword("email")
//
//            job.join()
        }

    @Test
    fun `LoginViewModel - updateRecoveryPasswordState - Successful`() =
        runTest(coroutineRule.testDispatcherProvider.io()) {
            // Arrange
            val recoveryPassUiState = RecoverPassState()

            // Assert
            val job = launch {
                viewModel.recoverPassState.test {
                    awaitItem().let { updateRecoveryPassUiState ->
                        assertThat(updateRecoveryPassUiState.isDialogVisible).isEqualTo(
                            recoveryPassUiState.isDialogVisible,
                        )
                        assertThat(updateRecoveryPassUiState.emailErrorMessage).isEqualTo(
                            recoveryPassUiState.emailErrorMessage,
                        )
                        assertThat(updateRecoveryPassUiState.recoveryPassResponse).isEqualTo(
                            recoveryPassUiState.recoveryPassResponse,
                        )
                        assertThat(updateRecoveryPassUiState.isLoading).isEqualTo(recoveryPassUiState.isLoading)
                        assertThat(updateRecoveryPassUiState.errorMessage).isEqualTo(recoveryPassUiState.errorMessage)
                    }

                    cancelAndConsumeRemainingEvents()
                }
            }

            // Act
//            viewModel.updateRecoveryPasswordState(recoveryPassUiState)

            job.join()
        }

    @Test
    fun `LoginViewModel - backendLogin Login - Successful`() = runTest(coroutineRule.testDispatcherProvider.io()) {
        // Arrange

//        val loginUiState = LoginUiState().copy(
//            isLogged = true,
//            screenState = LoginRegisterState.Login(),
//        )
//
//        whenever(backendRepository.login(any(), any())).thenReturn(
//            Result.success(LoginInfo()),
//        )
//
//        whenever(firebaseUser.email).thenReturn("email")
//        whenever(firebaseUser.uid).thenReturn("uid")
//
//        whenever(firebaseAuth.currentUser).thenReturn(firebaseUser)
//
//        // Assert
//        val job = launch {
//            viewModel.loginState.test {
//                awaitItem().let { newLoginUiState ->
//                    assertThat(LoginUiState()).isEqualTo(newLoginUiState)
//                }
//                awaitItem().let { newLoginUiState ->
//                    assertThat(loginUiState).isEqualTo(newLoginUiState)
//                }
//
//                cancelAndConsumeRemainingEvents()
//            }
//        }
//
//        // Act
//        viewModel.backendLogin(loginUiState)
//
//        job.join()
    }

    @Test
    fun `LoginViewModel - backendLogin Register - Successful`() = runTest(coroutineRule.testDispatcherProvider.io()) {
        // Arrange

//        val loginUiState = LoginUiState().copy(
//            isLogged = true,
//            screenState = LoginRegisterState.Register(),
//        )
//
//        whenever(backendRepository.register(any(), any())).thenReturn(
//            Result.success(LoginInfo()),
//        )
//
//        whenever(firebaseUser.email).thenReturn("email")
//        whenever(firebaseUser.uid).thenReturn("uid")
//
//        whenever(firebaseAuth.currentUser).thenReturn(firebaseUser)
//
//        // Assert
//        val job = launch {
//            viewModel.loginState.test {
//                awaitItem().let { newLoginUiState ->
//                    assertThat(LoginUiState()).isEqualTo(newLoginUiState)
//                }
//                awaitItem().let { newLoginUiState ->
//                    assertThat(loginUiState).isEqualTo(newLoginUiState)
//                }
//
//                cancelAndConsumeRemainingEvents()
//            }
//        }
//
//        // Act
//        viewModel.backendLogin(loginUiState)

//        job.join()
    }

    @Test
    fun `LoginViewModel - backendLogin Login Failure but Backend Repository - Successful`() =
        runTest(coroutineRule.testDispatcherProvider.io()) {
            // Arrange

//            val loginUiState = LoginUiState().copy(
//                isLogged = true,
//                screenState = LoginRegisterState.Login(),
//            )
//            val errorMessage = "Error"
//
//            whenever(backendRepository.login(any(), any())).thenReturn(
//                Result.failure(Exception(errorMessage)),
//            )
//
//            whenever(backendRepository.register(any(), any())).thenReturn(
//                Result.success(LoginInfo()),
//            )
//
//            whenever(firebaseUser.email).thenReturn("email")
//            whenever(firebaseUser.uid).thenReturn("uid")
//
//            whenever(firebaseAuth.currentUser).thenReturn(firebaseUser)
//
//            // Assert
//            val job = launch {
//                viewModel.loginState.test {
//                    awaitItem().let { newLoginUiState ->
//                        assertThat(LoginUiState()).isEqualTo(newLoginUiState)
//                    }
//                    awaitItem().let { newLoginUiState ->
//                        assertThat(loginUiState).isEqualTo(newLoginUiState)
//                    }
//
//                    cancelAndConsumeRemainingEvents()
//                }
//            }
//
//            // Act
//            viewModel.backendLogin(loginUiState)
//
//            job.join()
        }

    @Test
    fun `LoginViewModel - backendLogin Login Failure but Backend Repository - Fails`() =
        runTest(coroutineRule.testDispatcherProvider.io()) {
            // Arrange
//            val loginUiState = LoginUiState().copy(
//                isLogged = true,
//                screenState = LoginRegisterState.Login(),
//            )
//            val loginUiStateReturnError = LoginUiState().copy(
//                isLoading = false,
//                errorMessage = LoginException.BackendLogin(),
//            )
//            val errorMessage = "Error"
//
//            whenever(backendRepository.login(any(), any())).thenReturn(
//                Result.failure(Exception(errorMessage)),
//            )
//
//            whenever(backendRepository.register(any(), any())).thenReturn(
//                Result.failure(Exception(errorMessage)),
//            )
//
//            whenever(firebaseUser.email).thenReturn("email")
//            whenever(firebaseUser.uid).thenReturn("uid")
//
//            whenever(firebaseAuth.currentUser).thenReturn(firebaseUser)
//
//            // Assert
//            val job = launch {
//                viewModel.loginState.test {
//                    awaitItem().let { newLoginUiState ->
//                        assertThat(LoginUiState()).isEqualTo(newLoginUiState)
//                    }
//                    awaitItem().let { newLoginUiState ->
//                        assertThat(loginUiStateReturnError).isEqualTo(newLoginUiState)
//                    }
//
//                    cancelAndConsumeRemainingEvents()
//                }
//            }
//
//            // Act
// //            viewModel.backendLogin(loginUiState)
//
//            job.join()
        }
}
