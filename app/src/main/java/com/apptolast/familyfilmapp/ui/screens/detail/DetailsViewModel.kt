package com.apptolast.familyfilmapp.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.GroupMovieStatus
import com.apptolast.familyfilmapp.model.local.Movie
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.model.local.types.MovieStatus
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
    @Assisted private val movieId: Int,
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
                    repository.getMoviesByIds(arrayListOf(movieId)).getOrNull()?.first()?.let { movie ->
                        state.update { it.copy(movie = movie) }
                    }
                },
                async {
                    repository.getMyGroups(userId).collectLatest { groups ->
                        state.update { it.copy(groups = groups) }
                        // Load statuses for each group to know where this movie is assigned
                        loadMovieGroupStatuses(groups, userId)
                    }
                },
            )
        }
    }

    private suspend fun loadMovieGroupStatuses(groups: List<Group>, userId: String) {
        val movieStatuses = mutableMapOf<String, MovieStatus>()
        for (group in groups) {
            try {
                val statuses = repository.getMovieStatusesByGroup(group.id).first()
                val statusForMovie = statuses.firstOrNull { it.movieId == movieId && it.userId == userId }
                if (statusForMovie != null) {
                    movieStatuses[group.id] = statusForMovie.status
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading statuses for group: ${group.id}")
            }
        }
        state.update { it.copy(movieGroupStatuses = movieStatuses) }
    }

    fun onStatusButtonClick(status: MovieStatus) {
        state.update {
            it.copy(
                bottomSheetStatus = status,
                showBottomSheet = true,
                selectedGroupIds = it.groups
                    .filter { group -> it.movieGroupStatuses[group.id] == status }
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

    fun confirmMovieStatus() {
        val currentState = state.value
        val status = currentState.bottomSheetStatus ?: return
        val userId = auth.uid ?: return

        viewModelScope.launch(dispatcherProvider.io()) {
            // Groups to add the status to
            val groupsToAdd = currentState.selectedGroupIds.filter { groupId ->
                currentState.movieGroupStatuses[groupId] != status
            }
            // Groups to remove the status from (were checked before, now unchecked)
            val groupsToRemove = currentState.movieGroupStatuses
                .filter { (_, existingStatus) -> existingStatus == status }
                .keys
                .filter { it !in currentState.selectedGroupIds }

            if (groupsToAdd.isNotEmpty()) {
                repository.updateMovieStatus(groupsToAdd, userId, movieId, status)
                    .onSuccess { Timber.d("Status set in ${groupsToAdd.size} groups") }
                    .onFailure { Timber.e(it, "Error setting movie status") }
            }

            if (groupsToRemove.isNotEmpty()) {
                repository.removeMovieStatus(groupsToRemove.toList(), userId, movieId)
                    .onSuccess { Timber.d("Status removed from ${groupsToRemove.size} groups") }
                    .onFailure { Timber.e(it, "Error removing movie status") }
            }

            // Reload statuses
            loadMovieGroupStatuses(currentState.groups, userId)

            state.update { it.copy(showBottomSheet = false, bottomSheetStatus = null) }
        }
    }

    fun onBottomSheetDismiss() {
        state.update { it.copy(showBottomSheet = false, bottomSheetStatus = null) }
    }

    @AssistedFactory
    interface DetailsViewModelFactory {
        fun create(movieId: Int): DetailsViewModel
    }

    companion object {
        fun provideFactory(assistedFactory: DetailsViewModelFactory, movieId: Int): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T = assistedFactory.create(movieId) as T
            }
    }
}

data class DetailUiState(
    val user: User,
    val movie: Movie,
    val groups: List<Group>,
    val movieGroupStatuses: Map<String, MovieStatus>,
    val showBottomSheet: Boolean,
    val bottomSheetStatus: MovieStatus?,
    val selectedGroupIds: Set<String>,
) {
    constructor() : this(
        user = User(),
        movie = Movie(),
        groups = emptyList(),
        movieGroupStatuses = emptyMap(),
        showBottomSheet = false,
        bottomSheetStatus = null,
        selectedGroupIds = emptySet(),
    )

    val isToWatch: Boolean
        get() = movieGroupStatuses.values.any { it == MovieStatus.ToWatch }

    val isWatched: Boolean
        get() = movieGroupStatuses.values.any { it == MovieStatus.Watched }
}

@HiltViewModel
class DetailsViewModelFactoryProvider @Inject constructor(
    val detailsViewModelFactory: DetailsViewModel.DetailsViewModelFactory,
) : ViewModel()
