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
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailScreenViewModel @Inject constructor(
    private val repository: Repository,
    private val auth: FirebaseAuth,
) : ViewModel() {

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
        // Update User
        val updatedUser = state.value.user.copy(
            toWatch = (if (state.value.dialogType == DialogType.ToWatch) {
                updateMovieList(state.value.user.toWatch, movieId, group, isChecked)
            } else state.value.user.toWatch.distinct()),
            watched = (if (state.value.dialogType == DialogType.Watched) {
                updateMovieList(state.value.user.watched, movieId, group, isChecked)
            } else state.value.user.watched).distinct(),
        )
        repository.updateUser(viewModelScope, updatedUser)

        // Update group by adding or removing the movie from its lists
        val updatedGroup = group.copy(
            // update "updatedUser" in the group list user
            users = group.users.map { user ->
                if (user.id == auth.uid) updatedUser else user
            },
            watchedList = (if (state.value.dialogType == DialogType.Watched) {
                if (isChecked) group.watchedList + movieId else group.watchedList - movieId
            } else group.watchedList).distinct(),
            toWatchList = (if (state.value.dialogType == DialogType.ToWatch) {
                if (isChecked) group.toWatchList + movieId else group.toWatchList - movieId
            } else group.toWatchList).distinct(),
        )
        repository.updateGroup(viewModelScope, updatedGroup)
    }

    private fun updateMovieList(
        movieList: List<SelectedMovie>,
        movieId: Int,
        group: Group,
        isChecked: Boolean,
    ): List<SelectedMovie> {

        val updatedMovie = movieList.find { it.movieId == movieId }

        return if (updatedMovie != null) {
            val updatedGroups = if (isChecked) {
                updatedMovie.groups + group
            } else {
                updatedMovie.groups - group
            }
            movieList.map { movie ->
                if (movie.movieId == movieId) {
                    movie.copy(groups = updatedGroups)
                } else movie
            }
        } else {
            val newMovie = SelectedMovie(movieId, listOf(group))
            movieList + newMovie
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
