@file:OptIn(ExperimentalCoroutinesApi::class)

package com.apptolast.familyfilmapp.ui.screens.home

import com.apptolast.familyfilmapp.ads.NativeAdManager
import com.apptolast.familyfilmapp.analytics.AnalyticsTracker
import com.apptolast.familyfilmapp.exceptions.CustomException
import com.apptolast.familyfilmapp.firebase.CrashReporter
import com.apptolast.familyfilmapp.firebase.CurrentUserIdProvider
import com.apptolast.familyfilmapp.model.local.types.MediaFilter
import com.apptolast.familyfilmapp.network.TmdbLocaleManager
import com.apptolast.familyfilmapp.repositories.Repository
import com.apptolast.familyfilmapp.repositories.datasources.TmdbDatasource
import com.apptolast.familyfilmapp.testing.TestDispatcherProvider
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class HomeViewModelTest {

    private lateinit var viewModel: HomeViewModel

    private val testDispatcher = StandardTestDispatcher()
    private val dispatcherProvider = TestDispatcherProvider(testDispatcher)

    private val repository = mock<Repository>(MockMode.autoUnit)
    private val tmdbDatasource = mock<TmdbDatasource>(MockMode.autoUnit)
    private val tmdbLocaleManager = mock<TmdbLocaleManager>(MockMode.autoUnit)
    private val nativeAdManager = mock<NativeAdManager>(MockMode.autoUnit)
    private val analyticsTracker = mock<AnalyticsTracker>(MockMode.autoUnit)
    private val crashReporter = mock<CrashReporter>(MockMode.autoUnit)
    private val currentUserIdProvider = mock<CurrentUserIdProvider>(MockMode.autoUnit)

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        every { nativeAdManager.nativeAds } returns MutableStateFlow(emptyList())
        everySuspend { repository.getPopularMoviesList(1) } returns Result.success(emptyList())
        everySuspend { repository.getPopularTvShowsList(1) } returns Result.success(emptyList())
        // Default to "signed out" — tests that need a stable id override it.
        every { currentUserIdProvider.currentUserId() } returns null

        viewModel = HomeViewModel(
            repository = repository,
            tmdbDatasource = tmdbDatasource,
            dispatcherProvider = dispatcherProvider,
            tmdbLocaleManager = tmdbLocaleManager,
            nativeAdManager = nativeAdManager,
            analyticsTracker = analyticsTracker,
            crashReporter = crashReporter,
            currentUserIdProvider = currentUserIdProvider,
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
