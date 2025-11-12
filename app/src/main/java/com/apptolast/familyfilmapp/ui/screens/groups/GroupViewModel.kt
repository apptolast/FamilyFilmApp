package com.apptolast.familyfilmapp.ui.screens.groups

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.Movie
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.model.local.types.MovieStatus
import com.apptolast.familyfilmapp.repositories.Repository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(private val repository: Repository, private val auth: FirebaseAuth) :
    ViewModel() {

    // Single source of truth for all state
    private val _state = MutableStateFlow(GroupsState())
    val state: StateFlow<GroupsState> = _state.asStateFlow()

    init {
        observeGroups()
    }

    /**
     * Observes groups from Firebase and updates state reactively.
     * Uses stable Group IDs instead of indices to avoid race conditions.
     * Now also observes selectedGroupId changes to trigger member refresh.
     */
    private fun observeGroups() = viewModelScope.launch {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            _state.update { it.copy(errorMessage = "User not authenticated") }
            return@launch
        }

        combine(
            repository.getUserById(userId),
            repository.getMyGroups(userId),
            _state.map { it.selectedGroupId }.distinctUntilChanged(),
        ) { currentUser, groups, manualSelectedId ->
            Triple(currentUser, groups, manualSelectedId)
        }.catch { error ->
            Timber.e(error, "Error observing groups")
            _state.update { it.copy(errorMessage = error.message, isLoading = false) }
        }.collectLatest { (currentUser, groups, manualSelectedId) ->
            if (groups.isEmpty()) {
                _state.update {
                    it.copy(
                        currentUser = currentUser,
                        groups = emptyList(),
                        selectedGroupId = null,
                        groupUsers = emptyList(),
                        moviesToWatch = emptyList(),
                        moviesWatched = emptyList(),
                        isLoading = false,
                    )
                }
                return@collectLatest
            }

            // Determine which group to select
            val currentState = _state.value
            val selectedGroupId = determineSelectedGroupId(
                groups = groups,
                currentSelectedId = manualSelectedId,
                pendingGroupId = currentState.pendingGroupIdToSelect,
            )

            // Find the group
            val selectedGroup = groups.firstOrNull { it.id == selectedGroupId }
                ?: groups.first()

            Timber.d("Loading data for group: ${selectedGroup.name} (ID: ${selectedGroup.id})")

            // Load data for the selected group
            loadGroupData(
                groups = groups,
                selectedGroup = selectedGroup,
                currentUser = currentUser,
            )
        }
    }

    /**
     * Determines which group should be selected based on stable IDs.
     * This completely eliminates index-based race conditions.
     */
    private fun determineSelectedGroupId(
        groups: List<Group>,
        currentSelectedId: String?,
        pendingGroupId: String?,
    ): String? {
        // Priority 1: If there's a pending group to select, use it
        if (pendingGroupId != null) {
            val found = groups.firstOrNull { it.id == pendingGroupId }
            if (found != null) {
                Timber.d("Auto-selecting pending group: ${found.name}")
                return found.id
            }
        }

        // Priority 2: Keep current selection if it still exists
        if (currentSelectedId != null) {
            val found = groups.firstOrNull { it.id == currentSelectedId }
            if (found != null) {
                return found.id
            }
        }

        // Priority 3: Select first group
        return groups.firstOrNull()?.id
    }

    /**
     * Loads all data for the selected group.
     * Robustly handles member data collection and edge cases.
     */
    private suspend fun loadGroupData(groups: List<Group>, selectedGroup: Group, currentUser: User) {
        Timber.d("Loading data for group '${selectedGroup.name}' with ${selectedGroup.users.size} members")

        // Load group users - filter out nulls (deleted/non-existent users)
        val groupUsers = selectedGroup.users.mapNotNull { userId ->
            try {
                repository.getUserById(userId).firstOrNull()
            } catch (e: Exception) {
                Timber.e(e, "Error loading user $userId for group ${selectedGroup.name}")
                null
            }
        }

        if (groupUsers.size < selectedGroup.users.size) {
            val missingCount = selectedGroup.users.size - groupUsers.size
            Timber.w("Group '${selectedGroup.name}': $missingCount member(s) could not be loaded")
        }

        // Handle empty group case
        if (groupUsers.isEmpty()) {
            Timber.d("Group '${selectedGroup.name}' has no valid members")
            _state.update {
                it.copy(
                    currentUser = currentUser,
                    groups = groups,
                    selectedGroupId = selectedGroup.id,
                    groupUsers = emptyList(),
                    moviesToWatch = emptyList(),
                    moviesWatched = emptyList(),
                    pendingGroupIdToSelect = null,
                    isLoading = false,
                )
            }
            return
        }

        // Load movies to watch
        val toWatchMovieIds = groupUsers
            .flatMap { user ->
                user.statusMovies.filterValues { it == MovieStatus.ToWatch }.keys
            }
            .distinct()
            .map { it.toInt() }

        val moviesToWatch = if (toWatchMovieIds.isNotEmpty()) {
            repository.getMoviesByIds(toWatchMovieIds).getOrElse { error ->
                Timber.e(error, "Error loading movies to watch for group '${selectedGroup.name}'")
                emptyList()
            }
        } else {
            emptyList()
        }

        // Load watched movies
        val watchedMovieIds = groupUsers
            .flatMap { user ->
                user.statusMovies.filterValues { it == MovieStatus.Watched }.keys
            }
            .distinct()
            .map { it.toInt() }

        val moviesWatched = if (watchedMovieIds.isNotEmpty()) {
            repository.getMoviesByIds(watchedMovieIds).getOrElse { error ->
                Timber.e(error, "Error loading watched movies for group '${selectedGroup.name}'")
                emptyList()
            }
        } else {
            emptyList()
        }

        Timber.d(
            "Group '${selectedGroup.name}' loaded: " +
                "${groupUsers.size} users, ${moviesToWatch.size} to watch, ${moviesWatched.size} watched",
        )

        // Update state atomically with all loaded data
        _state.update {
            it.copy(
                currentUser = currentUser,
                groups = groups,
                selectedGroupId = selectedGroup.id,
                groupUsers = groupUsers,
                moviesToWatch = moviesToWatch,
                moviesWatched = moviesWatched,
                pendingGroupIdToSelect = null,
                isLoading = false,
            )
        }
    }

    // === Public Actions ===

    fun selectGroup(groupId: String) {
        Timber.d("User manually selected group: $groupId")
        _state.update {
            it.copy(
                selectedGroupId = groupId,
                pendingGroupIdToSelect = null,
                isLoading = true,
            )
        }
    }

    fun createGroup(groupName: String) = viewModelScope.launch {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            _state.update { it.copy(errorMessage = "User not authenticated") }
            return@launch
        }

        val currentUser = repository.getUserById(userId).firstOrNull()
        if (currentUser == null) {
            _state.update { it.copy(errorMessage = "User not found") }
            return@launch
        }

        repository.createGroup(
            groupName = groupName,
            user = currentUser,
            success = { createdGroup ->
                Timber.d("Group '$groupName' created successfully with ID: ${createdGroup.id}")
                // Mark this group ID as pending selection
                _state.update { it.copy(pendingGroupIdToSelect = createdGroup.id) }
            },
            failure = { error ->
                Timber.e(error, "Error creating group '$groupName'")
                _state.update {
                    it.copy(
                        errorMessage = error.message,
                        pendingGroupIdToSelect = null,
                    )
                }
            },
        )
    }

    fun changeGroupName(group: Group) {
        repository.updateGroup(
            group = group,
            success = {
                Timber.d("Group '${group.name}' updated successfully")
            },
            failure = { error ->
                Timber.e(error, "Error updating group '${group.name}'")
                _state.update { it.copy(errorMessage = error.message) }
            },
        )
    }

    fun deleteGroup(group: Group) {
        repository.deleteGroup(
            group = group,
            success = {
                Timber.d("Group '${group.name}' deleted successfully")
                // observeGroups() will automatically select another group
            },
            failure = { error ->
                Timber.e(error, "Error deleting group '${group.name}'")
                _state.update { it.copy(errorMessage = error.message) }
            },
        )
    }

    fun addMember(group: Group, email: String) {
        repository.addMember(
            group = group,
            email = email,
            success = {
                Timber.d("Member '$email' added to group '${group.name}'")
            },
            failure = { error ->
                Timber.e(error, "Error adding member '$email' to group '${group.name}'")
                _state.update { it.copy(errorMessage = error.message) }
            },
        )
    }

    fun deleteMember(group: Group, user: User) {
        repository.deleteMember(group, user)
        Timber.d("Member '${user.email}' removed from group '${group.name}'")
    }

    fun showDialog(dialog: GroupScreenDialogs) {
        _state.update { it.copy(showDialog = dialog) }
    }

    fun clearErrorMessage() {
        _state.update { it.copy(errorMessage = null) }
    }

    // === State Classes ===

    /**
     * Single, unified state for the Groups screen.
     * Uses stable Group IDs instead of indices for selection.
     * This eliminates all index-based race conditions.
     */
    data class GroupsState(
        // Backend data
        val currentUser: User = User(),
        val groups: List<Group> = emptyList(),
        val groupUsers: List<User> = emptyList(),
        val moviesToWatch: List<Movie> = emptyList(),
        val moviesWatched: List<Movie> = emptyList(),

        // UI state - using stable ID instead of index
        val selectedGroupId: String? = null,
        val showDialog: GroupScreenDialogs = GroupScreenDialogs.None,
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val textField: MutableState<String> = mutableStateOf(""),

        // Internal state for automatic selection
        val pendingGroupIdToSelect: String? = null,
    ) {
        /**
         * Returns the currently selected group based on ID.
         * This is inherently safe - no index validation needed.
         */
        val selectedGroup: Group?
            get() = groups.firstOrNull { it.id == selectedGroupId }

        /**
         * Returns the index of the currently selected group.
         * Always safe - derived from the stable ID.
         * This is only used for UI components that require an index (like TabRow).
         */
        val selectedGroupIndex: Int
            get() {
                if (groups.isEmpty()) return 0
                val index = groups.indexOfFirst { it.id == selectedGroupId }
                return if (index >= 0) index else 0
            }
    }

    sealed interface GroupScreenDialogs {
        data object CreateGroup : GroupScreenDialogs
        data class DeleteGroup(val group: Group) : GroupScreenDialogs
        data class ChangeGroupName(val group: Group) : GroupScreenDialogs
        data class AddMember(val group: Group) : GroupScreenDialogs
        data object None : GroupScreenDialogs
    }
}
