package com.apptolast.familyfilmapp.ui.screens.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.Movie
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.repositories.Repository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(private val repository: Repository, private val auth: FirebaseAuth) :
    ViewModel() {

    val backendState: StateFlow<BackendState>
        field: MutableStateFlow<BackendState> = MutableStateFlow(BackendState())

    val uiState: StateFlow<UiState>
        field: MutableStateFlow<UiState> = MutableStateFlow(UiState())

    init {
        viewModelScope.launch {
            awaitAll(
                async { getGroups() },
                async { me() },
            )
        }
    }

    private suspend fun getGroups() {
        repository.getMyGroups(auth.currentUser?.uid!!).combine(uiState) { groups, uiState ->
            groups
        }
            .catch { error ->
                backendState.update {
                    it.copy(errorMessage = error.message)
                }
            }
            .collectLatest { groups ->
                Timber.d("GroupViewModel - Collect room change")

                if (groups.isNotEmpty()) {

                    val moviesToWatch = repository
                        .getMoviesByIds(
                            groups[uiState.value.selectedGroupIndex].users.map {
                                it.toWatch.map { it.movieId }
                            }.flatten().distinct(),
                        )
                        .getOrNull()

                    val moviesWatched = repository
                        .getMoviesByIds(
                            groups[uiState.value.selectedGroupIndex].users.map {
                                it.watched.map { it.movieId }
                            }.flatten().distinct(),
                        )
                        .getOrNull()

                    backendState.update {
                        it.copy(
                            groups = groups,
//                            .sortedWith(
//                                compareBy(String.CASE_INSENSITIVE_ORDER) { group -> group.name },
//                            ),
                            moviesToWatch = moviesToWatch ?: emptyList(),
                            moviesWatched = moviesWatched ?: emptyList(),
                            errorMessage = null,
                        )
                    }
                }
            }
    }

    private suspend fun me() {
        val authUser = auth.currentUser ?: return

        repository.getUserById(authUser.uid).collectLatest { user ->
            backendState.update {
                it.copy(currentUser = user)
            }
        }
    }

    fun createGroup(groupName: String) = viewModelScope.launch {
        val currentUser = repository.getUserById(auth.currentUser?.uid!!).first()
        repository.createGroup(
            groupName = groupName,
            user = currentUser,
            success = { },
            failure = { error ->
                Timber.e(error, "Error creating the group ")
                backendState.update { it.copy(errorMessage = error.message) }
            },
        )
    }

    fun changeGroupName(group: Group) {
        repository.updateGroup(
            group = group,
            success = { },
            failure = { error ->
                Timber.e(error, "Error updating group name")
                backendState.update { it.copy(errorMessage = error.message) }
            },
        )
    }

    fun deleteGroup(group: Group) {
        uiState.update {
            it.copy(
                selectedGroupIndex = (uiState.value.selectedGroupIndex - 1).coerceIn(
                    0,
                    Int.MAX_VALUE,
                ),
            )
        }
        repository.deleteGroup(
            group = group,
            success = { },
            failure = { error ->
                Timber.e(error, "Error deleting group")
                backendState.update { it.copy(errorMessage = error.message) }
            },
        )
    }

    fun addMember(group: Group, email: String) {
        repository.addMember(
            group = group,
            email = email,
            success = { },
            failure = { error ->
                Timber.e(error, "Error adding member to group")
                backendState.update { it.copy(errorMessage = error.message) }
            },
        )
    }

    fun deleteMember(group: Group, user: User) {
        repository.deleteMember(group, user)
    }

    fun showDialog(dialog: GroupScreenDialogs) = uiState.update { it.copy(showDialog = dialog) }

    fun selectGroup(index: Int) = viewModelScope.launch {
        uiState.update {
            it.copy(selectedGroupIndex = index)
        }
    }

    fun clearErrorMessage() = backendState.update { it.copy(errorMessage = null) }

    data class BackendState(
        val currentUser: User,
        val groups: List<Group>,
        val moviesToWatch: List<Movie>,
        val moviesWatched: List<Movie>,
        val isLoading: Boolean,
        val errorMessage: String?,
    ) {
        constructor() : this(
            currentUser = User(),
            groups = emptyList(),
            moviesToWatch = emptyList(),
            moviesWatched = emptyList(),
            isLoading = false,
            errorMessage = null,
        )
    }

    data class UiState(val showDialog: GroupScreenDialogs, val selectedGroupIndex: Int) {
        constructor() : this(
            showDialog = GroupScreenDialogs.None,
            selectedGroupIndex = 0,
        )
    }

    sealed interface GroupScreenDialogs {
        data object CreateGroup : GroupScreenDialogs
        data class DeleteGroup(val group: Group) : GroupScreenDialogs
        data class ChangeGroupName(val group: Group) : GroupScreenDialogs
        data class AddMember(val group: Group) : GroupScreenDialogs
        data object None : GroupScreenDialogs
    }
}
