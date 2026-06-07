@file:OptIn(ExperimentalCoroutinesApi::class)

package com.apptolast.familyfilmapp.ui.screens.groups

import com.apptolast.familyfilmapp.analytics.AnalyticsTracker
import com.apptolast.familyfilmapp.firebase.CrashReporter
import com.apptolast.familyfilmapp.firebase.CurrentUserIdProvider
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.SyncState
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.repositories.Repository
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GroupsViewModelTest {

    private lateinit var viewModel: GroupsViewModel

    private val testDispatcher: CoroutineDispatcher = StandardTestDispatcher()
    private val repository = mock<Repository>(MockMode.autoUnit)
    private val analyticsTracker = mock<AnalyticsTracker>(MockMode.autoUnit)
    private val crashReporter = mock<CrashReporter>(MockMode.autoUnit)
    private val currentUserIdProvider = mock<CurrentUserIdProvider>(MockMode.autoUnit)

    private val testUserId = "test-user-id"
    private val owner = User(testUserId, "owner@test.com", "en-US", "", "Owner")
    private val member = User("member-id", "member@test.com", "en-US", "", "Member")
    private val testGroup = Group(
        id = "group-1",
        ownerId = testUserId,
        name = "Test Group",
        users = listOf(testUserId, "member-id"),
        lastUpdated = null,
    )

    private val groupsFlow = MutableStateFlow(listOf(testGroup))
    private val syncStateFlow = MutableStateFlow<SyncState>(SyncState.Synced)
    private val removedFromGroupFlow = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        every { currentUserIdProvider.currentUserId() } returns testUserId
        every { repository.getMyGroups(testUserId) } returns groupsFlow
        every { repository.getSyncState() } returns syncStateFlow
        every { repository.observeRemovedFromGroupEvents() } returns removedFromGroupFlow
        everySuspend { repository.getUsersByIds(any()) } returns Result.success(listOf(owner, member))
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): GroupsViewModel {
        viewModel = GroupsViewModel(
            repository = repository,
            analyticsTracker = analyticsTracker,
            crashReporter = crashReporter,
            currentUserIdProvider = currentUserIdProvider,
        )
        return viewModel
    }

    @Test
    fun `init observes groups and builds summaries without selecting a group`() = runTest {
        createViewModel()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(1, state.summaries.size)
        assertEquals("group-1", state.summaries.first().group.id)
        assertEquals(2, state.summaries.first().members.size)
        assertEquals(false, state.isLoading)
        assertNull(state.createdGroupIdToOpen)
    }

    @Test
    fun `init sets error when user is not authenticated`() = runTest {
        every { currentUserIdProvider.currentUserId() } returns null

        createViewModel()
        advanceUntilIdle()

        assertEquals("User not authenticated", viewModel.state.value.error)
        assertEquals(false, viewModel.state.value.isLoading)
    }

    @Test
    fun `createGroup exposes created group navigation target on success`() = runTest {
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

        viewModel.createGroup("New Group")
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals("group-new", state.createdGroupIdToOpen)
        assertEquals(false, state.isCreatingGroup)
    }

    @Test
    fun `created group navigation can be marked handled`() = runTest {
        everySuspend {
            repository.createGroup("New Group", testUserId)
        } returns Result.success(testGroup.copy(id = "group-new"))

        createViewModel()
        advanceUntilIdle()

        viewModel.createGroup("New Group")
        advanceUntilIdle()
        viewModel.onCreatedGroupNavigationHandled()

        assertNull(viewModel.state.value.createdGroupIdToOpen)
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
        assertEquals(false, state.isCreatingGroup)
    }

    @Test
    fun `clearError resets the error field`() = runTest {
        everySuspend {
            repository.createGroup("Bad Group", testUserId)
        } returns Result.failure(Exception("Boom"))

        createViewModel()
        advanceUntilIdle()

        viewModel.createGroup("Bad Group")
        advanceUntilIdle()
        assertEquals("Boom", viewModel.state.value.error)

        viewModel.clearError()
        assertNull(viewModel.state.value.error)
    }

    @Test
    fun `showDialog updates the dialog field`() = runTest {
        createViewModel()
        advanceUntilIdle()

        viewModel.showDialog(GroupsScreenDialog.CreateGroup)

        assertEquals(GroupsScreenDialog.CreateGroup, viewModel.state.value.showDialog)
    }

    @Test
    fun `empty groups marks loading false and clears summaries`() = runTest {
        groupsFlow.value = emptyList()

        createViewModel()
        advanceUntilIdle()

        assertTrue(viewModel.state.value.summaries.isEmpty())
        assertEquals(false, viewModel.state.value.isLoading)
    }

    @Test
    fun `removed-from-group event raises the removedFromGroup flag`() = runTest {
        createViewModel()
        advanceUntilIdle()
        assertEquals(false, viewModel.state.value.removedFromGroup)

        removedFromGroupFlow.emit(Unit)
        advanceUntilIdle()

        assertTrue(viewModel.state.value.removedFromGroup)
    }

    @Test
    fun `onRemovedFromGroupHandled lowers the removedFromGroup flag`() = runTest {
        createViewModel()
        advanceUntilIdle()
        removedFromGroupFlow.emit(Unit)
        advanceUntilIdle()
        assertTrue(viewModel.state.value.removedFromGroup)

        viewModel.onRemovedFromGroupHandled()

        assertEquals(false, viewModel.state.value.removedFromGroup)
    }
}
