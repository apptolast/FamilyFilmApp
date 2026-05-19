@file:OptIn(ExperimentalCoroutinesApi::class)

package com.apptolast.familyfilmapp.ui.screens.groups

import com.apptolast.familyfilmapp.analytics.AnalyticsTracker
import com.apptolast.familyfilmapp.firebase.CrashReporter
import com.apptolast.familyfilmapp.firebase.CurrentUserIdProvider
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.SyncState
import com.apptolast.familyfilmapp.model.local.User
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

class GroupViewModelTest {

    private lateinit var viewModel: GroupViewModel

    private val testDispatcher: CoroutineDispatcher = StandardTestDispatcher()

    private val repository = mock<Repository>(MockMode.autoUnit)
    private val analyticsTracker = mock<AnalyticsTracker>(MockMode.autoUnit)
    private val crashReporter = mock<CrashReporter>(MockMode.autoUnit)
    private val currentUserIdProvider = mock<CurrentUserIdProvider>(MockMode.autoUnit)
    private val cardStateStore = mock<RecommendedCardStateDatasource>(MockMode.autoUnit)

    private val testUserId = "test-user-id"
    private val testUser = User(
        id = testUserId,
        email = "test@test.com",
        language = "en-US",
        photoUrl = "",
    )
    private val testGroup = Group(
        id = "group-1",
        ownerId = testUserId,
        name = "Test Group",
        users = listOf(testUserId),
        lastUpdated = null,
    )

    private val groupsFlow = MutableStateFlow(listOf(testGroup))
    private val syncStateFlow = MutableStateFlow<SyncState>(SyncState.Synced)

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        every { currentUserIdProvider.currentUserId() } returns testUserId
        every { repository.getMyGroups(testUserId) } returns groupsFlow
        every { repository.getSyncState() } returns syncStateFlow
        every { repository.getMovieStatusesByGroup(any()) } returns flowOf(emptyList())
        everySuspend { repository.getUsersByIds(listOf(testUserId)) } returns Result.success(listOf(testUser))
        everySuspend { repository.getMoviesByIds(any()) } returns Result.success(emptyList())
        everySuspend { repository.getTvShowsByIds(any()) } returns Result.success(emptyList())
        every { cardStateStore.getRevealedMediaId(any()) } returns null
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): GroupViewModel {
        viewModel = GroupViewModel(
            repository = repository,
            analyticsTracker = analyticsTracker,
            crashReporter = crashReporter,
            currentUserIdProvider = currentUserIdProvider,
            cardStateStore = cardStateStore,
        )
        return viewModel
    }

    @Test
    fun `init observes groups and auto-selects the first one when user is authenticated`() = runTest {
        createViewModel()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(1, state.groups.size)
        assertEquals("group-1", state.groups.first().id)
        assertEquals("group-1", state.selectedGroupId)
        assertNotNull(state.selectedGroupData)
        assertEquals("Test Group", state.selectedGroupData?.group?.name)
        assertEquals(1, state.selectedGroupData?.members?.size)
        assertEquals(false, state.isLoading)
    }

    @Test
    fun `init sets error when user is not authenticated`() = runTest {
        every { currentUserIdProvider.currentUserId() } returns null

        createViewModel()
        advanceUntilIdle()

        assertEquals("User not authenticated", viewModel.state.value.error)
    }

    @Test
    fun `createGroup selects the newly created group on success`() = runTest {
        val newGroup = Group(
            id = "group-new",
            ownerId = testUserId,
            name = "New Group",
            users = listOf(testUserId),
            lastUpdated = null,
        )
        everySuspend { repository.createGroup("New Group", testUserId) } returns Result.success(newGroup)

        createViewModel()
        advanceUntilIdle()

        // Update the flow to include the new group so loadGroupData can find it.
        groupsFlow.value = listOf(testGroup, newGroup)
        advanceUntilIdle()

        viewModel.createGroup("New Group")
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals("group-new", state.selectedGroupId)
        assertEquals(false, state.isLoading)
    }

    @Test
    fun `createGroup surfaces repository error in state`() = runTest {
        everySuspend {
            repository.createGroup("Bad Group", testUserId)
        } returns Result.failure(Exception("Network error"))

        createViewModel()
        advanceUntilIdle()

        viewModel.createGroup("Bad Group")
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals("Network error", state.error)
        assertEquals(false, state.isLoading)
    }

    @Test
    fun `createGroup sets error when user is not authenticated`() = runTest {
        every { currentUserIdProvider.currentUserId() } returns null

        createViewModel()
        advanceUntilIdle()

        viewModel.createGroup("Any Group")
        advanceUntilIdle()

        assertTrue(viewModel.state.value.error?.contains("not authenticated") == true)
    }

    @Test
    fun `deleteGroup falls back to the previous group on success`() = runTest {
        val secondGroup = Group(
            id = "group-2",
            ownerId = testUserId,
            name = "Second Group",
            users = listOf(testUserId),
            lastUpdated = null,
        )
        groupsFlow.value = listOf(testGroup, secondGroup)
        everySuspend { repository.deleteGroup("group-2") } returns Result.success(Unit)

        createViewModel()
        advanceUntilIdle()

        viewModel.selectGroup("group-2")
        advanceUntilIdle()

        viewModel.deleteGroup("group-2")
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(false, state.isLoading)
        assertEquals("group-1", state.selectedGroupId)
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
    fun `addMember surfaces repository error in state`() = runTest {
        everySuspend {
            repository.addMember("group-1", "bad@test.com")
        } returns Result.failure(Exception("User not found"))

        createViewModel()
        advanceUntilIdle()

        viewModel.addMember("group-1", "bad@test.com")
        advanceUntilIdle()

        assertEquals("User not found", viewModel.state.value.error)
        assertEquals(false, viewModel.state.value.isLoading)
    }

    @Test
    fun `clearError resets the error field`() = runTest {
        everySuspend {
            repository.deleteGroup("group-1")
        } returns Result.failure(Exception("Boom"))

        createViewModel()
        advanceUntilIdle()

        viewModel.deleteGroup("group-1")
        advanceUntilIdle()
        assertEquals("Boom", viewModel.state.value.error)

        viewModel.clearError()
        assertNull(viewModel.state.value.error)
    }

    @Test
    fun `showDialog updates the dialog field`() = runTest {
        createViewModel()
        advanceUntilIdle()

        viewModel.showDialog(GroupViewModel.GroupScreenDialogs.CreateGroup)

        assertEquals(GroupViewModel.GroupScreenDialogs.CreateGroup, viewModel.state.value.showDialog)
    }
}
