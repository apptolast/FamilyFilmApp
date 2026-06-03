@file:OptIn(ExperimentalCoroutinesApi::class)

package com.apptolast.familyfilmapp.ui.screens.discover

import com.apptolast.familyfilmapp.analytics.AnalyticsTracker
import com.apptolast.familyfilmapp.firebase.CrashReporter
import com.apptolast.familyfilmapp.firebase.CurrentUserIdProvider
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.Media
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.model.local.key
import com.apptolast.familyfilmapp.model.local.types.MediaFilter
import com.apptolast.familyfilmapp.model.local.types.MediaType
import com.apptolast.familyfilmapp.network.TmdbLocaleManager
import com.apptolast.familyfilmapp.repositories.Repository
import com.apptolast.familyfilmapp.testing.TestDispatcherProvider
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
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

class DiscoverViewModelTest {

    private lateinit var viewModel: DiscoverViewModel

    private val testDispatcher = StandardTestDispatcher()
    private val dispatcherProvider = TestDispatcherProvider(testDispatcher)

    private val repository = mock<Repository>(MockMode.autoUnit)
    private val tmdbLocaleManager = mock<TmdbLocaleManager>(MockMode.autoUnit)
    private val analyticsTracker = mock<AnalyticsTracker>(MockMode.autoUnit)
    private val crashReporter = mock<CrashReporter>(MockMode.autoUnit)
    private val currentUserIdProvider = mock<CurrentUserIdProvider>(MockMode.autoUnit)

    private val testUserId = "user-1"
    private val testUser = User(id = testUserId, email = "user@test.com", language = "en-US", photoUrl = "")
    private val testGroup = Group(
        id = "group-1",
        ownerId = testUserId,
        name = "Family",
        users = listOf(testUserId),
        lastUpdated = null,
    )
    private val skippedFlow = MutableStateFlow<List<Media>>(emptyList())
    private var shuffler: MediaShuffler = MediaShuffler { media -> media }

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        skippedFlow.value = emptyList()
        shuffler = MediaShuffler { media -> media }

