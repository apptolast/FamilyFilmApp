package com.apptolast.familyfilmapp.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.apptolast.familyfilmapp.ads.NativeAdManager
import com.apptolast.familyfilmapp.exceptions.CustomException
import com.apptolast.familyfilmapp.model.local.Media
import com.apptolast.familyfilmapp.model.local.types.MediaFilter
import com.apptolast.familyfilmapp.network.TmdbLocaleManager
import com.apptolast.familyfilmapp.repositories.Repository
import com.apptolast.familyfilmapp.utils.DispatcherProvider
import com.google.android.gms.ads.nativead.NativeAd
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
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
    private val tmdbLocaleManager: TmdbLocaleManager,
    private val nativeAdManager: NativeAdManager,
) : ViewModel() {

    val nativeAds: StateFlow<List<NativeAd>> = nativeAdManager.nativeAds

    val homeUiState: StateFlow<HomeUiState>
        field: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState())

    private val selectedFilter = MutableStateFlow(MediaFilter.ALL)
    private val activeSearchQuery = MutableStateFlow<String?>(null)

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
        nativeAdManager.loadAds()
        observeAdultContentChanges()
    }

    private fun observeAdultContentChanges() = viewModelScope.launch(dispatcherProvider.io()) {
        // Re-run any active search when the adult content preference changes so
        // already-fetched results don't keep showing stale items.
        tmdbLocaleManager.includeAdult
            .drop(1)
            .collect {
                activeSearchQuery.value?.takeIf { it.isNotEmpty() }?.let { query ->
                    Timber.d("includeAdult changed, re-running search: $query")
                    runSearch(query)
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        nativeAdManager.destroyAds()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val media: Flow<PagingData<Media>> = combine(
        selectedFilter,
        tmdbLocaleManager.includeAdult,
    ) { filter, includeAdult -> filter to includeAdult }
        .distinctUntilChanged()
        .flatMapLatest { (filter, _) ->
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
        .cachedIn(viewModelScope)

    fun setMediaFilter(filter: MediaFilter) {
        selectedFilter.value = filter
        homeUiState.update { it.copy(selectedFilter = filter, filterMedia = emptyList()) }
    }

    fun searchMediaByName(mediaFilter: String) = viewModelScope.launch(dispatcherProvider.io()) {
        if (mediaFilter.isEmpty()) {
            activeSearchQuery.value = null
            homeUiState.update { it.copy(filterMedia = emptyList()) }
            clearError()
            Timber.d("Search cleared, showing popular media")
        } else {
            activeSearchQuery.value = mediaFilter
            runSearch(mediaFilter)
        }
    }

    private suspend fun runSearch(query: String) {
        val currentFilter = homeUiState.value.selectedFilter
        val result = when (currentFilter) {
            MediaFilter.ALL -> repository.searchMulti(query)

            MediaFilter.MOVIES -> repository.searchTmdbMovieByName(query)

            MediaFilter.TV_SHOWS -> repository.searchMulti(query).map { list ->
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

    fun triggerError(errorMessage: String) {
        homeUiState.update { it.copy(errorMessage = CustomException.GenericException(errorMessage)) }
    }

    fun clearError() {
        homeUiState.update { it.copy(errorMessage = CustomException.GenericException(null)) }
    }
}
