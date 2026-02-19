package com.apptolast.familyfilmapp.ui.screens.groups

import com.apptolast.familyfilmapp.MainDispatcherRule
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.Movie
import com.apptolast.familyfilmapp.model.local.SyncState
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.model.local.types.MovieStatus
import com.apptolast.familyfilmapp.repositories.Repository
import com.google.common.truth.Truth.assertThat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GroupViewModelTest {

    private lateinit var viewModel: GroupViewModel

    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val dispatcher = MainDispatcherRule()

    @MockK
    lateinit var repository: Repository

    @MockK
    lateinit var auth: FirebaseAuth

    @RelaxedMockK
    lateinit var firebaseUser: FirebaseUser

    private val testUserId = "test-user-id"
    private val testUser = User().copy(id = testUserId, email = "test@test.com")
    private val testGroup = Group().copy(
        id = "group-1",
        ownerId = testUserId,
        name = "Test Group",
        users = listOf(testUserId),
    )

    private val groupsFlow = MutableStateFlow(listOf(testGroup))
    private val syncStateFlow = MutableStateFlow<SyncState>(SyncState.Synced)

    @Before
    fun setUp() {
        every { auth.currentUser } returns firebaseUser
        every { firebaseUser.uid } returns testUserId
        every { repository.getMyGroups(testUserId) } returns groupsFlow
        every { repository.getSyncState() } returns syncStateFlow
        every { repository.startSync(testUserId) } returns Unit
        every { repository.stopSync() } returns Unit
        coEvery { repository.getUsersByIds(listOf(testUserId)) } returns Result.success(listOf(testUser))
        coEvery { repository.getMoviesByIds(any()) } returns Result.success(emptyList())
    }

    private fun createViewModel(): GroupViewModel {
        viewModel = GroupViewModel(repository, auth)
        return viewModel
    }

    @Test
    fun `init should start observing groups and sync when user is authenticated`() = runTest {
        createViewModel()
        advanceUntilIdle()

        verify { repository.startSync(testUserId) }
        verify { repository.getMyGroups(testUserId) }
        verify { repository.getSyncState() }

        val state = viewModel.state.value
        assertThat(state.groups).hasSize(1)
        assertThat(state.groups.first().id).isEqualTo("group-1")
        assertThat(state.isLoading).isFalse()
    }

    @Test
    fun `init should set error when user is not authenticated`() = runTest {
        every { auth.currentUser } returns null

        createViewModel()
        advanceUntilIdle()

        assertThat(viewModel.state.value.error).isEqualTo("User not authenticated")
    }

    @Test
    fun `init should auto-select first group and load its data`() = runTest {
        createViewModel()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertThat(state.selectedGroupId).isEqualTo("group-1")
        assertThat(state.selectedGroupData).isNotNull()
        assertThat(state.selectedGroupData?.group?.name).isEqualTo("Test Group")
        assertThat(state.selectedGroupData?.members).hasSize(1)
    }

    @Test
    fun `selectGroup should update selected group and load data`() = runTest {
        val secondGroup = Group().copy(
            id = "group-2",
            ownerId = testUserId,
            name = "Second Group",
            users = listOf(testUserId),
        )
        groupsFlow.value = listOf(testGroup, secondGroup)

        createViewModel()
        advanceUntilIdle()

        viewModel.selectGroup("group-2")
        advanceUntilIdle()

        val state = viewModel.state.value
        assertThat(state.selectedGroupId).isEqualTo("group-2")
        assertThat(state.selectedGroupData?.group?.name).isEqualTo("Second Group")
    }

    @Test
    fun `selectGroup should do nothing for non-existent group`() = runTest {
        createViewModel()
        advanceUntilIdle()

        val stateBefore = viewModel.state.value
        viewModel.selectGroup("non-existent")
        advanceUntilIdle()

        // Selection should remain unchanged
        assertThat(viewModel.state.value.selectedGroupId).isEqualTo(stateBefore.selectedGroupId)
    }

    @Test
    fun `createGroup should update state on success`() = runTest {
        val newGroup = Group().copy(
            id = "group-new",
            ownerId = testUserId,
            name = "New Group",
            users = listOf(testUserId),
        )
        coEvery { repository.createGroup("New Group", testUserId) } returns Result.success(newGroup)

        createViewModel()
        advanceUntilIdle()

        // Update the flow to include the new group so loadGroupData can find it
        groupsFlow.value = listOf(testGroup, newGroup)
        advanceUntilIdle()

        viewModel.createGroup("New Group")
        advanceUntilIdle()

        val state = viewModel.state.value
        assertThat(state.selectedGroupId).isEqualTo("group-new")
        assertThat(state.isLoading).isFalse()
    }

    @Test
    fun `createGroup should set error on failure`() = runTest {
        coEvery {
            repository.createGroup("Bad Group", testUserId)
        } returns Result.failure(Exception("Network error"))

        createViewModel()
        advanceUntilIdle()

        viewModel.createGroup("Bad Group")
        advanceUntilIdle()

        val state = viewModel.state.value
        assertThat(state.error).isEqualTo("Network error")
        assertThat(state.isLoading).isFalse()
    }

    @Test
    fun `createGroup should set error when user is not authenticated`() = runTest {
        every { auth.currentUser } returns null

        createViewModel()
        advanceUntilIdle()

        viewModel.createGroup("Any Group")
        advanceUntilIdle()

        assertThat(viewModel.state.value.error).contains("not authenticated")
    }

    @Test
    fun `deleteGroup should select previous group on success`() = runTest {
        val secondGroup = Group().copy(
            id = "group-2",
            ownerId = testUserId,
            name = "Second Group",
            users = listOf(testUserId),
        )
        groupsFlow.value = listOf(testGroup, secondGroup)
        coEvery { repository.deleteGroup("group-2") } returns Result.success(Unit)

        createViewModel()
        advanceUntilIdle()

        // Select second group then delete it
        viewModel.selectGroup("group-2")
        advanceUntilIdle()

        viewModel.deleteGroup("group-2")
        advanceUntilIdle()

        val state = viewModel.state.value
        assertThat(state.isLoading).isFalse()
        // Should fall back to previous group (group-1)
        assertThat(state.selectedGroupId).isEqualTo("group-1")
    }

    @Test
    fun `deleteGroup should set error on failure`() = runTest {
        coEvery {
            repository.deleteGroup("group-1")
        } returns Result.failure(Exception("Delete failed"))

        createViewModel()
        advanceUntilIdle()

        viewModel.deleteGroup("group-1")
        advanceUntilIdle()

        assertThat(viewModel.state.value.error).isEqualTo("Delete failed")
        assertThat(viewModel.state.value.isLoading).isFalse()
    }

    @Test
    fun `addMember should reload group data on success`() = runTest {
        coEvery { repository.addMember("group-1", "new@test.com") } returns Result.success(Unit)
        val updatedUsers = listOf(testUserId, "new-user-id")
        val newUser = User().copy(id = "new-user-id", email = "new@test.com")
        coEvery { repository.getUsersByIds(updatedUsers) } returns Result.success(listOf(testUser, newUser))

        createViewModel()
        advanceUntilIdle()

        viewModel.addMember("group-1", "new@test.com")
        advanceUntilIdle()

        assertThat(viewModel.state.value.error).isNull()
    }

    @Test
    fun `addMember should set error on failure`() = runTest {
        coEvery {
            repository.addMember("group-1", "bad@test.com")
        } returns Result.failure(Exception("User not found"))

        createViewModel()
        advanceUntilIdle()

        viewModel.addMember("group-1", "bad@test.com")
        advanceUntilIdle()

        assertThat(viewModel.state.value.error).isEqualTo("User not found")
        assertThat(viewModel.state.value.isLoading).isFalse()
    }

    @Test
    fun `removeMember should reload group data on success`() = runTest {
        coEvery { repository.removeMember("group-1", "other-user") } returns Result.success(Unit)

        createViewModel()
        advanceUntilIdle()

        viewModel.removeMember("group-1", "other-user")
        advanceUntilIdle()

        assertThat(viewModel.state.value.error).isNull()
    }

    @Test
    fun `removeMember should set error on failure`() = runTest {
        coEvery {
            repository.removeMember("group-1", "other-user")
        } returns Result.failure(Exception("Permission denied"))

        createViewModel()
        advanceUntilIdle()

        viewModel.removeMember("group-1", "other-user")
        advanceUntilIdle()

        assertThat(viewModel.state.value.error).isEqualTo("Permission denied")
    }

    @Test
    fun `changeGroupName should update state on success`() = runTest {
        val updatedGroup = testGroup.copy(name = "Updated Name")
        coEvery { repository.updateGroup(updatedGroup) } returns Result.success(Unit)

        createViewModel()
        advanceUntilIdle()

        viewModel.changeGroupName(updatedGroup)
        advanceUntilIdle()

        assertThat(viewModel.state.value.isLoading).isFalse()
        assertThat(viewModel.state.value.error).isNull()
    }

    @Test
    fun `changeGroupName should set error on failure`() = runTest {
        val updatedGroup = testGroup.copy(name = "Updated Name")
        coEvery {
            repository.updateGroup(updatedGroup)
        } returns Result.failure(Exception("Update failed"))

        createViewModel()
        advanceUntilIdle()

        viewModel.changeGroupName(updatedGroup)
        advanceUntilIdle()

        assertThat(viewModel.state.value.error).isEqualTo("Update failed")
    }

    @Test
    fun `syncState changes should propagate to UI state`() = runTest {
        createViewModel()
        advanceUntilIdle()

        assertThat(viewModel.state.value.syncState).isEqualTo(SyncState.Synced)

        syncStateFlow.value = SyncState.Syncing
        advanceUntilIdle()
        assertThat(viewModel.state.value.syncState).isEqualTo(SyncState.Syncing)

        syncStateFlow.value = SyncState.Error("Sync failed")
        advanceUntilIdle()
        assertThat(viewModel.state.value.syncState).isInstanceOf(SyncState.Error::class.java)
    }

    @Test
    fun `showDialog should update dialog state`() = runTest {
        createViewModel()
        advanceUntilIdle()

        viewModel.showDialog(GroupViewModel.GroupScreenDialogs.CreateGroup)
        assertThat(viewModel.state.value.showDialog).isEqualTo(GroupViewModel.GroupScreenDialogs.CreateGroup)

        viewModel.showDialog(GroupViewModel.GroupScreenDialogs.None)
        assertThat(viewModel.state.value.showDialog).isEqualTo(GroupViewModel.GroupScreenDialogs.None)
    }

    @Test
    fun `clearError should reset error to null`() = runTest {
        coEvery {
            repository.createGroup("Bad", testUserId)
        } returns Result.failure(Exception("Error"))

        createViewModel()
        advanceUntilIdle()

        viewModel.createGroup("Bad")
        advanceUntilIdle()
        assertThat(viewModel.state.value.error).isNotNull()

        viewModel.clearError()
        assertThat(viewModel.state.value.error).isNull()
    }

    @Test
    fun `empty groups list should clear selection`() = runTest {
        createViewModel()
        advanceUntilIdle()

        groupsFlow.value = emptyList()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertThat(state.groups).isEmpty()
        assertThat(state.selectedGroupId).isNull()
        assertThat(state.selectedGroupData).isNull()
    }

    @Test
    fun `groups update preserves current selection if group still exists`() = runTest {
        val secondGroup = Group().copy(
            id = "group-2",
            ownerId = testUserId,
            name = "Second Group",
            users = listOf(testUserId),
        )
        groupsFlow.value = listOf(testGroup, secondGroup)

        createViewModel()
        advanceUntilIdle()

        viewModel.selectGroup("group-2")
        advanceUntilIdle()
        assertThat(viewModel.state.value.selectedGroupId).isEqualTo("group-2")

        // Update groups but keep group-2
        val updatedSecondGroup = secondGroup.copy(name = "Renamed Group")
        groupsFlow.value = listOf(testGroup, updatedSecondGroup)
        advanceUntilIdle()

        // Selection should remain on group-2
        assertThat(viewModel.state.value.selectedGroupId).isEqualTo("group-2")
    }

    @Test
    fun `groups update selects first when current selection is deleted`() = runTest {
        val secondGroup = Group().copy(
            id = "group-2",
            ownerId = testUserId,
            name = "Second Group",
            users = listOf(testUserId),
        )
        groupsFlow.value = listOf(testGroup, secondGroup)

        createViewModel()
        advanceUntilIdle()

        viewModel.selectGroup("group-2")
        advanceUntilIdle()

        // Remove group-2 from the list
        groupsFlow.value = listOf(testGroup)
        advanceUntilIdle()

        // Should fall back to first group
        assertThat(viewModel.state.value.selectedGroupId).isEqualTo("group-1")
    }

    @Test
    fun `loadGroupData should compute recommended movie from toWatch list`() = runTest {
        val userWithMovies = testUser.copy(
            statusMovies = mapOf(
                "100" to MovieStatus.ToWatch,
                "200" to MovieStatus.ToWatch,
            ),
        )
        coEvery { repository.getUsersByIds(listOf(testUserId)) } returns Result.success(listOf(userWithMovies))

        val movie1 = Movie().copy(id = 100, title = "Low Rated", voteAverage = 5.0f)
        val movie2 = Movie().copy(id = 200, title = "High Rated", voteAverage = 9.0f)
        coEvery { repository.getMoviesByIds(any()) } returns Result.success(listOf(movie1, movie2))

        createViewModel()
        advanceUntilIdle()

        val groupData = viewModel.state.value.selectedGroupData
        assertThat(groupData).isNotNull()
        assertThat(groupData?.recommendedMovie?.title).isEqualTo("High Rated")
        assertThat(groupData?.moviesToWatch).hasSize(2)
    }

    @Test
    fun `selectedGroupIndex returns 0 for empty groups`() {
        val state = GroupViewModel.GroupsState()
        assertThat(state.selectedGroupIndex).isEqualTo(0)
    }

    @Test
    fun `selectedGroupIndex returns correct index for selected group`() {
        val groups = listOf(
            Group().copy(id = "a"),
            Group().copy(id = "b"),
            Group().copy(id = "c"),
        )
        val state = GroupViewModel.GroupsState(groups = groups, selectedGroupId = "b")
        assertThat(state.selectedGroupIndex).isEqualTo(1)
    }

    @Test
    fun `selectedGroupIndex returns 0 when selected group not found`() {
        val groups = listOf(Group().copy(id = "a"))
        val state = GroupViewModel.GroupsState(groups = groups, selectedGroupId = "non-existent")
        assertThat(state.selectedGroupIndex).isEqualTo(0)
    }
}