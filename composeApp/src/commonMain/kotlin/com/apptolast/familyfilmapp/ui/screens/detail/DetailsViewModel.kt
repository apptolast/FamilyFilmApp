package com.apptolast.familyfilmapp.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptolast.familyfilmapp.analytics.AnalyticsEvents
import com.apptolast.familyfilmapp.analytics.AnalyticsTracker
import com.apptolast.familyfilmapp.firebase.CrashReporter
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.Media
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.model.local.types.MediaStatus
import com.apptolast.familyfilmapp.model.local.types.MediaType
import com.apptolast.familyfilmapp.repositories.Repository
import com.apptolast.familyfilmapp.ui.screens.home.toAnalyticsContentType
import com.apptolast.familyfilmapp.utils.DispatcherProvider
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Detail screen ViewModel. The legacy version used Hilt assisted injection
 * (`@AssistedInject` + `@AssistedFactory`) to take the route payload
 * (mediaId + mediaType) as constructor parameters. With Koin the screen
 * passes them through `koinViewModel<DetailsViewModel>(parameters = {
 * parametersOf(mediaId, mediaType) })`.
 */
class DetailsViewModel(
    private val repository: Repository,
    private val dispatcherProvider: DispatcherProvider,
    private val analyticsTracker: AnalyticsTracker,
    private val crashReporter: CrashReporter,
    private val mediaId: Int,
    private val mediaType: MediaType,
) : ViewModel() {

    private val _state = MutableStateFlow(DetailUiState())
    val state: StateFlow<DetailUiState> = _state.asStateFlow()

    private val currentUserId: String? get() = Firebase.auth.currentUser?.uid

    init {
        analyticsTracker.logEvent(
            AnalyticsEvents.Standard.EVENT_VIEW_ITEM,
            mapOf(
                AnalyticsEvents.Standard.PARAM_CONTENT_TYPE to mediaType.toAnalyticsContentType(),
                AnalyticsEvents.Standard.PARAM_ITEM_ID to mediaId.toString(),
            ),
        )
        viewModelScope.launch(dispatcherProvider.io()) {
            val userId = currentUserId ?: return@launch
            awaitAll(
                async {
                    repository.getUserById(userId).collectLatest { user ->
                        _state.update { it.copy(user = user) }
                    }
                },
                async {
                    val result = when (mediaType) {
                        MediaType.MOVIE -> repository.getMoviesByIds(listOf(mediaId))
                        MediaType.TV_SHOW -> repository.getTvShowsByIds(listOf(mediaId))
                    }
                    result.getOrNull()?.firstOrNull()?.let { media ->
                        _state.update { it.copy(media = media) }
                    }
                },
                async {
                    repository.getMyGroups(userId).collectLatest { groups ->
                        _state.update { it.copy(groups = groups) }
                        loadMediaGroupStatuses(groups, userId)
                    }
                },
            )
        }
    }

    private suspend fun loadMediaGroupStatuses(groups: List<Group>, userId: String) {
        val mediaStatuses = mutableMapOf<String, MediaStatus>()
        for (group in groups) {
            try {
                val statuses = repository.getMovieStatusesByGroup(group.id).first()
                val statusForMedia = statuses.firstOrNull { it.mediaId == mediaId && it.userId == userId }
                if (statusForMedia != null) {
                    mediaStatuses[group.id] = statusForMedia.status
                }
            } catch (e: Throwable) {
                crashReporter.recordException(e)
            }
        }
        _state.update { it.copy(mediaGroupStatuses = mediaStatuses) }
    }

    fun onStatusButtonClick(status: MediaStatus) {
        _state.update {
            it.copy(
                bottomSheetStatus = status,
                showBottomSheet = true,
                selectedGroupIds = it.groups
                    .filter { group -> it.mediaGroupStatuses[group.id] == status }
                    .map { group -> group.id }
                    .toSet(),
            )
        }
    }

    fun onGroupSelectionChanged(groupId: String, selected: Boolean) {
        _state.update {
            val updated = if (selected) {
                it.selectedGroupIds + groupId
            } else {
                it.selectedGroupIds - groupId
            }
            it.copy(selectedGroupIds = updated)
        }
    }

    fun confirmMediaStatus() {
        val currentState = _state.value
        val status = currentState.bottomSheetStatus ?: return
        val userId = currentUserId ?: return

        viewModelScope.launch(dispatcherProvider.io()) {
            val groupsToAdd = currentState.selectedGroupIds.filter { groupId ->
                currentState.mediaGroupStatuses[groupId] != status
            }
            val groupsToRemove = currentState.mediaGroupStatuses
                .filter { (_, existingStatus) -> existingStatus == status }
                .keys
                .filter { it !in currentState.selectedGroupIds }

            if (groupsToAdd.isNotEmpty()) {
                repository.updateMovieStatus(groupsToAdd, userId, mediaId, status, mediaType)
                    .onSuccess { logStatusAdded(status, groupsToAdd.size) }
                    .onFailure { crashReporter.recordException(it) }
            }

            if (groupsToRemove.isNotEmpty()) {
                repository.removeMovieStatus(groupsToRemove.toList(), userId, mediaId, mediaType)
                    .onSuccess {
                        analyticsTracker.logEvent(
                            AnalyticsEvents.REMOVE_FROM_LIST,
                            mapOf(
                                AnalyticsEvents.Standard.PARAM_CONTENT_TYPE to mediaType.toAnalyticsContentType(),
                                AnalyticsEvents.Standard.PARAM_ITEM_ID to mediaId.toString(),
                                AnalyticsEvents.Param.PREVIOUS_STATUS to status.name,
                                AnalyticsEvents.Param.GROUP_COUNT to groupsToRemove.size.toLong(),
                            ),
                        )
                    }
                    .onFailure { crashReporter.recordException(it) }
            }

            loadMediaGroupStatuses(currentState.groups, userId)
            _state.update { it.copy(showBottomSheet = false, bottomSheetStatus = null) }
        }
    }

    fun onBottomSheetDismiss() {
        _state.update { it.copy(showBottomSheet = false, bottomSheetStatus = null) }
    }

    private fun logStatusAdded(status: MediaStatus, groupCount: Int) {
        val params = mapOf(
            AnalyticsEvents.Standard.PARAM_CONTENT_TYPE to mediaType.toAnalyticsContentType(),
            AnalyticsEvents.Standard.PARAM_ITEM_ID to mediaId.toString(),
            AnalyticsEvents.Param.GROUP_COUNT to groupCount.toLong(),
        )
        when (status) {
            MediaStatus.ToWatch -> analyticsTracker.logEvent(AnalyticsEvents.Standard.EVENT_ADD_TO_WISHLIST, params)
            MediaStatus.Watched -> analyticsTracker.logEvent(AnalyticsEvents.MARK_AS_WATCHED, params)
        }
    }
}

data class DetailUiState(
    val user: User,
    val media: Media,
    val groups: List<Group>,
    val mediaGroupStatuses: Map<String, MediaStatus>,
    val showBottomSheet: Boolean,
    val bottomSheetStatus: MediaStatus?,
    val selectedGroupIds: Set<String>,
) {
    constructor() : this(
        user = User(),
        media = Media(),
        groups = emptyList(),
        mediaGroupStatuses = emptyMap(),
        showBottomSheet = false,
        bottomSheetStatus = null,
        selectedGroupIds = emptySet(),
    )

    val isToWatch: Boolean get() = mediaGroupStatuses.values.any { it == MediaStatus.ToWatch }
    val isWatched: Boolean get() = mediaGroupStatuses.values.any { it == MediaStatus.Watched }
}
