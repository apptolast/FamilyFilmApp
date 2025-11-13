package com.apptolast.familyfilmapp.ui.screens.groups

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.Movie
import com.apptolast.familyfilmapp.model.local.SyncState
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.model.local.types.MovieStatus
import com.apptolast.familyfilmapp.repositories.Repository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Simplified GroupViewModel with explicit functions and no reactive complexity.
 * No combine(), no circular dependencies, no infinite loops.
 */
@HiltViewModel
class GroupViewModel @Inject constructor(private val repository: Repository, private val auth: FirebaseAuth) :
    ViewModel() {

    // Single source of truth - simple state
    private val _state = MutableStateFlow(GroupsState())
    val state: StateFlow<GroupsState> = _state.asStateFlow()

    private var groupsObserverJob: Job? = null
    private var currentUserId: String? = null

    init {
        currentUserId = auth.currentUser?.uid
        if (currentUserId != null) {
            // Start observing groups from Room (local database)
            startObservingGroups()
            // Start Firebase sync (syncs Firebase changes to Room)
            repository.startSync(currentUserId!!)
            // Observe sync state
            observeSyncState()
        } else {
            _state.update { it.copy(error = "User not authenticated") }
        }
    }

    /**
     * Observe synchronization state from repository.
     * Updates UI to reflect sync status (syncing, synced, error, offline).
     */
    private fun observeSyncState() {
        viewModelScope.launch {
            repository.getSyncState().collectLatest { syncState ->
                _state.update { it.copy(syncState = syncState) }
            }
        }
    }

    override fun onCleared() {
        // Stop Firebase sync when ViewModel is destroyed
        repository.stopSync()
        super.onCleared()
    }

    // ===== OBSERVING GROUPS =====

    /**
     * Start observing user's groups from repository.
     * This is a simple Flow collection without complex combine logic.
     */
    private fun startObservingGroups() {
        val userId = currentUserId ?: return

        groupsObserverJob?.cancel()
        groupsObserverJob = viewModelScope.launch {
            repository.getMyGroups(userId).collectLatest { groups ->
                Timber.d("Groups updated: ${groups.size} groups")
                handleGroupsUpdate(groups)
            }
        }
    }

    /**
     * Handle when groups list updates.
     * Logic: Keep selection if group still exists, otherwise select first.
     */
    private suspend fun handleGroupsUpdate(groups: List<Group>) {
        if (groups.isEmpty()) {
            _state.update {
                it.copy(
                    groups = emptyList(),
                    selectedGroupId = null,
                    selectedGroupData = null,
                    isLoading = false,
                )
            }
            return
        }

        val currentSelectedId = _state.value.selectedGroupId

        // Determine which group to select
        val groupToSelect = when {
            // If no selection yet, select first
            currentSelectedId == null -> groups.first()
            // If current selection still exists, keep it
            groups.any { it.id == currentSelectedId } -> groups.first { it.id == currentSelectedId }
            // If current selection was deleted, select first
            else -> groups.first()
        }

        _state.update { it.copy(groups = groups) }

        // Only load group data if selection actually changed
        if (groupToSelect.id != currentSelectedId) {
            Timber.d("Selection changed to: ${groupToSelect.name}")
            loadGroupData(groupToSelect.id)
        }
    }

    // ===== PUBLIC ACTIONS =====

    /**
     * Select a group and load its data.
     * Simple, explicit function - no reactive magic.
     */
    fun selectGroup(groupId: String) = viewModelScope.launch {
        Timber.d("User selected group: $groupId")

        val group = _state.value.groups.firstOrNull { it.id == groupId }
        if (group == null) {
            Timber.e("Group $groupId not found")
            return@launch
        }

        _state.update {
            it.copy(
                selectedGroupId = groupId,
                isLoading = true,
                error = null,
            )
        }

        loadGroupData(groupId)
    }

    /**
     * Create a new group and auto-select it.
     */
    fun createGroup(groupName: String) = viewModelScope.launch {
        val userId = currentUserId
        if (userId == null) {
            _state.update { it.copy(error = "User not authenticated") }
            return@launch
        }

        Timber.d("Creating group: $groupName")
        _state.update { it.copy(isLoading = true, error = null) }

        repository.createGroup(groupName, userId)
            .onSuccess { createdGroup ->
                Timber.d("Group created: ${createdGroup.id}")
                // The group will appear in the groups list via the observer
                // We'll auto-select it when it appears
                _state.update {
                    it.copy(
                        selectedGroupId = createdGroup.id,
                        isLoading = false,
                    )
                }
                // Load data for the new group
                loadGroupData(createdGroup.id)
            }
            .onFailure { error ->
                Timber.e(error, "Error creating group")
                _state.update {
                    it.copy(
                        error = error.message ?: "Error creating group",
                        isLoading = false,
                    )
                }
            }
    }

    /**
     * Delete a group and select the previous one.
     */
    fun deleteGroup(groupId: String) = viewModelScope.launch {
        Timber.d("Deleting group: $groupId")
        _state.update { it.copy(isLoading = true, error = null) }

        // Find the previous group before deleting
        val currentGroups = _state.value.groups
        val currentIndex = currentGroups.indexOfFirst { it.id == groupId }
        val groupToSelectAfterDelete = when {
            currentIndex <= 0 && currentGroups.size > 1 -> currentGroups[1]
            currentIndex > 0 -> currentGroups[currentIndex - 1]
            else -> null
        }

        repository.deleteGroup(groupId)
            .onSuccess {
                Timber.d("Group deleted successfully")
                // The observer will update the groups list automatically
                // Select the previous group if available
                groupToSelectAfterDelete?.let { group ->
                    _state.update { it.copy(selectedGroupId = group.id) }
                    loadGroupData(group.id)
                }
                _state.update { it.copy(isLoading = false) }
            }
            .onFailure { error ->
                Timber.e(error, "Error deleting group")
                _state.update {
                    it.copy(
                        error = error.message ?: "Error deleting group",
                        isLoading = false,
                    )
                }
            }
    }

    /**
     * Add a member to a group.
     */
    fun addMember(groupId: String, email: String) = viewModelScope.launch {
        Timber.d("Adding member $email to group $groupId")
        _state.update { it.copy(isLoading = true, error = null) }

        repository.addMember(groupId, email)
            .onSuccess {
                Timber.d("Member added successfully")
                // Reload group data to show new member
                loadGroupData(groupId)
            }
            .onFailure { error ->
                Timber.e(error, "Error adding member")
                _state.update {
                    it.copy(
                        error = error.message ?: "Error adding member",
                        isLoading = false,
                    )
                }
            }
    }

    /**
     * Remove a member from a group.
     */
    fun removeMember(groupId: String, userId: String) = viewModelScope.launch {
        Timber.d("Removing member $userId from group $groupId")
        _state.update { it.copy(isLoading = true, error = null) }

        repository.removeMember(groupId, userId)
            .onSuccess {
                Timber.d("Member removed successfully")
                // Reload group data to reflect removal
                loadGroupData(groupId)
            }
            .onFailure { error ->
                Timber.e(error, "Error removing member")
                _state.update {
                    it.copy(
                        error = error.message ?: "Error removing member",
                        isLoading = false,
                    )
                }
            }
    }

    /**
     * Change group name.
     */
    fun changeGroupName(group: Group) = viewModelScope.launch {
        Timber.d("Updating group name: ${group.name}")
        _state.update { it.copy(isLoading = true, error = null) }

        repository.updateGroup(group)
            .onSuccess {
                Timber.d("Group name updated successfully")
                // The observer will update the groups list automatically
                _state.update { it.copy(isLoading = false) }
            }
            .onFailure { error ->
                Timber.e(error, "Error updating group name")
                _state.update {
                    it.copy(
                        error = error.message ?: "Error updating group name",
                        isLoading = false,
                    )
                }
            }
    }

    // ===== LOADING GROUP DATA =====

    /**
     * Load all data for a specific group.
     * This is an explicit, controlled function - no automatic triggering.
     */
    private suspend fun loadGroupData(groupId: String) {
        Timber.d("Loading data for group: $groupId")

        val group = _state.value.groups.firstOrNull { it.id == groupId }
        if (group == null) {
            Timber.e("Group $groupId not found in state")
            _state.update {
                it.copy(
                    error = "Group not found",
                    isLoading = false,
                )
            }
            return
        }

        // Load members using batch query
        val membersResult = repository.getUsersByIds(group.users)
        val members = membersResult.getOrElse { error ->
            Timber.e(error, "Error loading group members")
            emptyList()
        }

        if (members.isEmpty() && group.users.isNotEmpty()) {
            Timber.w("No members loaded for group ${group.name}")
        }

        // Sort members: owner first, then others
        val sortedMembers = members.sortedBy { user ->
            if (user.id == group.ownerId) 0 else 1
        }

        // Load movies to watch
        val toWatchMovieIds = sortedMembers
            .flatMap { user ->
                user.statusMovies
                    .filterValues { it == MovieStatus.ToWatch }
                    .keys
            }
            .distinct()
            .mapNotNull { it.toIntOrNull() }

        val moviesToWatch = if (toWatchMovieIds.isNotEmpty()) {
            repository.getMoviesByIds(toWatchMovieIds).getOrElse { error ->
                Timber.e(error, "Error loading movies to watch")
                emptyList()
            }
        } else {
            emptyList()
        }

        // Load watched movies
        val watchedMovieIds = sortedMembers
            .flatMap { user ->
                user.statusMovies
                    .filterValues { it == MovieStatus.Watched }
                    .keys
            }
            .distinct()
            .mapNotNull { it.toIntOrNull() }

        val moviesWatched = if (watchedMovieIds.isNotEmpty()) {
            repository.getMoviesByIds(watchedMovieIds).getOrElse { error ->
                Timber.e(error, "Error loading watched movies")
                emptyList()
            }
        } else {
            emptyList()
        }

        // Find recommended movie (highest popularity from toWatch)
        val recommendedMovie = moviesToWatch.maxByOrNull { it.voteAverage }

        Timber.d(
            "Loaded group '${group.name}': ${sortedMembers.size} members, " +
                "${moviesToWatch.size} to watch, ${moviesWatched.size} watched",
        )

        // Update state with loaded data
        val groupData = GroupData(
            group = group,
            members = sortedMembers,
            moviesToWatch = moviesToWatch,
            moviesWatched = moviesWatched,
            recommendedMovie = recommendedMovie,
            currentUserId = currentUserId ?: "",
        )

        _state.update {
            it.copy(
                selectedGroupId = groupId,
                selectedGroupData = groupData,
                isLoading = false,
                error = null,
            )
        }
    }

    // ===== UI HELPERS =====

    fun showDialog(dialog: GroupScreenDialogs) {
        _state.update { it.copy(showDialog = dialog) }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    // ===== STATE =====

    /**
     * Simple state - no computed properties, no circular dependencies.
     */
    data class GroupsState(
        val groups: List<Group> = emptyList(),
        val selectedGroupId: String? = null,
        val selectedGroupData: GroupData? = null,
        val isLoading: Boolean = true,
        val error: String? = null,
        val showDialog: GroupScreenDialogs = GroupScreenDialogs.None,
        val textField: MutableState<String> = mutableStateOf(""),
        val syncState: SyncState = SyncState.Synced,
    ) {
        /**
         * Computed index for TabRow - derived on demand, not stored.
         */
        val selectedGroupIndex: Int
            get() {
                if (groups.isEmpty() || selectedGroupId == null) return 0
                val index = groups.indexOfFirst { it.id == selectedGroupId }
                return if (index >= 0) index else 0
            }
    }

    /**
     * All data for a selected group bundled together.
     */
    data class GroupData(
        val group: Group,
        val members: List<User>,
        val moviesToWatch: List<Movie>,
        val moviesWatched: List<Movie>,
        val recommendedMovie: Movie?,
        val currentUserId: String,
    )

    sealed interface GroupScreenDialogs {
        data object CreateGroup : GroupScreenDialogs
        data class DeleteGroup(val group: Group) : GroupScreenDialogs
        data class ChangeGroupName(val group: Group) : GroupScreenDialogs
        data class AddMember(val group: Group) : GroupScreenDialogs
        data object None : GroupScreenDialogs
    }
}
