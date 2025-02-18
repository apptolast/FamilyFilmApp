package com.apptolast.familyfilmapp.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.SelectedMovie
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.repositories.Repository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DetailScreenViewModel @Inject constructor(private val repository: Repository, private val auth: FirebaseAuth) :
    ViewModel() {

    val state: StateFlow<DetailScreenStateState>
        field: MutableStateFlow<DetailScreenStateState> = MutableStateFlow(DetailScreenStateState())

    init {
        viewModelScope.launch {
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
        val currentUser = state.value.user.copy()
        val dialogType = state.value.dialogType

        // 1. Update the user's movie lists
        val (updatedToWatchList, updatedWatchedList) = updateUserMovieLists(
            currentUser = currentUser,
            movieId = movieId,
            group = group,
            isChecked = isChecked,
            dialogType = dialogType
        )

        // 2. Update the group's movie lists
        val updatedGroup = updateGroupMovieLists(
            group = group,
            movieId = movieId,
            isChecked = isChecked,
            dialogType = dialogType
        )

        // 3. Persist the changes
        persistChanges(
            updatedUser = currentUser.copy(
                toWatch = updatedToWatchList.distinct(),
                watched = updatedWatchedList.distinct()
            ),
            updatedGroup = updatedGroup
        )
    }

    /**
     * Updates the user's toWatch and watched lists based on the movieId, group, and isChecked status.
     * This function ensures that a movie can only exist in one list at a time (either toWatch or watched).
     *
     * @param currentUser The current user.
     * @param movieId The ID of the movie being updated.
     * @param group The group associated with the movie.
     * @param isChecked Whether the movie is being added or removed from the list.
     * @param dialogType The type of dialog (ToWatch or Watched) indicating which list is being directly modified.
     * @return A Pair containing the updated toWatch list and the updated watched list.
     */
    private fun updateUserMovieLists(
        currentUser: User,
        movieId: Int,
        group: Group,
        isChecked: Boolean,
        dialogType: DialogType
    ): Pair<List<SelectedMovie>, List<SelectedMovie>> {

        // Update toWatch list
        val updatedToWatchList = updateMovieList(
            movieList = currentUser.toWatch,
            movieId = movieId,
            group = group,
            isChecked = isChecked && dialogType == DialogType.ToWatch
        )

        // Update watched list
        val updatedWatchedList = updateMovieList(
            movieList = currentUser.watched,
            movieId = movieId,
            group = group,
            isChecked = isChecked && dialogType == DialogType.Watched
        )

        // Remove from other list
        val removeMovieFromToWatch = if (dialogType == DialogType.Watched && isChecked) {
            updatedToWatchList.filter { it.movieId != movieId }
        } else {
            updatedToWatchList
        }

        val removeMovieFromWatched = if (dialogType == DialogType.ToWatch && isChecked) {
            updatedWatchedList.filter { it.movieId != movieId }
        } else {
            updatedWatchedList
        }

        return Pair(removeMovieFromToWatch, removeMovieFromWatched)
    }

    /**
     * Updates the group's watchedList and toWatchList based on the movieId and isChecked status.
     *
     * @param group The group to update.
     * @param movieId The ID of the movie being updated.
     * @param isChecked Whether to add or remove the movie from the list.
     * @param dialogType The type of dialog (ToWatch or Watched) indicating which list is being directly modified.
     * @return The updated Group object.
     */
    private fun updateGroupMovieLists(
        group: Group,
        movieId: Int,
        isChecked: Boolean,
        dialogType: DialogType
    ): Group {
        return group.copy(
            users = group.users.map { user ->
                if (user.id == auth.uid) state.value.user else user
            },
            watchedList = (if (dialogType == DialogType.Watched) {
                if (isChecked) group.watchedList + movieId else group.watchedList - movieId
            } else {
                group.watchedList
            }).distinct(),
            toWatchList = (if (dialogType == DialogType.ToWatch) {
                if (isChecked) group.toWatchList + movieId else group.toWatchList - movieId
            } else {
                group.toWatchList
            }).distinct()
        )
    }

    /**
     * Persists the updated user and group to the database.
     *
     * @param updatedUser The updated User object.
     * @param updatedGroup The updated Group object.
     */
    private fun persistChanges(updatedUser: User, updatedGroup: Group) {
        repository.updateUser(updatedUser) {
            repository.updateGroup(
                group = updatedGroup,
                success = {},
                failure = { Timber.e(it) }
            )
        }
    }

    /**
     * Updates a movie list (either toWatch or watched) by either adding or removing
     * a group ID to the movie's list of groups.
     *
     * @param movieList The list of movies to update.
     * @param movieId The ID of the movie to update.
     * @param group The group to add or remove.
     * @param isChecked Whether to add or remove the group.
     * @return The updated movie list.
     */
    private fun updateMovieList(
        movieList: List<SelectedMovie>,
        movieId: Int,
        group: Group,
        isChecked: Boolean,
    ): List<SelectedMovie> {
        val existingMovie = movieList.firstOrNull { it.movieId == movieId }

        return if (existingMovie != null) {
            val updatedGroups = if (isChecked) {
                existingMovie.groupsIds.toMutableSet().apply { add(group.id) }
            } else {
                existingMovie.groupsIds.toMutableSet().apply { remove(group.id) }
            }

            movieList.map { movie ->
                if (movie.movieId == movieId) {
                    movie.copy(groupsIds = updatedGroups.toList()) // Convert back to List
                } else {
                    movie
                }
            }
        } else {
            movieList + SelectedMovie(movieId, listOf(group.id))
        }
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

