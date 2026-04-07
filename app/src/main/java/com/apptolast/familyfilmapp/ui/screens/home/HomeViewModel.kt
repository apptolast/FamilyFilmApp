package com.apptolast.familyfilmapp.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.apptolast.familyfilmapp.exceptions.CustomException
import com.apptolast.familyfilmapp.model.local.Media
import com.apptolast.familyfilmapp.model.local.types.MediaFilter
import com.apptolast.familyfilmapp.repositories.Repository
import com.apptolast.familyfilmapp.utils.DispatcherProvider
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: Repository,
    private val auth: FirebaseAuth,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {

    val homeUiState: StateFlow<HomeUiState>
        field: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState())

    private val selectedFilter = MutableStateFlow(MediaFilter.ALL)

    init {
        viewModelScope.launch(dispatcherProvider.io()) {
            homeUiState.update { it.copy(isLoading = true) }
            val userId = auth.uid
            if (userId == null) {
                homeUiState.update { it.copy(isLoading = false) }
                return@launch
            }

            repository.getUserById(userId).collectLatest { user ->
                homeUiState.update {
                    it.copy(
                        isLoading = false,
                        user = user,
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val media: Flow<PagingData<Media>> = selectedFilter
        .flatMapLatest { filter ->
            when (filter) {
                MediaFilter.ALL -> repository.getPopularMovies()
                MediaFilter.MOVIES -> repository.getPopularMovies()
                MediaFilter.TV_SHOWS -> repository.getPopularTvShows()
            }
        }
        .catch { error ->
            triggerError(error.message ?: "Error getting media")
            Timber.e(error, "Error getting media")
        }
        .distinctUntilChanged()
        .cachedIn(viewModelScope)

    fun setMediaFilter(filter: MediaFilter) {
        selectedFilter.value = filter
        homeUiState.update { it.copy(selectedFilter = filter, filterMedia = emptyList()) }
    }

    fun searchMediaByName(mediaFilter: String) = viewModelScope.launch(dispatcherProvider.io()) {
        if (mediaFilter.isEmpty()) {
            homeUiState.update { it.copy(filterMedia = emptyList()) }
            clearError()
            Timber.d("Search cleared, showing popular media")
        } else {
            val currentFilter = homeUiState.value.selectedFilter
            val result = when (currentFilter) {
                MediaFilter.ALL -> repository.searchMulti(mediaFilter)

                MediaFilter.MOVIES -> repository.searchTmdbMovieByName(mediaFilter)

                MediaFilter.TV_SHOWS -> repository.searchMulti(mediaFilter).map { list ->
                    list.filter { it.mediaType == com.apptolast.familyfilmapp.model.local.types.MediaType.TV_SHOW }
                }
            }

            result
                .onSuccess { mediaList ->
                    homeUiState.update { it.copy(filterMedia = mediaList) }
                    clearError()
                }
                .onFailure { e ->
                    Timber.e(e, "Error searching media")
                    triggerError(e.message ?: "Error searching media")
                }
        }
    }

    fun triggerError(errorMessage: String) {
        homeUiState.update { it.copy(errorMessage = CustomException.GenericException(errorMessage)) }
    }

    fun clearError() {
        homeUiState.update { it.copy(errorMessage = CustomException.GenericException(null)) }
    }
}
