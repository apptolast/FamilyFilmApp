package com.apptolast.familyfilmapp.ui.screens.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptolast.familyfilmapp.analytics.AnalyticsEvents
import com.apptolast.familyfilmapp.analytics.AnalyticsTracker
import com.apptolast.familyfilmapp.analytics.UserProperties
import com.apptolast.familyfilmapp.firebase.CrashReporter
import com.apptolast.familyfilmapp.firebase.CurrentUserIdProvider
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.SyncState
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.repositories.Repository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GroupsViewModel(
    private val repository: Repository,
    private val analyticsTracker: AnalyticsTracker,
    private val crashReporter: CrashReporter,
    private val currentUserIdProvider: CurrentUserIdProvider,
) : ViewModel() {

    private val _state = MutableStateFlow(GroupsState())
    val state: StateFlow<GroupsState> = _state.asStateFlow()

    private var groupsObserverJob: Job? = null
    private val currentUserId: String? get() = currentUserIdProvider.currentUserId()

    init {
        if (currentUserId != null) {
            startObservingGroups()
            observeSyncState()
            observeRemovedFromGroup()
        } else {
            _state.update { it.copy(error = "User not authenticated", isLoading = false) }
        }
    }

    private fun observeSyncState() {
        viewModelScope.launch {
            repository.getSyncState().collectLatest { syncState ->
                _state.update { it.copy(syncState = syncState) }
            }
        }
    }

    private fun observeRemovedFromGroup() {
        viewModelScope.launch {
            repository.observeRemovedFromGroupEvents().collectLatest {
                _state.update { it.copy(removedFromGroup = true) }
            }
        }
    }

    private fun startObservingGroups() {
        val userId = currentUserId ?: return
        groupsObserverJob?.cancel()
        groupsObserverJob = viewModelScope.launch {
            repository.getMyGroups(userId).collectLatest { groups ->
                analyticsTracker.setUserProperty(UserProperties.GROUPS_COUNT, groups.size.toString())
                updateGroupSummaries(groups)
            }
        }
    }

    private suspend fun updateGroupSummaries(groups: List<Group>) {
        if (groups.isEmpty()) {
            _state.update {
                it.copy(
                    summaries = emptyList(),
                    isLoading = false,
                    error = null,
                )
            }
            return
        }

        val userIds = groups.flatMap { it.users }.distinct()
        val usersById = repository.getUsersByIds(userIds)
            .onFailure { crashReporter.recordException(it) }
            .getOrDefault(emptyList())
            .associateBy { it.id }

        val summaries = groups.map { group ->
            GroupSummary(
                group = group,
                members = group.users.mapNotNull { userId -> usersById[userId] },
            )
        }

        _state.update {
            it.copy(
                summaries = summaries,
                isLoading = false,
                error = null,
            )
        }
    }

    fun createGroup(groupName: String) = viewModelScope.launch {
        val userId = currentUserId
        if (userId == null) {
            _state.update { it.copy(error = "User not authenticated") }
            return@launch
        }
        _state.update { it.copy(isCreatingGroup = true, error = null) }
        repository.createGroup(groupName, userId)
            .onSuccess { createdGroup ->
                analyticsTracker.logEvent(AnalyticsEvents.GROUP_CREATED)
                _state.update {
                    it.copy(
                        isCreatingGroup = false,
                        createdGroupIdToOpen = createdGroup.id,
                    )
                }
            }
            .onFailure { error ->
                crashReporter.recordException(error)
                _state.update {
                    it.copy(
                        error = error.message ?: "Error creating group",
                        isCreatingGroup = false,
                    )
                }
            }
    }

    fun showDialog(dialog: GroupsScreenDialog) {
        _state.update { it.copy(showDialog = dialog) }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    fun onCreatedGroupNavigationHandled() {
        _state.update { it.copy(createdGroupIdToOpen = null) }
    }

    fun onRemovedFromGroupHandled() {
        _state.update { it.copy(removedFromGroup = false) }
    }

    data class GroupsState(
        val summaries: List<GroupSummary> = emptyList(),
        val isLoading: Boolean = true,
        val isCreatingGroup: Boolean = false,
        val error: String? = null,
        val showDialog: GroupsScreenDialog = GroupsScreenDialog.None,
        val createdGroupIdToOpen: String? = null,
        val syncState: SyncState = SyncState.Synced,
        val removedFromGroup: Boolean = false,
    )
}

data class GroupSummary(val group: Group, val members: List<User>)

sealed interface GroupsScreenDialog {
    data object CreateGroup : GroupsScreenDialog
    data object None : GroupsScreenDialog
}
