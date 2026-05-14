package com.apptolast.familyfilmapp.ui.screens.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptolast.familyfilmapp.analytics.AnalyticsEvents
import com.apptolast.familyfilmapp.analytics.AnalyticsTracker
import com.apptolast.familyfilmapp.exceptions.CustomException
import com.apptolast.familyfilmapp.firebase.CrashReporter
import com.apptolast.familyfilmapp.model.local.Media
import com.apptolast.familyfilmapp.model.local.types.MediaFilter
import com.apptolast.familyfilmapp.model.local.types.MediaStatus
import com.apptolast.familyfilmapp.network.TmdbLocaleManager
import com.apptolast.familyfilmapp.repositories.Repository
import com.apptolast.familyfilmapp.ui.screens.home.toAnalyticsContentType
import com.apptolast.familyfilmapp.utils.DispatcherProvider
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DiscoverViewModel(
    private val repository: Repository,
    private val dispatcherProvider: DispatcherProvider,
    private val tmdbLocaleManager: TmdbLocaleManager,
    private val analyticsTracker: AnalyticsTracker,
    private val crashReporter: CrashReporter,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DiscoverUiState())
    val uiState: StateFlow<DiscoverUiState> = _uiState.asStateFlow()

    private val currentUserId: String? get() = Firebase.auth.currentUser?.uid

    private var currentPage = 1
    private var markedMediaIds: Set<Int> = emptySet()

    init {
        loadUser()
        loadGroups()
        loadMedia()
        observeAdultContentChanges()
    }

    private fun observeAdultContentChanges() = viewModelScope.launch {
        tmdbLocaleManager.includeAdult
            .drop(1)
            .collect {
                currentPage = 1
                _uiState.update { it.copy(mediaList = emptyList(), currentMediaIndex = 0) }
                loadMedia()
            }
    }

    private fun loadUser() = viewModelScope.launch {
        val userId = currentUserId
        if (userId == null) {
            triggerError("User not authenticated")
            return@launch
        }
        repository.getUserById(userId).collectLatest { user ->
            _uiState.update { it.copy(user = user) }
        }
    }

    private fun loadGroups() = viewModelScope.launch {
        val userId = currentUserId ?: return@launch
        repository.getMyGroups(userId).collectLatest { groups ->
            _uiState.update {
                it.copy(
                    groups = groups,
                    selectedGroupIds = if (it.selectedGroupIds.isEmpty()) {
                        groups.map { g -> g.id }.toSet()
                    } else {
                        it.selectedGroupIds
                    },
                )
            }
        }
    }

    private fun loadMedia() = viewModelScope.launch(dispatcherProvider.io()) {
        _uiState.update { it.copy(isLoading = true) }

        val userId = currentUserId
        if (userId != null) {
            markedMediaIds = try {
                repository.getAllMarkedMovieIdsForUser(userId).toSet()
            } catch (e: Throwable) {
                crashReporter.recordException(e)
                emptySet()
            }
        }

        fetchPopularMedia(currentPage)
            .onSuccess { mediaList ->
                val popularMedia = mediaList.filter { it.id !in markedMediaIds }
                _uiState.update {
                    it.copy(
                        mediaList = popularMedia,
                        isLoading = false,
                        currentMediaIndex = 0,
                    )
                }
            }
            .onFailure { e ->
                crashReporter.recordException(e)
                triggerError(e.message ?: "Error loading media")
                _uiState.update { it.copy(isLoading = false) }
            }
    }

    private suspend fun fetchPopularMedia(page: Int): Result<List<Media>> =
        when (_uiState.value.selectedFilter) {
            MediaFilter.ALL -> {
                val movies = repository.getPopularMoviesList(page).getOrDefault(emptyList())
                val tvShows = repository.getPopularTvShowsList(page).getOrDefault(emptyList())
                Result.success((movies + tvShows).sortedByDescending { it.popularity })
            }
            MediaFilter.MOVIES -> repository.getPopularMoviesList(page)
            MediaFilter.TV_SHOWS -> repository.getPopularTvShowsList(page)
        }

    fun toggleGroupSelection(groupId: String) {
        _uiState.update {
            val updated = if (groupId in it.selectedGroupIds) {
                it.selectedGroupIds - groupId
            } else {
                it.selectedGroupIds + groupId
            }
            it.copy(selectedGroupIds = updated)
        }
    }

    fun markAsWatched() = viewModelScope.launch(dispatcherProvider.io()) {
        val currentMedia = _uiState.value.currentMedia ?: return@launch
        updateMediaStatus(currentMedia, MediaStatus.Watched)
        moveToNext()
    }

    fun markAsWantToWatch() = viewModelScope.launch(dispatcherProvider.io()) {
        val currentMedia = _uiState.value.currentMedia ?: return@launch
        updateMediaStatus(currentMedia, MediaStatus.ToWatch)
        moveToNext()
    }

    fun skipMedia() {
        moveToNext()
    }

    private fun moveToNext() {
        _uiState.update { it.copy(currentMediaIndex = it.currentMediaIndex + 1) }
        if (_uiState.value.currentMediaIndex >= _uiState.value.mediaList.size - 3) {
            loadMoreMedia()
        }
    }

    private fun loadMoreMedia() = viewModelScope.launch(dispatcherProvider.io()) {
        currentPage++
        fetchPopularMedia(currentPage)
            .onSuccess { mediaList ->
                val newMedia = mediaList.filter { media ->
                    media.id !in markedMediaIds && _uiState.value.mediaList.none { it.id == media.id }
                }
                _uiState.update { it.copy(mediaList = it.mediaList + newMedia) }
            }
            .onFailure { e -> crashReporter.recordException(e) }
    }

    private suspend fun updateMediaStatus(media: Media, status: MediaStatus) {
        try {
            val userId = currentUserId ?: return
            val selectedGroups = _uiState.value.selectedGroupIds.toList()
            if (selectedGroups.isEmpty()) {
                triggerError("Select at least one group")
                return
            }
            repository.updateMovieStatus(selectedGroups, userId, media.id, status, media.mediaType)
                .onSuccess {
                    markedMediaIds = markedMediaIds + media.id
                    logDiscoverStatus(media, status, selectedGroups.size)
                }
                .onFailure { e ->
                    crashReporter.recordException(e)
                    triggerError("Error updating media status")
                }
        } catch (e: Throwable) {
            crashReporter.recordException(e)
            triggerError("Error updating media status")
        }
    }

    private fun triggerError(message: String) {
        _uiState.update { it.copy(errorMessage = CustomException.GenericException(message)) }
    }

    fun setMediaFilter(filter: MediaFilter) {
        _uiState.update {
            it.copy(selectedFilter = filter, mediaList = emptyList(), currentMediaIndex = 0)
        }
        currentPage = 1
        loadMedia()
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun logDiscoverStatus(media: Media, status: MediaStatus, groupCount: Int) {
        val params = mapOf(
            AnalyticsEvents.Standard.PARAM_CONTENT_TYPE to media.mediaType.toAnalyticsContentType(),
            AnalyticsEvents.Standard.PARAM_ITEM_ID to media.id.toString(),
            AnalyticsEvents.Param.SOURCE to "discover",
            AnalyticsEvents.Param.GROUP_COUNT to groupCount.toLong(),
        )
        when (status) {
            MediaStatus.ToWatch -> analyticsTracker.logEvent(AnalyticsEvents.Standard.EVENT_ADD_TO_WISHLIST, params)
            MediaStatus.Watched -> analyticsTracker.logEvent(AnalyticsEvents.MARK_AS_WATCHED, params)
        }
    }
}
