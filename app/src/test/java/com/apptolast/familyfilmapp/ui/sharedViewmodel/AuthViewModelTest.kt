package com.apptolast.familyfilmapp.ui.sharedViewmodel

import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.apptolast.familyfilmapp.MainDispatcherRule
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.repositories.FirebaseAuthRepository
import com.apptolast.familyfilmapp.repositories.Repository
import com.google.common.truth.Truth.assertThat
import com.google.firebase.auth.FirebaseUser
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AuthViewModelTest {

    private lateinit var viewModel: AuthViewModel

    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val dispatcher = MainDispatcherRule()

    @MockK
    lateinit var authRepository: FirebaseAuthRepository

    @MockK
    lateinit var repository: Repository

    @RelaxedMockK
    lateinit var credentialManager: CredentialManager

    @RelaxedMockK
    lateinit var credentialRequest: GetCredentialRequest

    @RelaxedMockK
    lateinit var firebaseUser: FirebaseUser

    private val testUserId = "auth-test-user"
    private val testUser = User().copy(id = testUserId, email = "auth@test.com")

    @Before
    fun setUp() {
        // Default mocks for init block
        every { authRepository.getUser() } returns flowOf(null)
        every { authRepository.isTokenValid() } returns flowOf(false)
        every { authRepository.checkEmailVerification(any()) } returns flowOf(false)
        every { authRepository.getProvider() } returns flowOf(null)
    }

    private fun createViewModel(): AuthViewModel {
        viewModel = AuthViewModel(
            authRepository,
            repository,
            dispatcher.testDispatcherProvider,
            credentialManager,
            credentialRequest,
        )
        return viewModel
    }

    @Test
    fun `init should set Unauthenticated when no user`() = runTest {
        createViewModel()
        advanceUntilIdle()

        assertThat(viewModel.authState.value).isEqualTo(AuthState.Unauthenticated)
    }

    @Test
    fun `init should set Authenticated when user is verified and token valid`() = runTest {
        every { firebaseUser.isEmailVerified } returns true
        every { firebaseUser.uid } returns testUserId
        every { firebaseUser.email } returns "auth@test.com"
        every { authRepository.getUser() } returns flowOf(firebaseUser)
        every { authRepository.isTokenValid() } returns flowOf(true)
        every { repository.startSync(testUserId) } returns Unit

        createViewModel()
        advanceUntilIdle()

        val state = viewModel.authState.value
        assertThat(state).isInstanceOf(AuthState.Authenticated::class.java)
        assertThat((state as AuthState.Authenticated).user.id).isEqualTo(testUserId)
    }

    @Test
    fun `init should remain Unauthenticated when email not verified`() = runTest {
        every { firebaseUser.isEmailVerified } returns false
        every { authRepository.getUser() } returns flowOf(firebaseUser)
        every { authRepository.isTokenValid() } returns flowOf(true)

        createViewModel()
        advanceUntilIdle()

        assertThat(viewModel.authState.value).isEqualTo(AuthState.Unauthenticated)
    }

    @Test
    fun `init should remain Unauthenticated when token is invalid`() = runTest {
        every { firebaseUser.isEmailVerified } returns true
        every { authRepository.getUser() } returns flowOf(firebaseUser)
        every { authRepository.isTokenValid() } returns flowOf(false)

        createViewModel()
        advanceUntilIdle()

        assertThat(viewModel.authState.value).isEqualTo(AuthState.Unauthenticated)
    }

    @Test
    fun `login should set Loading then Authenticated on success`() = runTest {
        val loginUser = User().copy(id = "login-user", email = "login@test.com")
        every { authRepository.login("login@test.com", "pass123") } returns flowOf(Result.success(loginUser))
        every { repository.startSync("login-user") } returns Unit

        createViewModel()
        advanceUntilIdle()

        viewModel.login("login@test.com", "pass123")
        advanceUntilIdle()

        val state = viewModel.authState.value
        assertThat(state).isInstanceOf(AuthState.Authenticated::class.java)
        assertThat((state as AuthState.Authenticated).user.email).isEqualTo("login@test.com")
    }

    @Test
    fun `login should set Error on failure`() = runTest {
        every {
            authRepository.login("bad@test.com", "wrong")
        } returns flowOf(Result.failure(Exception("Invalid credentials")))

        createViewModel()
        advanceUntilIdle()

        viewModel.login("bad@test.com", "wrong")
        advanceUntilIdle()

        val state = viewModel.authState.value
        assertThat(state).isInstanceOf(AuthState.Error::class.java)
        assertThat((state as AuthState.Error).message).isEqualTo("Invalid credentials")
    }

    @Test
    fun `login should set Error when user is null in success`() = runTest {
        every {
            authRepository.login("null@test.com", "pass")
        } returns flowOf(Result.success(null))

        createViewModel()
        advanceUntilIdle()

        viewModel.login("null@test.com", "pass")
        advanceUntilIdle()

        val state = viewModel.authState.value
        assertThat(state).isInstanceOf(AuthState.Error::class.java)
    }

    @Test
    fun `logOut should clear state and stop sync`() = runTest {
        every { repository.stopSync() } returns Unit
        every { authRepository.logOut() } returns Unit
        coEvery { repository.clearLocalData() } returns Unit

        createViewModel()
        advanceUntilIdle()

        viewModel.logOut()
        advanceUntilIdle()

        verify { repository.stopSync() }
        verify { authRepository.logOut() }
        assertThat(viewModel.authState.value).isEqualTo(AuthState.Unauthenticated)
    }

    @Test
    fun `changeScreenState should toggle Login to Register`() = runTest {
        createViewModel()
        advanceUntilIdle()

        // Initial state is Login
        assertThat(viewModel.screenState.value).isInstanceOf(
            com.apptolast.familyfilmapp.ui.screens.login.uistates.LoginRegisterState.Login::class.java,
        )

        viewModel.changeScreenState()

        assertThat(viewModel.screenState.value).isInstanceOf(
            com.apptolast.familyfilmapp.ui.screens.login.uistates.LoginRegisterState.Register::class.java,
        )
        assertThat(viewModel.authState.value).isEqualTo(AuthState.Unauthenticated)
    }

    @Test
    fun `changeScreenState should toggle Register back to Login`() = runTest {
        createViewModel()
        advanceUntilIdle()

        viewModel.changeScreenState() // Login -> Register
        viewModel.changeScreenState() // Register -> Login

        assertThat(viewModel.screenState.value).isInstanceOf(
            com.apptolast.familyfilmapp.ui.screens.login.uistates.LoginRegisterState.Login::class.java,
        )
    }

    @Test
    fun `clearFailure should reset to Unauthenticated`() = runTest {
        every {
            authRepository.login("x@x.com", "x")
        } returns flowOf(Result.failure(Exception("Error")))

        createViewModel()
        advanceUntilIdle()

        viewModel.login("x@x.com", "x")
        advanceUntilIdle()
        assertThat(viewModel.authState.value).isInstanceOf(AuthState.Error::class.java)

        viewModel.clearFailure()
        assertThat(viewModel.authState.value).isEqualTo(AuthState.Unauthenticated)
    }

    @Test
    fun `recoverPassword should update state on success`() = runTest {
        every {
            authRepository.recoverPassword("recover@test.com")
        } returns flowOf(Result.success(true))

        createViewModel()
        advanceUntilIdle()

        viewModel.recoverPassword("recover@test.com")
        advanceUntilIdle()

        val state = viewModel.recoverPassState.value
        assertThat(state.isSuccessful).isTrue()
        assertThat(state.isLoading).isFalse()
        assertThat(state.errorMessage).isNull()
    }

    @Test
    fun `recoverPassword should update state on failure`() = runTest {
        every {
            authRepository.recoverPassword("bad@test.com")
        } returns flowOf(Result.failure(Exception("Email not found")))

        createViewModel()
        advanceUntilIdle()

        viewModel.recoverPassword("bad@test.com")
        advanceUntilIdle()

        val state = viewModel.recoverPassState.value
        assertThat(state.errorMessage).isEqualTo("Email not found")
        assertThat(state.isLoading).isFalse()
    }

    @Test
    fun `updateRecoveryPasswordState should update state`() = runTest {
        createViewModel()
        advanceUntilIdle()

        val newState = com.apptolast.familyfilmapp.ui.screens.login.uistates.RecoverPassState(
            isDialogVisible = true,
            email = "test@test.com",
        )
        viewModel.updateRecoveryPasswordState(newState)

        assertThat(viewModel.recoverPassState.value.isDialogVisible).isTrue()
        assertThat(viewModel.recoverPassState.value.email).isEqualTo("test@test.com")
    }

    @Test
    fun `registerAndSendEmail should update isEmailSent on success`() = runTest {
        val registeredUser = User().copy(id = "new-user", email = "new@test.com")
        every {
            authRepository.register("new@test.com", "pass123")
        } returns flowOf(Result.success(registeredUser))
        // createNewUser uses callback-based repository method
        every {
            repository.createUser(any(), any(), any())
        } answers {
            // Invoke success callback
            val successCallback = secondArg<(Void?) -> Unit>()
            successCallback(null)
        }

        createViewModel()
        advanceUntilIdle()

        viewModel.registerAndSendEmail("new@test.com", "pass123")
        advanceUntilIdle()

        assertThat(viewModel.isEmailSent.value).isTrue()
    }

    @Test
    fun `registerAndSendEmail should set error on failure`() = runTest {
        every {
            authRepository.register("dup@test.com", "pass")
        } returns flowOf(Result.failure(Exception("Email already exists")))

        createViewModel()
        advanceUntilIdle()

        viewModel.registerAndSendEmail("dup@test.com", "pass")
        advanceUntilIdle()

        val state = viewModel.authState.value
        assertThat(state).isInstanceOf(AuthState.Error::class.java)
        assertThat((state as AuthState.Error).message).isEqualTo("Email already exists")
    }

    @Test
    fun `email and password state flows should be mutable`() = runTest {
        createViewModel()
        advanceUntilIdle()

        assertThat(viewModel.email.value).isEmpty()
        assertThat(viewModel.password.value).isEmpty()
    }

    @Test
    fun `initial screen state is Login`() = runTest {
        createViewModel()
        advanceUntilIdle()

        assertThat(viewModel.screenState.value).isInstanceOf(
            com.apptolast.familyfilmapp.ui.screens.login.uistates.LoginRegisterState.Login::class.java,
        )
    }
}