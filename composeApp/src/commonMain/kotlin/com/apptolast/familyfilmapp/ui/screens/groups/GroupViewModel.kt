package com.apptolast.familyfilmapp.ui.screens.groups

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptolast.familyfilmapp.analytics.AnalyticsEvents
import com.apptolast.familyfilmapp.analytics.AnalyticsTracker
import com.apptolast.familyfilmapp.analytics.UserProperties
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GroupViewModel(
    private val repository: Repository,
    private val analyticsTracker: AnalyticsTracker,
    private val crashReporter: CrashReporter,
    private val currentUserIdProvider: CurrentUserIdProvider,
) : ViewModel() {

    private val _state = MutableStateFlow(GroupsState())
    val state: StateFlow<GroupsState> = _state.asStateFlow()

    private var groupsObserverJob: Job? = null
    private var movieStatusObserverJob: Job? = null

    private val currentUserId: String? get() = currentUserIdProvider.currentUserId()

    init {
        if (currentUserId != null) {
            startObservingGroups()
            observeSyncState()
        } else {
            _state.update { it.copy(error = "User not authenticated") }
        }
    }

    private fun observeSyncState() {
        viewModelScope.launch {
            repository.getSyncState().collectLatest { syncState ->
                _state.update { it.copy(syncState = syncState) }
            }
        }
    }

    private fun startObservingGroups() {
        val userId = currentUserId ?: return
        groupsObserverJob?.cancel()
        groupsObserverJob = viewModelScope.launch {
            repository.getMyGroups(userId).collect { groups ->
                analyticsTracker.setUserProperty(UserProperties.GROUPS_COUNT, groups.size.toString())
                handleGroupsUpdate(groups)
            }
        }
    }

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
        val groupToSelect = when {
            currentSelectedId == null -> groups.first()
            groups.any { it.id == currentSelectedId } -> groups.first { it.id == currentSelectedId }
            else -> groups.first()
        }

        _state.update { currentState ->
            val updatedGroupData = currentState.selectedGroupData?.let { data ->
                val updatedGroup = groups.firstOrNull { it.id == data.group.id }
                if (updatedGroup != null) data.copy(group = updatedGroup) else data
            }
            currentState.copy(groups = groups, selectedGroupData = updatedGroupData)
        }

        if (groupToSelect.id != currentSelectedId) {
            loadGroupData(groupToSelect.id)
        }
    }

    fun selectGroup(groupId: String) = viewModelScope.launch {
        val group = _state.value.groups.firstOrNull { it.id == groupId } ?: return@launch
        analyticsTracker.logEvent(AnalyticsEvents.GROUP_SWITCHED)
        _state.update { it.copy(selectedGroupId = groupId, isLoading = true, error = null) }
        loadGroupData(groupId)
    }

    fun createGroup(groupName: String) = viewModelScope.launch {
        val userId = currentUserId
        if (userId == null) {
            _state.update { it.copy(error = "User not authenticated") }
            return@launch
        }
        _state.update { it.copy(isLoading = true, error = null) }
        repository.createGroup(groupName, userId)
            .onSuccess { createdGroup ->
                analyticsTracker.logEvent(AnalyticsEvents.GROUP_CREATED)
                _state.update { it.copy(selectedGroupId = createdGroup.id, isLoading = false) }
                loadGroupData(createdGroup.id)
            }
            .onFailure { error ->
                crashReporter.recordException(error)
                _state.update {
                    it.copy(error = error.message ?: "Error creating group", isLoading = false)
                }
            }
    }

    fun deleteGroup(groupId: String) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null) }
        val currentGroups = _state.value.groups
        val currentIndex = currentGroups.indexOfFirst { it.id == groupId }
        val groupToSelectAfterDelete = when {
            currentIndex <= 0 && currentGroups.size > 1 -> currentGroups[1]
            currentIndex > 0 -> currentGroups[currentIndex - 1]
            else -> null
        }
        repository.deleteGroup(groupId)
            .onSuccess {
                analyticsTracker.logEvent(AnalyticsEvents.GROUP_DELETED)
                groupToSelectAfterDelete?.let { group ->
                    _state.update { it.copy(selectedGroupId = group.id) }
                    loadGroupData(group.id)
                }
                _state.update { it.copy(isLoading = false) }
            }
            .onFailure { error ->
                crashReporter.recordException(error)
                _state.update {
                    it.copy(error = error.message ?: "Error deleting group", isLoading = false)
                }
            }
    }

    fun addMember(groupId: String, email: String) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null) }
        repository.addMember(groupId, email)
            .onSuccess {
                analyticsTracker.logEvent(AnalyticsEvents.GROUP_MEMBER_ADDED)
                loadGroupData(groupId)
            }
            .onFailure { error ->
                crashReporter.recordException(error)
                _state.update {
                    it.copy(error = error.message ?: "Error adding member", isLoading = false)
                }
            }
    }

    fun removeMember(groupId: String, userId: String) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null) }
        repository.removeMember(groupId, userId)
            .onSuccess {
                analyticsTracker.logEvent(AnalyticsEvents.GROUP_MEMBER_REMOVED)
                loadGroupData(groupId)
            }
            .onFailure { error ->
                crashReporter.recordException(error)
                _state.update {
                    it.copy(error = error.message ?: "Error removing member", isLoading = false)
                }
            }
    }

    fun changeGroupName(group: Group) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null) }
        repository.updateGroup(group)
            .onSuccess {
                analyticsTracker.logEvent(AnalyticsEvents.GROUP_RENAMED)
                _state.update { it.copy(isLoading = false) }
            }
            .onFailure { error ->
                crashReporter.recordException(error)
                _state.update {
                    it.copy(error = error.message ?: "Error updating group name", isLoading = false)
                }
            }
    }

    private suspend fun loadGroupData(groupId: String) {
        val group = _state.value.groups.firstOrNull { it.id == groupId }
        if (group == null) {
            _state.update { it.copy(error = "Group not found", isLoading = false) }
            return
        }
        val membersResult = repository.getUsersByIds(group.users)
        val members = membersResult.getOrElse { error ->
            crashReporter.recordException(error)
            emptyList()
        }
        val sortedMembers = members.sortedBy { user ->
            if (user.id == group.ownerId) 0 else 1
        }
        observeMovieStatuses(groupId, sortedMembers)
    }

    private fun observeMovieStatuses(groupId: String, members: List<User>) {
        movieStatusObserverJob?.cancel()
        movieStatusObserverJob = viewModelScope.launch {
            repository.getMovieStatusesByGroup(groupId).collectLatest { groupStatuses ->
                val currentGroup = _state.value.groups.firstOrNull { it.id == groupId } ?: return@collectLatest

                val toWatchStatuses = groupStatuses.filter { it.status == MediaStatus.ToWatch }
                val mediaToWatch = resolveMediaByType(toWatchStatuses)

                val watchedStatuses = groupStatuses.filter { it.status == MediaStatus.Watched }
                val mediaWatched = resolveMediaByType(watchedStatuses)

                val recommendedMedia = mediaToWatch.maxByOrNull { it.voteAverage }

                val groupData = GroupData(
                    group = currentGroup,
                    members = members,
                    mediaToWatch = mediaToWatch,
                    mediaWatched = mediaWatched,
                    recommendedMedia = recommendedMedia,
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
        }
    }

    private suspend fun resolveMediaByType(statuses: List<GroupMediaStatus>): List<Media> {
        val movieIds = statuses.filter { it.mediaType == MediaType.MOVIE }.map { it.mediaId }.distinct()
        val tvIds = statuses.filter { it.mediaType == MediaType.TV_SHOW }.map { it.mediaId }.distinct()
        val movies = if (movieIds.isNotEmpty()) {
            repository.getMoviesByIds(movieIds).getOrElse { error ->
                crashReporter.recordException(error)
                emptyList()
            }
        } else {
            emptyList()
        }
        val tvShows = if (tvIds.isNotEmpty()) {
            repository.getTvShowsByIds(tvIds).getOrElse { error ->
                crashReporter.recordException(error)
                emptyList()
            }
        } else {
            emptyList()
        }
        return movies + tvShows
    }

    fun showDialog(dialog: GroupScreenDialogs) {
        _state.update { it.copy(showDialog = dialog) }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

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
        val selectedGroupIndex: Int
            get() {
                if (groups.isEmpty() || selectedGroupId == null) return 0
                val index = groups.indexOfFirst { it.id == selectedGroupId }
                return if (index >= 0) index else 0
            }
    }

    data class GroupData(
        val group: Group,
        val members: List<User>,
        val mediaToWatch: List<Media>,
        val mediaWatched: List<Media>,
        val recommendedMedia: Media?,
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
