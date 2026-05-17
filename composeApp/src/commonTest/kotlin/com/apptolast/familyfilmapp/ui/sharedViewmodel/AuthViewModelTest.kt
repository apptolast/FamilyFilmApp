@file:OptIn(ExperimentalCoroutinesApi::class)

package com.apptolast.familyfilmapp.ui.sharedViewmodel

import com.apptolast.familyfilmapp.analytics.AnalyticsTracker
import com.apptolast.familyfilmapp.auth.AppleSignInClient
import com.apptolast.familyfilmapp.auth.AppleTokenRevoker
import com.apptolast.familyfilmapp.auth.GoogleSignInClient
import com.apptolast.familyfilmapp.firebase.CrashReporter
import com.apptolast.familyfilmapp.purchases.PurchaseManager
import com.apptolast.familyfilmapp.repositories.FirebaseAuthRepository
import com.apptolast.familyfilmapp.repositories.Repository
import com.apptolast.familyfilmapp.ui.screens.login.uistates.LoginRegisterState
import com.apptolast.familyfilmapp.utils.DispatcherProvider
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verifySuspend
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

// Authenticated-path tests are deferred until Mokkery can mock the GitLive auth surface.
class AuthViewModelTest {

    private lateinit var viewModel: AuthViewModel

    private val testDispatcher: CoroutineDispatcher = StandardTestDispatcher()

    private val dispatcherProvider = object : DispatcherProvider {
        override fun main(): CoroutineDispatcher = testDispatcher
        override fun default(): CoroutineDispatcher = testDispatcher
        override fun io(): CoroutineDispatcher = testDispatcher
        override fun unconfined(): CoroutineDispatcher = testDispatcher
    }

    private val authRepository = mock<FirebaseAuthRepository>(MockMode.autoUnit)
    private val repository = mock<Repository>(MockMode.autoUnit)
    private val googleSignInClient = mock<GoogleSignInClient>(MockMode.autoUnit)
    private val appleSignInClient = mock<AppleSignInClient>(MockMode.autoUnit)
    private val appleTokenRevoker = mock<AppleTokenRevoker>(MockMode.autoUnit)
    private val purchaseManager = mock<PurchaseManager>(MockMode.autoUnit)
    private val analyticsTracker = mock<AnalyticsTracker>(MockMode.autoUnit)
    private val crashReporter = mock<CrashReporter>(MockMode.autoUnit)

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        // Defaults so the VM settles on Unauthenticated.
        every { authRepository.getUser() } returns flowOf(null)
        every { authRepository.isTokenValid() } returns flowOf(false)
        every { authRepository.checkEmailVerification(any()) } returns flowOf(false)
        every { authRepository.getProvider() } returns flowOf(null)
        every { purchaseManager.hasRemovedAds } returns MutableStateFlow(false)
        every { purchaseManager.hasChatPremium } returns MutableStateFlow(false)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): AuthViewModel {
        viewModel = AuthViewModel(
            authRepository = authRepository,
            repository = repository,
            dispatcherProvider = dispatcherProvider,
            googleSignInClient = googleSignInClient,
            appleSignInClient = appleSignInClient,
            appleTokenRevoker = appleTokenRevoker,
            purchaseManager = purchaseManager,
            analyticsTracker = analyticsTracker,
            crashReporter = crashReporter,
        )
        return viewModel
    }

    @Test
    fun `init settles on Unauthenticated when no user is signed in`() = runTest {
        createViewModel()
        advanceUntilIdle()

        assertEquals(AuthState.Unauthenticated, viewModel.authState.value)
    }

    @Test
    fun `login emits Error when authRepository returns failure`() = runTest {
        every {
            authRepository.login("bad@test.com", "wrong")
        } returns flowOf(Result.failure(Exception("Invalid credentials")))

        createViewModel()
        advanceUntilIdle()

        viewModel.login("bad@test.com", "wrong")
        advanceUntilIdle()

        val state = viewModel.authState.value
        assertIs<AuthState.Error>(state)
        assertEquals("Invalid credentials", state.message)
    }

    @Test
    fun `login emits Error when success payload is null`() = runTest {
        every {
            authRepository.login("null@test.com", "pass")
        } returns flowOf(Result.success(null))

        createViewModel()
        advanceUntilIdle()

        viewModel.login("null@test.com", "pass")
        advanceUntilIdle()

        assertIs<AuthState.Error>(viewModel.authState.value)
    }

    @Test
    fun `changeScreenState toggles Login to Register and back to Login`() = runTest {
        createViewModel()
        advanceUntilIdle()

        assertIs<LoginRegisterState.Login>(viewModel.screenState.value)

        viewModel.changeScreenState()
        assertIs<LoginRegisterState.Register>(viewModel.screenState.value)
        // changeScreenState also resets authState back to Unauthenticated.
        assertEquals(AuthState.Unauthenticated, viewModel.authState.value)

        viewModel.changeScreenState()
        assertIs<LoginRegisterState.Login>(viewModel.screenState.value)
    }

    @Test
    fun `clearFailure resets Error state back to Unauthenticated`() = runTest {
        every {
            authRepository.login("x@x.com", "x")
        } returns flowOf(Result.failure(Exception("Error")))

        createViewModel()
        advanceUntilIdle()

        viewModel.login("x@x.com", "x")
        advanceUntilIdle()
        assertIs<AuthState.Error>(viewModel.authState.value)

        viewModel.clearFailure()
        assertEquals(AuthState.Unauthenticated, viewModel.authState.value)
    }

    @Test
    fun `logOut calls collaborators and resets state to Unauthenticated`() = runTest {
        createViewModel()
        advanceUntilIdle()

        viewModel.logOut()
        advanceUntilIdle()

        verify { repository.stopSync() }
        verifySuspend { authRepository.logOut() }
        verify { purchaseManager.logout() }
        assertEquals(AuthState.Unauthenticated, viewModel.authState.value)
    }

    @Test
    fun `recoverPassword emits success state when authRepository succeeds`() = runTest {
        every {
            authRepository.recoverPassword("user@test.com")
        } returns flowOf(Result.success(true))

        createViewModel()
        advanceUntilIdle()

        viewModel.recoverPassword("user@test.com")
        advanceUntilIdle()

        val state = viewModel.recoverPassState.value
        assertTrue(state.isSuccessful)
        assertEquals(false, state.isLoading)
        assertEquals(null, state.errorMessage)
    }

    @Test
    fun `recoverPassword sets errorMessage when authRepository fails`() = runTest {
        every {
            authRepository.recoverPassword("bad@test.com")
        } returns flowOf(Result.failure(Exception("Recover failed")))

        createViewModel()
        advanceUntilIdle()

        viewModel.recoverPassword("bad@test.com")
        advanceUntilIdle()

        val state = viewModel.recoverPassState.value
        assertEquals("Recover failed", state.errorMessage)
        assertEquals(false, state.isLoading)
    }

    @Test
    fun `googleSignIn surfaces Error when GoogleSignInClient returns null token`() = runTest {
        everySuspend { googleSignInClient.signIn() } returns null

        createViewModel()
        advanceUntilIdle()

        viewModel.googleSignIn()
        advanceUntilIdle()

        assertIs<AuthState.Error>(viewModel.authState.value)
    }

    @Test
    fun `skipUsernameSetup clears the username prompt flag`() = runTest {
        createViewModel()
        advanceUntilIdle()

        viewModel.skipUsernameSetup()
        assertEquals(false, viewModel.shouldPromptForUsername.value)
    }
}
