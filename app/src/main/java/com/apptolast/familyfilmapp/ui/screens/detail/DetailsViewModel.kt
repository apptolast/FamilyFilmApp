package com.apptolast.familyfilmapp.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.Media
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.model.local.types.MediaStatus
import com.apptolast.familyfilmapp.repositories.Repository
import com.apptolast.familyfilmapp.utils.DispatcherProvider
import com.google.firebase.auth.FirebaseAuth
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class DetailsViewModel @AssistedInject constructor(
    private val repository: Repository,
    private val auth: FirebaseAuth,
    private val dispatcherProvider: DispatcherProvider,
    @Assisted private val mediaId: Int,
) : ViewModel() {

    val state: StateFlow<DetailUiState>
        field: MutableStateFlow<DetailUiState> = MutableStateFlow(DetailUiState())

    init {
        viewModelScope.launch(dispatcherProvider.io()) {
            val userId = auth.uid
            if (userId == null) {
                Timber.e("User not authenticated")
                return@launch
            }

            awaitAll(
                async {
                    repository.getUserById(userId).collectLatest { user ->
                        state.update { it.copy(user = user) }
                    }
                },
                async {
                    repository.getMoviesByIds(arrayListOf(mediaId)).getOrNull()?.first()?.let { media ->
                        state.update { it.copy(media = media) }
                    }
                },
                async {
                    repository.getMyGroups(userId).collectLatest { groups ->
                        state.update { it.copy(groups = groups) }
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
            } catch (e: Exception) {
                Timber.e(e, "Error loading statuses for group: ${group.id}")
            }
        }
        state.update { it.copy(mediaGroupStatuses = mediaStatuses) }
    }

    fun onStatusButtonClick(status: MediaStatus) {
        state.update {
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
        state.update {
            val updated = if (selected) {
                it.selectedGroupIds + groupId
            } else {
                it.selectedGroupIds - groupId
            }
            it.copy(selectedGroupIds = updated)
        }
    }

    fun confirmMediaStatus() {
        val currentState = state.value
        val status = currentState.bottomSheetStatus ?: return
        val userId = auth.uid ?: return

        viewModelScope.launch(dispatcherProvider.io()) {
            val groupsToAdd = currentState.selectedGroupIds.filter { groupId ->
                currentState.mediaGroupStatuses[groupId] != status
            }
            val groupsToRemove = currentState.mediaGroupStatuses
                .filter { (_, existingStatus) -> existingStatus == status }
                .keys
                .filter { it !in currentState.selectedGroupIds }

            if (groupsToAdd.isNotEmpty()) {
                repository.updateMovieStatus(groupsToAdd, userId, mediaId, status)
                    .onSuccess { Timber.d("Status set in ${groupsToAdd.size} groups") }
                    .onFailure { Timber.e(it, "Error setting media status") }
            }

            if (groupsToRemove.isNotEmpty()) {
                repository.removeMovieStatus(groupsToRemove.toList(), userId, mediaId)
                    .onSuccess { Timber.d("Status removed from ${groupsToRemove.size} groups") }
                    .onFailure { Timber.e(it, "Error removing media status") }
            }

            loadMediaGroupStatuses(currentState.groups, userId)

            state.update { it.copy(showBottomSheet = false, bottomSheetStatus = null) }
        }
    }

    fun onBottomSheetDismiss() {
        state.update { it.copy(showBottomSheet = false, bottomSheetStatus = null) }
    }

    @AssistedFactory
    interface DetailsViewModelFactory {
        fun create(mediaId: Int): DetailsViewModel
    }

    companion object {
        fun provideFactory(assistedFactory: DetailsViewModelFactory, mediaId: Int): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T = assistedFactory.create(mediaId) as T
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

    val isToWatch: Boolean
        get() = mediaGroupStatuses.values.any { it == MediaStatus.ToWatch }

    val isWatched: Boolean
        get() = mediaGroupStatuses.values.any { it == MediaStatus.Watched }
}

@HiltViewModel
class DetailsViewModelFactoryProvider @Inject constructor(
    val detailsViewModelFactory: DetailsViewModel.DetailsViewModelFactory,
) : ViewModel()
