@file:OptIn(ExperimentalCoroutinesApi::class)

package com.apptolast.familyfilmapp.ui.screens.home

import com.apptolast.familyfilmapp.ads.NativeAdManager
import com.apptolast.familyfilmapp.analytics.AnalyticsTracker
import com.apptolast.familyfilmapp.exceptions.CustomException
import com.apptolast.familyfilmapp.firebase.CrashReporter
import com.apptolast.familyfilmapp.model.local.types.MediaFilter
import com.apptolast.familyfilmapp.network.TmdbLocaleManager
import com.apptolast.familyfilmapp.repositories.Repository
import com.apptolast.familyfilmapp.utils.DispatcherProvider
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

/**
 * Reference HomeViewModel test ported to commonTest with Mokkery.
 *
 * Demonstrates the migration's testing setup end-to-end: Mokkery
 * replaces MockK (`mock<T>()` + `every {} returns ...` / `everySuspend
 * {} returns ...`), `kotlin.test` replaces JUnit4 + Truth, and
 * Dispatchers.setMain(StandardTestDispatcher()) replaces the JUnit4
 * `MainDispatcherRule` (which doesn't apply in commonTest).
 *
 * Coverage focuses on the parts of the ViewModel that don't need a
 * mocked `Firebase.auth.currentUser?.uid` (the VM reads that lazily
 * via the GitLive singleton; tests that need a stable user id should
 * mock the GitLive Firebase object via Mokkery's mockObject pattern in
 * a follow-up).
 */
class HomeViewModelTest {

    private lateinit var viewModel: HomeViewModel

    private val testDispatcher: CoroutineDispatcher = StandardTestDispatcher()

    private val dispatcherProvider = object : DispatcherProvider {
        override fun main(): CoroutineDispatcher = testDispatcher
        override fun default(): CoroutineDispatcher = testDispatcher
        override fun io(): CoroutineDispatcher = testDispatcher
        override fun unconfined(): CoroutineDispatcher = testDispatcher
    }

    private val repository = mock<Repository>(MockMode.autoUnit)
    private val tmdbLocaleManager = mock<TmdbLocaleManager>(MockMode.autoUnit)
    private val nativeAdManager = mock<NativeAdManager>(MockMode.autoUnit)
    private val analyticsTracker = mock<AnalyticsTracker>(MockMode.autoUnit)
    private val crashReporter = mock<CrashReporter>(MockMode.autoUnit)

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        every { tmdbLocaleManager.includeAdult } returns MutableStateFlow(false)
        every { nativeAdManager.nativeAds } returns MutableStateFlow(emptyList())
        everySuspend { repository.getPopularMoviesList(1) } returns Result.success(emptyList())
        everySuspend { repository.getPopularTvShowsList(1) } returns Result.success(emptyList())

        viewModel = HomeViewModel(
            repository = repository,
            dispatcherProvider = dispatcherProvider,
            tmdbLocaleManager = tmdbLocaleManager,
            nativeAdManager = nativeAdManager,
            analyticsTracker = analyticsTracker,
            crashReporter = crashReporter,
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `searchMediaByName with empty string clears filterMedia and errorMessage`() = runTest {
        viewModel.searchMediaByName("")
        testScheduler.advanceUntilIdle()

        assertEquals(emptyList(), viewModel.homeUiState.value.filterMedia)
        assertEquals(CustomException.GenericException(null), viewModel.homeUiState.value.errorMessage)
    }

    @Test
    fun `setMediaFilter updates selectedFilter and resets filterMedia`() = runTest {
        viewModel.setMediaFilter(MediaFilter.TV_SHOWS)
        testScheduler.advanceUntilIdle()

        assertEquals(MediaFilter.TV_SHOWS, viewModel.homeUiState.value.selectedFilter)
        assertEquals(emptyList(), viewModel.homeUiState.value.filterMedia)
    }

    @Test
    fun `clearError sets errorMessage to GenericException(null)`() = runTest {
        viewModel.clearError()
        testScheduler.advanceUntilIdle()

        assertEquals(CustomException.GenericException(null), viewModel.homeUiState.value.errorMessage)
        // Sanity check: the inner string is null when "cleared"
        assertNull(viewModel.homeUiState.value.errorMessage?.error)
    }
}
