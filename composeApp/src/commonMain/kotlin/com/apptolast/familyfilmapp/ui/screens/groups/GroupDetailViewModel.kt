package com.apptolast.familyfilmapp.ui.screens.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptolast.familyfilmapp.analytics.AnalyticsEvents
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GroupDetailViewModel(
    private val repository: Repository,
    private val analyticsTracker: AnalyticsTracker,
    private val crashReporter: CrashReporter,
    private val currentUserIdProvider: CurrentUserIdProvider,
    private val cardStateStore: RecommendedCardStateDatasource,
    private val groupId: String,
) : ViewModel() {

    private val _state = MutableStateFlow(GroupDetailState())
    val state: StateFlow<GroupDetailState> = _state.asStateFlow()

    private var groupsObserverJob: Job? = null
    private var movieStatusObserverJob: Job? = null
    private val revealedRecommendationByGroup = MutableStateFlow<Map<String, String>>(emptyMap())
    private val currentUserId: String? get() = currentUserIdProvider.currentUserId()

    init {
        if (currentUserId != null) {
            startObservingGroup()
            observeSyncState()
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

    private fun startObservingGroup() {
        val userId = currentUserId ?: return
        groupsObserverJob?.cancel()
        groupsObserverJob = viewModelScope.launch {
            repository.getMyGroups(userId).collectLatest { groups ->
                val group = groups.firstOrNull { it.id == groupId }
                if (group == null) {
                    movieStatusObserverJob?.cancel()
                    _state.update {
                        it.copy(
                            groupData = null,
                            isLoading = false,
                            navigateBackAfterDelete = true,
                        )
                    }
                    return@collectLatest
                }
                loadGroupData(group)
            }
        }
    }

    private suspend fun loadGroupData(group: Group) {
        val membersResult = repository.getUsersByIds(group.users)
        val members = membersResult.getOrElse { error ->
            crashReporter.recordException(error)
            emptyList()
        }
        val sortedMembers = members.sortedBy { user ->
            if (user.id == group.ownerId) 0 else 1
        }
        observeMovieStatuses(group, sortedMembers)
    }

    private fun observeMovieStatuses(group: Group, members: List<User>) {
        movieStatusObserverJob?.cancel()
        movieStatusObserverJob = viewModelScope.launch {
            repository.getMovieStatusesByGroup(group.id).collectLatest { groupStatuses ->
                val toWatchStatuses = groupStatuses.filter { it.status == MediaStatus.ToWatch }
                val mediaToWatch = resolveMediaByType(toWatchStatuses)

                val watchedStatuses = groupStatuses.filter { it.status == MediaStatus.Watched }
                val mediaWatched = resolveMediaByType(watchedStatuses)

                val recommendedMedia = mediaToWatch.maxByOrNull { it.voteAverage }

                val revealedMediaId = revealedRecommendationByGroup.value[group.id]
                    ?: cardStateStore.getRevealedMediaId(group.id)?.also { stored ->
                        revealedRecommendationByGroup.update { it + (group.id to stored) }
                    }
                val isRecommendedRevealed = recommendedMedia != null &&
                    revealedMediaId == recommendedMedia.id.toString()

                val groupData = GroupData(
                    group = group,
                    members = members,
                    memberStats = buildMemberStats(members, groupStatuses),
                    mediaToWatch = mediaToWatch,
                    mediaWatched = mediaWatched,
                    recommendedMedia = recommendedMedia,
                    currentUserId = currentUserId ?: "",
                    isRecommendedRevealed = isRecommendedRevealed,
                )

                _state.update {
                    it.copy(
                        groupData = groupData,
                        isLoading = false,
                        error = null,
                        navigateBackAfterDelete = false,
                    )
                }
            }
        }
    }

    private fun buildMemberStats(
        members: List<User>,
        groupStatuses: List<GroupMediaStatus>,
    ): Map<String, MemberMediaStats> = members.associate { member ->
        val watchedCount = groupStatuses.count { status ->
            status.userId == member.id && status.status == MediaStatus.Watched
        }
        val toWatchCount = groupStatuses.count { status ->
            status.userId == member.id && status.status == MediaStatus.ToWatch
        }
        member.id to MemberMediaStats(
            watchedCount = watchedCount,
            toWatchCount = toWatchCount,
        )
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

    fun revealRecommendedCard() {
        val data = _state.value.groupData ?: return
        val mediaId = data.recommendedMedia?.id?.toString() ?: return
        cardStateStore.setRevealedMediaId(data.group.id, mediaId)
        revealedRecommendationByGroup.update { it + (data.group.id to mediaId) }
        _state.update { state ->
            state.copy(
                groupData = state.groupData?.copy(isRecommendedRevealed = true),
            )
        }
        analyticsTracker.logEvent(AnalyticsEvents.RECOMMENDED_CARD_REVEALED)
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

    fun updateGroupImage(group: Group, imageBytes: ByteArray) = viewModelScope.launch {
        _state.update { it.copy(isUploadingImage = true, error = null) }
        repository.updateGroupImage(group, imageBytes)
            .onSuccess {
                analyticsTracker.logEvent(AnalyticsEvents.GROUP_IMAGE_UPDATED)
                _state.update { it.copy(isUploadingImage = false) }
            }
            .onFailure { error ->
                crashReporter.recordException(error)
                _state.update {
                    it.copy(error = error.message ?: "Error updating group image", isUploadingImage = false)
                }
            }
    }

    fun addMember(groupId: String, identifier: String) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null) }
        repository.addMember(groupId, identifier)
            .onSuccess {
                analyticsTracker.logEvent(AnalyticsEvents.GROUP_MEMBER_ADDED)
                _state.update { it.copy(isLoading = false) }
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
                _state.update { it.copy(isLoading = false) }
            }
            .onFailure { error ->
                crashReporter.recordException(error)
                _state.update {
                    it.copy(error = error.message ?: "Error removing member", isLoading = false)
                }
            }
    }

    fun deleteGroup(groupId: String) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null) }
        repository.deleteGroup(groupId)
            .onSuccess {
                analyticsTracker.logEvent(AnalyticsEvents.GROUP_DELETED)
                cardStateStore.clearRevealedMediaId(groupId)
                revealedRecommendationByGroup.update { it - groupId }
                _state.update {
                    it.copy(
                        isLoading = false,
                        navigateBackAfterDelete = true,
                    )
                }
            }
            .onFailure { error ->
                crashReporter.recordException(error)
                _state.update {
                    it.copy(error = error.message ?: "Error deleting group", isLoading = false)
                }
            }
    }

    fun showDialog(dialog: GroupDetailDialog) {
        _state.update { it.copy(showDialog = dialog) }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    fun onBackNavigationHandled() {
        _state.update { it.copy(navigateBackAfterDelete = false) }
    }

    data class GroupDetailState(
        val groupData: GroupData? = null,
        val isLoading: Boolean = true,
        val isUploadingImage: Boolean = false,
        val error: String? = null,
        val showDialog: GroupDetailDialog = GroupDetailDialog.None,
        val syncState: SyncState = SyncState.Synced,
        val navigateBackAfterDelete: Boolean = false,
    )

    data class GroupData(
        val group: Group,
        val members: List<User>,
        val memberStats: Map<String, MemberMediaStats>,
        val mediaToWatch: List<Media>,
        val mediaWatched: List<Media>,
        val recommendedMedia: Media?,
        val currentUserId: String,
        val isRecommendedRevealed: Boolean = false,
    ) {
        val isCurrentUserOwner: Boolean get() = group.ownerId == currentUserId
    }
}

data class MemberMediaStats(val watchedCount: Int = 0, val toWatchCount: Int = 0)

sealed interface GroupDetailDialog {
    data class DeleteGroup(val group: Group) : GroupDetailDialog
    data class ChangeGroupName(val group: Group) : GroupDetailDialog
    data class AddMember(val group: Group) : GroupDetailDialog
    data object None : GroupDetailDialog
}
