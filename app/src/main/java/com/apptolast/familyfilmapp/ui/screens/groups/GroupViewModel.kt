package com.apptolast.familyfilmapp.ui.screens.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.Movie
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.repositories.Repository
import com.apptolast.familyfilmapp.ui.screens.detail.MovieStatus
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
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
        refreshUi()
    }

    fun refreshUi() = viewModelScope.launch {
        combine(
            uiState,
            repository.getUserById(auth.currentUser!!.uid),
            repository.getMyGroups(auth.currentUser!!.uid),
        ) { uiState, currentUser, groups ->

            if (groups.isEmpty()) return@combine arrayOf<Any>(false)

            try {
                groups[uiState.selectedGroupIndex]
            } catch (e: Exception) {
                this@GroupViewModel.uiState.update {
                    it.copy(
                        selectedGroupIndex = uiState.selectedGroupIndex.coerceIn(
                            0,
                            groups.size - 1,
                        ),
                    )
                }
                return@combine arrayOf<Any>(false)
            }

            // Get all users from the group
            val groupUsers = mutableListOf<User>()
            groups[uiState.selectedGroupIndex].users.map {
                repository.getUserById(it).firstOrNull()?.let { user ->
                    groupUsers.add(user)
                }
            }

            // Get all movies from the group
            val toWatch = mutableListOf<Movie>()
            val watched = mutableListOf<Movie>()

            groupUsers
                .flatMap { user ->
                    user.statusMovies.filterValues { it == MovieStatus.ToWatch }.keys
                }
                .distinct()
                .let { movieIds ->
                    repository.getMoviesByIds(movieIds.map { it.toInt() })
                        .onSuccess { movies -> toWatch.addAll(movies) }
                        .onFailure { error -> Timber.e(error, "Error getting toWatch movies by ids") }
                }

            groupUsers
                .flatMap { user ->
                    user.statusMovies.filterValues { it == MovieStatus.Watched }.keys
                }
                .distinct()
                .let { movieIds ->
                    repository.getMoviesByIds(movieIds.map { it.toInt() })
                        .onSuccess { movies -> watched.addAll(movies) }
                        .onFailure { error -> Timber.e(error, "Error getting toWatch movies by ids") }
                }

            arrayOf<Any>(currentUser, groups, groupUsers, toWatch, watched)

        }.catch {
            Timber.e(it, "Error refreshing UI")
        }.collectLatest {

            if (it.any { it == false }) return@collectLatest

            val currentUser = it[0] as User
            val groups = it[1] as List<Group>
            val groupUsers = it[2] as List<User>
            val toWatch = it[3] as List<Movie>
            val watched = it[4] as List<Movie>

            // Update everything at once
            backendState.update {
                it.copy(
                    currentUser = currentUser,
                    groups = groups,
                    groupUsers = groupUsers,
                    moviesToWatch = toWatch,
                    moviesWatched = watched,
                )
            }
        }
    }

    fun createGroup(groupName: String) = viewModelScope.launch {
        val currentUser = repository.getUserById(auth.currentUser?.uid!!).first()
        repository.createGroup(
            groupName = groupName,
            user = currentUser,
            success = {
                viewModelScope.launch {
                    delay(50)
                    val pos = backendState.value.groups.indexOfFirst { it.name == groupName }
                    uiState.update { it.copy(selectedGroupIndex = pos) }
                }
            },
            failure = { error ->
                Timber.e(error, "Error creating the group ")
                uiState.update { it.copy(errorMessage = error.message) }
            },
        )
    }

    fun changeGroupName(group: Group) {
        repository.updateGroup(
            group = group,
            success = { },
            failure = { error ->
                Timber.e(error, "Error updating group name")
                uiState.update { it.copy(errorMessage = error.message) }
            },
        )
    }

    fun deleteGroup(group: Group) {
        repository.deleteGroup(
            group = group,
            success = {
                uiState.update {
                    it.copy(
                        selectedGroupIndex = (uiState.value.selectedGroupIndex - 1).coerceIn(
                            0,
                            Int.MAX_VALUE,
                        ),
                    )
                }
            },
            failure = { error ->
                Timber.e(error, "Error deleting group")
                uiState.update { it.copy(errorMessage = error.message) }
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
                uiState.update { it.copy(errorMessage = error.message) }
            },
        )
    }

    fun deleteMember(group: Group, userId: User) {
        repository.deleteMember(group, userId)
    }

    fun showDialog(dialog: GroupScreenDialogs) = uiState.update { it.copy(showDialog = dialog) }

    fun selectGroup(index: Int) = viewModelScope.launch {
        uiState.update {
            it.copy(selectedGroupIndex = index)
        }
    }

    fun clearErrorMessage() = uiState.update { it.copy(errorMessage = null) }

    private fun List<Group>.sort() = sortedWith(
        compareBy(String.CASE_INSENSITIVE_ORDER) { group -> group.name },
    )

    data class BackendState(
        val currentUser: User,
        val groups: List<Group>,
        val groupUsers: List<User>,
        val moviesToWatch: List<Movie>,
        val moviesWatched: List<Movie>,
    ) {
        constructor() : this(
            currentUser = User(),
            groups = emptyList(),
            groupUsers = emptyList(),
            moviesToWatch = emptyList(),
            moviesWatched = emptyList(),
        )
    }

    data class UiState(
        val showDialog: GroupScreenDialogs,
        val selectedGroupIndex: Int,
        val isLoading: Boolean,
        val errorMessage: String?,
    ) {
        constructor() : this(
            showDialog = GroupScreenDialogs.None,
            selectedGroupIndex = 0,
            isLoading = false,
            errorMessage = null,
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
