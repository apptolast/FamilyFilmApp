package com.apptolast.familyfilmapp.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.SelectedMovie
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.repositories.Repository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class DetailScreenViewModel @Inject constructor(private val repository: Repository, private val auth: FirebaseAuth) :
    ViewModel() {

    val state: StateFlow<DetailScreenStateState>
        field: MutableStateFlow<DetailScreenStateState> = MutableStateFlow(DetailScreenStateState())

    init {
        viewModelScope.launch {

//            repository.getMyGroups(auth.uid!!).combine(
//                repository.getUserById(auth.uid!!),
//            ) { groups, users ->
//                groups to users
//            }.collect { (groups, user) ->
//                state.update {
//                    it.copy(user = user, groups = groups)
//                }
//            }

            awaitAll(
                async {
                    repository.getMyGroups(auth.uid!!).collectLatest {
                        it.sortedBy { it.name }.let { groups ->
                            state.update { it.copy(groups = groups) }
                        }
                    }
                },
                async {
                    repository.getUserById(auth.uid!!).collectLatest { user ->
                        state.update { it.copy(user = user) }
                    }
                },
            )
        }
    }

    fun displayDialog(dialogType: DialogType) = viewModelScope.launch {
        state.update {
            it.copy(
                dialogType = dialogType,
            )
        }
    }

    fun updateMovieGroup(movieId: Int, group: Group, isChecked: Boolean) {
        val userId = auth.currentUser!!.uid
        val dialogType = state.value.dialogType

        // Find the current user in the group's user list
        val currentUser = group.users.find { it.id == userId }

        // If user is in the group, then update the data correctly
        if (currentUser != null) {
            // Update the user's movie lists based on dialog type and check status
            val (updatedToWatch, updatedWatched) = updateUserLists(
                user = currentUser,
                movieId = movieId,
                isChecked = isChecked,
                dialogType = dialogType,
                group = group,
            )

            val updatedUsers = group.users.map { user ->
                if (user.id == userId) {
                    user.copy(
                        toWatch = updatedToWatch.distinct(),
                        watched = updatedWatched.distinct(),
                    )
                } else {
                    user
                }
            }

            // Create a copy of the group with the updated user list
            val updatedGroup = group.copy(users = updatedUsers)

            // Update the group in Firebase
            repository.updateGroup(
                group = updatedGroup,
                success = { Timber.d("Successfully updated the group") },
                failure = { Timber.e(it, "Failed to update the group") },
            )
            // Update user in Firebase
            updateUser(updatedUsers, userId)
        } else {
            // Update movie lists by adding the movie to the group
            val updatedUsers = group.users + state.value.user.copy(
                toWatch = listOf(SelectedMovie(movieId = movieId, groupsIds = listOf(group.id))),
            )
            val updatedGroup = group.copy(users = updatedUsers)

            // Update the group in Firebase
            repository.updateGroup(
                group = updatedGroup,
                success = { Timber.d("Successfully updated the group") },
                failure = { Timber.e(it, "Failed to update the group") },
            )
            // Update user in Firebase
            updateUser(updatedUsers, userId)
        }
    }

    private fun updateUser(updatedUsers: List<User>, userId: String) {
        updatedUsers.find { it.id == userId }?.let {
            repository.updateUser(
                user = it,
                success = { Timber.d("Successfully updated the user") },
            )
        }
    }

    private fun updateUserLists(
        user: User,
        movieId: Int,
        isChecked: Boolean,
        dialogType: DialogType,
        group: Group,
    ): Pair<MutableList<SelectedMovie>, MutableList<SelectedMovie>> {
        val updatedToWatch = user.toWatch.toMutableList()
        val updatedWatched = user.watched.toMutableList()

        if (dialogType == DialogType.ToWatch) {
            if (isChecked) {
                // Add movie to toWatch, preserving existing group associations
                val existingMovie = updatedToWatch.find { it.movieId == movieId }
                if (existingMovie != null) {
                    // Movie already exists in toWatch, so add the new group ID
                    val updatedGroups = existingMovie.groupsIds.toMutableList().apply { add(group.id) }.distinct()
                    updatedToWatch.remove(existingMovie) // Remove the old entry
                    updatedToWatch.add(existingMovie.copy(groupsIds = updatedGroups)) // Add updated entry
                } else {
                    // Movie doesn't exist in toWatch, so create a new entry with the current group ID
                    updatedToWatch.add(SelectedMovie(movieId = movieId, groupsIds = listOf(group.id)))
                }
            } else {
                // Remove movie from toWatch for the specified group, preserving other group associations
                updatedToWatch.removeIf { it.movieId == movieId && it.groupsIds.contains(group.id) }
            }
            updatedWatched.removeIf { it.movieId == movieId }
        } else if (dialogType == DialogType.Watched) {
            if (isChecked) {
                // Add movie to watched, preserving existing group associations
                val existingMovie = updatedWatched.find { it.movieId == movieId }
                if (existingMovie != null) {
                    // Movie already exists in watched, so add the new group ID
                    val updatedGroups = existingMovie.groupsIds.toMutableList().apply { add(group.id) }.distinct()
                    updatedWatched.remove(existingMovie) // Remove the old entry
                    updatedWatched.add(existingMovie.copy(groupsIds = updatedGroups)) // Add updated entry
                } else {
                    // Movie doesn't exist in watched, so create a new entry with the current group ID
                    updatedWatched.add(SelectedMovie(movieId = movieId, groupsIds = listOf(group.id)))
                }
            } else {
                // Remove movie from watched for the specified group, preserving other group associations
                updatedWatched.removeIf { it.movieId == movieId && it.groupsIds.contains(group.id) }
            }
            updatedToWatch.removeIf { it.movieId == movieId }
        }

        return Pair(updatedToWatch, updatedWatched)
    }
}

data class DetailScreenStateState(
    val user: User,
    val dialogType: DialogType = DialogType.NONE,
    val groups: List<Group>,
) {
    constructor() : this(
        user = User(),
        dialogType = DialogType.NONE,
        groups = emptyList(),
    )
}

enum class DialogType {
    Watched,
    ToWatch,
    NONE,
}
