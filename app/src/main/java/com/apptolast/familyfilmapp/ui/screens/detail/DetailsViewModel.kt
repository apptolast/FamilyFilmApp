package com.apptolast.familyfilmapp.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptolast.familyfilmapp.model.local.Movie
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.model.local.types.MovieStatus
import com.apptolast.familyfilmapp.repositories.Repository
import com.apptolast.familyfilmapp.utils.DispatcherProvider
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.collections.toMutableMap

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repository: Repository,
    private val auth: FirebaseAuth,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {

    val state: StateFlow<DetailStateState>
        field: MutableStateFlow<DetailStateState> = MutableStateFlow(DetailStateState())

    init {
        viewModelScope.launch(dispatcherProvider.io()) {
            repository.getUserById(auth.uid!!).collectLatest { user ->
                state.update { it.copy(user = user) }
            }
        }
    }

    fun updateMovieStatus(movie: Movie, status: MovieStatus) {
        val currentStatusMovies = state.value.user.statusMovies.toMutableMap()
        currentStatusMovies[movie.id.toString()] = status

        repository.updateUser(
            state.value.user.copy(
                statusMovies = currentStatusMovies.toMap(),
            ),
        ) {
            Timber.d("Movie status updated")
        }
    }
}

data class DetailStateState(val user: User) {
    constructor() : this(user = User())
}
