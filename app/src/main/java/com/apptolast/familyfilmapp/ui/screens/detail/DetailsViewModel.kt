package com.apptolast.familyfilmapp.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
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

    val state: StateFlow<DetailStateState>
        field: MutableStateFlow<DetailStateState> = MutableStateFlow(DetailStateState())

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
            )
        }
    }

    fun updateMovieStatus(movie: Movie, status: MovieStatus) {
        val currentStatusMovies = state.value.user.statusMovies.toMutableMap()
        val currentStatus = currentStatusMovies[movie.id.toString()]

        // Toggle functionality: If already has the same status, remove it (deselect)
        if (currentStatus == status) {
            currentStatusMovies.remove(movie.id.toString())
        } else {
            // Otherwise set the new status
            currentStatusMovies[movie.id.toString()] = status
        }

        repository.updateUser(
            state.value.user.copy(
                statusMovies = currentStatusMovies.toMap(),
            ),
        ) {
            Timber.d("Movie status updated")
        }
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

data class DetailStateState(val user: User, val movie: Movie) {
    constructor() : this(
        user = User(),
        movie = Movie(),
    )
}

@HiltViewModel
class DetailsViewModelFactoryProvider @Inject constructor(
    val detailsViewModelFactory: DetailsViewModel.DetailsViewModelFactory,
) : ViewModel()