        every { currentUserIdProvider.currentUserId() } returns testUserId
        every { tmdbLocaleManager.includeAdult } returns MutableStateFlow(false)
        every { repository.getUserById(testUserId) } returns flowOf(testUser)
        every { repository.getMyGroups(testUserId) } returns flowOf(listOf(testGroup))
        every { repository.observeSkippedMedia(testUserId) } returns skippedFlow
        everySuspend { repository.getAllMarkedMediaKeysForUser(testUserId) } returns emptyList()
        everySuspend { repository.getSkippedMediaKeysForUser(testUserId) } returns emptyList()
        everySuspend { repository.getPopularMoviesList(any()) } returns Result.success(emptyList())
        everySuspend { repository.getPopularTvShowsList(any()) } returns Result.success(emptyList())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): DiscoverViewModel {
        viewModel = DiscoverViewModel(
            repository = repository,
            dispatcherProvider = dispatcherProvider,
            tmdbLocaleManager = tmdbLocaleManager,
            analyticsTracker = analyticsTracker,
            crashReporter = crashReporter,
            currentUserIdProvider = currentUserIdProvider,
            mediaShuffler = shuffler,
        )
        return viewModel
    }

    @Test
    fun `initial load applies MediaShuffler`() = runTest {
        val movie = media(1, "Movie", MediaType.MOVIE, popularity = 30f)
        val secondMovie = media(2, "Second Movie", MediaType.MOVIE, popularity = 20f)
        val tvShow = media(3, "Show", MediaType.TV_SHOW, popularity = 10f)
        shuffler = MediaShuffler { media -> media.reversed() }
        everySuspend { repository.getPopularMoviesList(1) } returns Result.success(listOf(secondMovie, movie))
        everySuspend { repository.getPopularTvShowsList(1) } returns Result.success(listOf(tvShow))

        createViewModel()
        advanceUntilIdle()

        assertEquals(listOf(tvShow, secondMovie, movie), viewModel.uiState.value.mediaList)
    }

    @Test
    fun `initial load filters marked and skipped media by media key`() = runTest {
        val movie = media(1, "Movie", MediaType.MOVIE)
        val markedMovie = media(2, "Marked", MediaType.MOVIE)
        val skippedShow = media(3, "Skipped Show", MediaType.TV_SHOW)
        everySuspend { repository.getAllMarkedMediaKeysForUser(testUserId) } returns listOf(markedMovie.key)
        everySuspend { repository.getSkippedMediaKeysForUser(testUserId) } returns listOf(skippedShow.key)
        everySuspend { repository.getPopularMoviesList(1) } returns Result.success(listOf(movie, markedMovie))
        everySuspend { repository.getPopularTvShowsList(1) } returns Result.success(listOf(skippedShow))

        createViewModel()
        advanceUntilIdle()

        assertEquals(listOf(movie), viewModel.uiState.value.mediaList)
    }

    @Test
    fun `skipMedia persists locally and advances current media`() = runTest {
        val mediaList = (1..5).map { media(it, "Movie $it", MediaType.MOVIE) }
        everySuspend { repository.getPopularMoviesList(1) } returns Result.success(mediaList)
        everySuspend { repository.getPopularTvShowsList(1) } returns Result.success(emptyList())

        createViewModel()
        advanceUntilIdle()
        val firstLoadedMedia = viewModel.uiState.value.mediaList.first()

        viewModel.skipMedia()
        advanceUntilIdle()

        verifySuspend { repository.skipMedia(testUserId, firstLoadedMedia) }
        assertEquals(1, viewModel.uiState.value.currentMediaIndex)
    }

    @Test
    fun `restoreSkippedMedia deletes local skip prepends media and resets current index`() = runTest {
        val skipped = media(99, "Restored", MediaType.TV_SHOW)
        val movie = media(1, "Movie", MediaType.MOVIE)
        val secondMovie = media(2, "Second Movie", MediaType.MOVIE)
        skippedFlow.value = listOf(skipped)
        everySuspend { repository.getSkippedMediaKeysForUser(testUserId) } returns listOf(skipped.key)
        everySuspend { repository.getPopularMoviesList(1) } returns Result.success(listOf(movie, secondMovie))
        everySuspend { repository.getPopularTvShowsList(1) } returns Result.success(listOf(skipped))
        everySuspend { repository.restoreSkippedMedia(testUserId, skipped.key) } returns skipped

        createViewModel()
        advanceUntilIdle()

        viewModel.skipMedia()
        advanceUntilIdle()
        viewModel.restoreSkippedMedia(skipped)
        advanceUntilIdle()

        verifySuspend { repository.restoreSkippedMedia(testUserId, skipped.key) }
        assertEquals(0, viewModel.uiState.value.currentMediaIndex)
        assertEquals(skipped, viewModel.uiState.value.mediaList.first())
        assertEquals(emptyList(), viewModel.uiState.value.skippedMedia)
    }

    @Test
    fun `setMediaFilter preserves key filtering and applies shuffler`() = runTest {
        val movie = media(1, "Movie", MediaType.MOVIE)
        val markedMovie = media(2, "Marked Movie", MediaType.MOVIE)
        val skippedMovie = media(3, "Skipped Movie", MediaType.MOVIE)
        val lastMovie = media(4, "Last Movie", MediaType.MOVIE)
        shuffler = MediaShuffler { media -> media.reversed() }
        everySuspend { repository.getAllMarkedMediaKeysForUser(testUserId) } returns listOf(markedMovie.key)
        everySuspend { repository.getSkippedMediaKeysForUser(testUserId) } returns listOf(skippedMovie.key)
        everySuspend {
            repository.getPopularMoviesList(1)
        } returns Result.success(listOf(movie, markedMovie, skippedMovie, lastMovie))

        createViewModel()
        advanceUntilIdle()

        viewModel.setMediaFilter(MediaFilter.MOVIES)
        advanceUntilIdle()

        assertEquals(MediaFilter.MOVIES, viewModel.uiState.value.selectedFilter)
        assertEquals(listOf(lastMovie, movie), viewModel.uiState.value.mediaList)
    }

    private fun media(
        id: Int,
        title: String,
        mediaType: MediaType,
        popularity: Float = id.toFloat(),
    ): Media = Media(
        id = id,
        title = title,
        adult = false,
        popularity = popularity,
        voteAverage = 7f,
        streamProviders = emptyList(),
        buyProviders = emptyList(),
        rentProviders = emptyList(),
        releaseDate = "2024-01-01",
        overview = "$title overview",
        posterPath = "/$id.jpg",
        mediaType = mediaType,
    )

}
