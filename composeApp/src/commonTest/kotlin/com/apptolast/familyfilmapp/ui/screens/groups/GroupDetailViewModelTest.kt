@file:OptIn(ExperimentalCoroutinesApi::class)

package com.apptolast.familyfilmapp.ui.screens.groups

import com.apptolast.familyfilmapp.analytics.AnalyticsTracker
import com.apptolast.familyfilmapp.firebase.CrashReporter
import com.apptolast.familyfilmapp.firebase.CurrentUserIdProvider
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.GroupMediaStatus
import com.apptolast.familyfilmapp.model.local.Media
import com.apptolast.familyfilmapp.model.local.SyncState
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.model.local.types.MediaStatus
import com.apptolast.familyfilmapp.model.local.types.MediaType
import com.apptolast.familyfilmapp.repositories.Repository
import com.apptolast.familyfilmapp.repositories.datasources.RecommendedCardStateDatasource
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
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
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GroupDetailViewModelTest {

    private lateinit var viewModel: GroupDetailViewModel

    private val testDispatcher: CoroutineDispatcher = StandardTestDispatcher()
    private val repository = mock<Repository>(MockMode.autoUnit)
    private val analyticsTracker = mock<AnalyticsTracker>(MockMode.autoUnit)
    private val crashReporter = mock<CrashReporter>(MockMode.autoUnit)
    private val currentUserIdProvider = mock<CurrentUserIdProvider>(MockMode.autoUnit)
    private val cardStateStore = mock<RecommendedCardStateDatasource>(MockMode.autoUnit)

    private val ownerId = "owner-id"
    private val memberId = "member-id"
    private val owner = User(ownerId, "owner@test.com", "en-US", "", "Owner")
    private val member = User(memberId, "member@test.com", "en-US", "", "Member")
    private val testGroup = Group(
        id = "group-1",
        ownerId = ownerId,
        name = "Test Group",
        users = listOf(ownerId, memberId),
        lastUpdated = null,
    )
    private val statuses = listOf(
        GroupMediaStatus("group-1", ownerId, 1, MediaStatus.Watched, MediaType.MOVIE),
        GroupMediaStatus("group-1", ownerId, 2, MediaStatus.ToWatch, MediaType.MOVIE),
        GroupMediaStatus("group-1", memberId, 3, MediaStatus.ToWatch, MediaType.MOVIE),
    )

    private val groupsFlow = MutableStateFlow(listOf(testGroup))
    private val syncStateFlow = MutableStateFlow<SyncState>(SyncState.Synced)

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        every { currentUserIdProvider.currentUserId() } returns ownerId
        every { repository.getMyGroups(ownerId) } returns groupsFlow
        every { repository.getSyncState() } returns syncStateFlow
        every { repository.getMovieStatusesByGroup(any()) } returns flowOf(statuses)
        every { cardStateStore.getRevealedMediaId(any()) } returns null
        everySuspend { repository.getUsersByIds(any()) } returns Result.success(listOf(member, owner))
        everySuspend { repository.getMoviesByIds(listOf(1)) } returns Result.success(
            listOf(Media().copy(id = 1, title = "Watched", voteAverage = 6f)),
        )
        everySuspend { repository.getMoviesByIds(listOf(2, 3)) } returns Result.success(
            listOf(
                Media().copy(id = 2, title = "To Watch", voteAverage = 8f),
                Media().copy(id = 3, title = "Other To Watch", voteAverage = 7f),
            ),
        )
        everySuspend { repository.getTvShowsByIds(any()) } returns Result.success(emptyList())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(groupId: String = "group-1"): GroupDetailViewModel {
        viewModel = GroupDetailViewModel(
            repository = repository,
            analyticsTracker = analyticsTracker,
            crashReporter = crashReporter,
            currentUserIdProvider = currentUserIdProvider,
            cardStateStore = cardStateStore,
            groupId = groupId,
        )
        return viewModel
    }

    @Test
    fun `init loads group detail and sorts owner first`() = runTest {
        createViewModel()
        advanceUntilIdle()

        val data = viewModel.state.value.groupData
        assertNotNull(data)
        assertEquals("Test Group", data.group.name)
        assertEquals(ownerId, data.members.first().id)
        assertEquals(false, viewModel.state.value.isLoading)
    }

    @Test
    fun `init calculates watched and to watch stats per member`() = runTest {
        createViewModel()
        advanceUntilIdle()

        val stats = viewModel.state.value.groupData?.memberStats
        assertEquals(1, stats?.get(ownerId)?.watchedCount)
        assertEquals(1, stats?.get(ownerId)?.toWatchCount)
        assertEquals(0, stats?.get(memberId)?.watchedCount)
        assertEquals(1, stats?.get(memberId)?.toWatchCount)
    }

    @Test
    fun `init resolves group media lists and recommended media`() = runTest {
        createViewModel()
        advanceUntilIdle()

        val data = viewModel.state.value.groupData
        assertEquals(2, data?.mediaToWatch?.size)
        assertEquals(1, data?.mediaWatched?.size)
        assertEquals(2, data?.recommendedMedia?.id)
    }

    @Test
    fun `missing group requests navigation back`() = runTest {
        createViewModel(groupId = "missing")
        advanceUntilIdle()

        assertNull(viewModel.state.value.groupData)
        assertEquals(false, viewModel.state.value.isLoading)
        assertTrue(viewModel.state.value.navigateBackAfterDelete)
    }

    @Test
    fun `deleteGroup requests navigation back on success`() = runTest {
        everySuspend { repository.deleteGroup("group-1") } returns Result.success(Unit)

        createViewModel()
        advanceUntilIdle()

        viewModel.deleteGroup("group-1")
        advanceUntilIdle()

        assertTrue(viewModel.state.value.navigateBackAfterDelete)
        assertEquals(false, viewModel.state.value.isLoading)
    }

    @Test
    fun `deleteGroup surfaces repository error in state`() = runTest {
        everySuspend {
            repository.deleteGroup("group-1")
        } returns Result.failure(Exception("Delete failed"))

        createViewModel()
        advanceUntilIdle()

        viewModel.deleteGroup("group-1")
        advanceUntilIdle()

        assertEquals("Delete failed", viewModel.state.value.error)
        assertEquals(false, viewModel.state.value.isLoading)
    }

    @Test
    fun `revealRecommendedCard marks recommended card as revealed`() = runTest {
        createViewModel()
        advanceUntilIdle()

        viewModel.revealRecommendedCard()

        assertEquals(true, viewModel.state.value.groupData?.isRecommendedRevealed)
    }

    @Test
    fun `showDialog updates the dialog field`() = runTest {
        createViewModel()
        advanceUntilIdle()

        viewModel.showDialog(GroupDetailDialog.AddMember(testGroup))

        assertEquals(GroupDetailDialog.AddMember(testGroup), viewModel.state.value.showDialog)
    }
}
