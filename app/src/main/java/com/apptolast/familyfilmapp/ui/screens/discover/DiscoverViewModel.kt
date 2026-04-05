package com.apptolast.familyfilmapp.ui.screens.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptolast.familyfilmapp.exceptions.CustomException
import com.apptolast.familyfilmapp.model.local.Media
import com.apptolast.familyfilmapp.model.local.types.MediaFilter
import com.apptolast.familyfilmapp.model.local.types.MediaStatus
import com.apptolast.familyfilmapp.repositories.Repository
import com.apptolast.familyfilmapp.utils.DispatcherProvider
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val repository: Repository,
    private val auth: FirebaseAuth,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {

    val uiState: StateFlow<DiscoverUiState>
        field: MutableStateFlow<DiscoverUiState> = MutableStateFlow(DiscoverUiState())

    private var currentPage = 1
    private var markedMediaIds: Set<Int> = emptySet()

    init {
        loadUser()
        loadGroups()
        loadMedia()
    }

    private fun loadUser() = viewModelScope.launch {
        val userId = auth.uid
        if (userId == null) {
            Timber.w("User not authenticated")
            triggerError("User not authenticated")
            return@launch
        }

        repository.getUserById(userId).collectLatest { user ->
            uiState.update { it.copy(user = user) }
        }
    }

    private fun loadGroups() = viewModelScope.launch {
        val userId = auth.uid ?: return@launch

        repository.getMyGroups(userId).collectLatest { groups ->
            uiState.update {
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
        uiState.update { it.copy(isLoading = true) }

        val userId = auth.uid
        if (userId != null) {
            markedMediaIds = try {
                repository.getAllMarkedMovieIdsForUser(userId).toSet()
            } catch (e: Exception) {
                Timber.e(e, "Error loading marked media IDs")
                emptySet()
            }
        }

        repository.getPopularMoviesList(page = currentPage)
            .onSuccess { mediaList ->
                val popularMedia = mediaList.filter { media ->
                    media.id !in markedMediaIds
                }

                uiState.update {
                    it.copy(
                        mediaList = popularMedia,
                        isLoading = false,
                        currentMediaIndex = 0,
                    )
                }

                Timber.d("Loaded ${popularMedia.size} media items for discovery")
            }
            .onFailure { e ->
                Timber.e(e, "Error loading media")
                triggerError(e.message ?: "Error loading media")
                uiState.update { it.copy(isLoading = false) }
            }
    }

    fun toggleGroupSelection(groupId: String) {
        uiState.update {
            val updated = if (groupId in it.selectedGroupIds) {
                it.selectedGroupIds - groupId
            } else {
                it.selectedGroupIds + groupId
            }
            it.copy(selectedGroupIds = updated)
        }
    }

    fun markAsWatched() = viewModelScope.launch(dispatcherProvider.io()) {
        val currentMedia = uiState.value.currentMedia ?: return@launch
        updateMediaStatus(currentMedia, MediaStatus.Watched)
        moveToNext()
    }

    fun markAsWantToWatch() = viewModelScope.launch(dispatcherProvider.io()) {
        val currentMedia = uiState.value.currentMedia ?: return@launch
        updateMediaStatus(currentMedia, MediaStatus.ToWatch)
        moveToNext()
    }

    fun skipMedia() {
        moveToNext()
    }

    private fun moveToNext() {
        uiState.update {
            it.copy(currentMediaIndex = it.currentMediaIndex + 1)
        }

        if (uiState.value.currentMediaIndex >= uiState.value.mediaList.size - 3) {
            loadMoreMedia()
        }
    }

    private fun loadMoreMedia() = viewModelScope.launch(dispatcherProvider.io()) {
        Timber.d("Loading more media...")

        currentPage++
        repository.getPopularMoviesList(page = currentPage)
            .onSuccess { mediaList ->
                val newMedia = mediaList.filter { media ->
                    media.id !in markedMediaIds &&
                        !uiState.value.mediaList.any { it.id == media.id }
                }

                uiState.update {
                    it.copy(mediaList = it.mediaList + newMedia)
                }

                Timber.d("Loaded ${newMedia.size} additional media items")
            }
            .onFailure { e ->
                Timber.e(e, "Error loading more media")
            }
    }

    private suspend fun updateMediaStatus(media: Media, status: MediaStatus) {
        try {
            val userId = auth.uid ?: return
            val selectedGroups = uiState.value.selectedGroupIds.toList()

            if (selectedGroups.isEmpty()) {
                triggerError("Select at least one group")
                return
            }

            repository.updateMovieStatus(selectedGroups, userId, media.id, status)
                .onSuccess {
                    markedMediaIds = markedMediaIds + media.id
                    Timber.d("Media ${media.title} marked as $status in ${selectedGroups.size} groups")
                }
                .onFailure { e ->
                    Timber.e(e, "Error updating media status")
                    triggerError("Error updating media status")
                }
        } catch (e: Exception) {
            Timber.e(e, "Error updating media status")
            triggerError("Error updating media status")
        }
    }

    private fun triggerError(message: String) {
        uiState.update { it.copy(errorMessage = CustomException.GenericException(message)) }
    }

    fun setMediaFilter(filter: MediaFilter) {
        uiState.update { it.copy(selectedFilter = filter, mediaList = emptyList(), currentMediaIndex = 0) }
        currentPage = 1
        loadMedia()
    }

    fun clearError() {
        uiState.update { it.copy(errorMessage = null) }
    }
}
