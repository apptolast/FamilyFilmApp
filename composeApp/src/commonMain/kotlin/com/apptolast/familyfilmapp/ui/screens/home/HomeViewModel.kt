@file:OptIn(ExperimentalCoroutinesApi::class)

package com.apptolast.familyfilmapp.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptolast.familyfilmapp.ads.NativeAdHandle
import com.apptolast.familyfilmapp.ads.NativeAdManager
import com.apptolast.familyfilmapp.analytics.AnalyticsEvents
import com.apptolast.familyfilmapp.analytics.AnalyticsTracker
import com.apptolast.familyfilmapp.exceptions.CustomException
import com.apptolast.familyfilmapp.firebase.CrashReporter
import com.apptolast.familyfilmapp.model.local.Media
import com.apptolast.familyfilmapp.model.local.types.MediaFilter
import com.apptolast.familyfilmapp.model.local.types.MediaType
import com.apptolast.familyfilmapp.network.TmdbLocaleManager
import com.apptolast.familyfilmapp.repositories.Repository
import com.apptolast.familyfilmapp.utils.DispatcherProvider
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: Repository,
    private val dispatcherProvider: DispatcherProvider,
    private val tmdbLocaleManager: TmdbLocaleManager,
    private val nativeAdManager: NativeAdManager,
    private val analyticsTracker: AnalyticsTracker,
    private val crashReporter: CrashReporter,
) : ViewModel() {

    val nativeAds: StateFlow<List<NativeAdHandle>> = nativeAdManager.nativeAds

    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState: StateFlow<HomeUiState> = _homeUiState.asStateFlow()

    private val selectedFilter = MutableStateFlow(MediaFilter.ALL)
    private val activeSearchQuery = MutableStateFlow<String?>(null)

    private val currentUserId: String? get() = Firebase.auth.currentUser?.uid

    private val _media = MutableStateFlow<List<Media>>(emptyList())
    val media: StateFlow<List<Media>> = _media.asStateFlow()

    init {
        viewModelScope.launch(dispatcherProvider.io()) {
            _homeUiState.update { it.copy(isLoading = true) }
            val userId = currentUserId
            if (userId == null) {
                _homeUiState.update { it.copy(isLoading = false) }
                return@launch
            }
            repository.getUserById(userId).collectLatest { user ->
                _homeUiState.update { it.copy(isLoading = false, user = user) }
            }
        }
        nativeAdManager.loadAds()
        observeAdultContentChanges()
        loadMedia(MediaFilter.ALL)
    }

    private fun observeAdultContentChanges() = viewModelScope.launch(dispatcherProvider.io()) {
        tmdbLocaleManager.includeAdult
            .drop(1)
            .collect {
                activeSearchQuery.value?.takeIf { it.isNotEmpty() }?.let { runSearch(it) }
            }
    }

    override fun onCleared() {
        super.onCleared()
        nativeAdManager.destroyAds()
    }

    /**
     * Paging dropped from the data layer in block 11 — this fetches a single
     * page on filter change. If multi-page paging is needed by the UI, block
     * 13 plugs in a multiplatform paging library and reverts the signature.
     */
    private fun loadMedia(filter: MediaFilter) = viewModelScope.launch(dispatcherProvider.io()) {
        val result = when (filter) {
            MediaFilter.ALL, MediaFilter.MOVIES -> repository.getPopularMoviesList()
            MediaFilter.TV_SHOWS -> repository.getPopularTvShowsList()
        }
        result
            .onSuccess { list -> _media.update { list } }
            .onFailure { e ->
                crashReporter.recordException(e)
                triggerError(e.message ?: "Error getting media")
            }
    }

    fun setMediaFilter(filter: MediaFilter) {
        selectedFilter.value = filter
        _homeUiState.update { it.copy(selectedFilter = filter, filterMedia = emptyList()) }
        analyticsTracker.logEvent(
            AnalyticsEvents.FILTER_CHANGED,
            mapOf(AnalyticsEvents.Param.FILTER to filter.name),
        )
        loadMedia(filter)
    }

    fun searchMediaByName(mediaFilter: String) = viewModelScope.launch(dispatcherProvider.io()) {
        if (mediaFilter.isEmpty()) {
            activeSearchQuery.value = null
            _homeUiState.update { it.copy(filterMedia = emptyList()) }
            clearError()
        } else {
            activeSearchQuery.value = mediaFilter
            runSearch(mediaFilter)
        }
    }

    private suspend fun runSearch(query: String) {
        val currentFilter = _homeUiState.value.selectedFilter
        val result = when (currentFilter) {
            MediaFilter.ALL -> repository.searchMulti(query)
            MediaFilter.MOVIES -> repository.searchTmdbMovieByName(query)
            MediaFilter.TV_SHOWS -> repository.searchMulti(query).map { list ->
                list.filter { it.mediaType == MediaType.TV_SHOW }
            }
        }
        result
            .onSuccess { mediaList ->
                _homeUiState.update { it.copy(filterMedia = mediaList) }
                clearError()
                analyticsTracker.logSearch(
                    queryLength = query.length,
                    resultsCount = mediaList.size,
                    filter = currentFilter.name,
                )
            }
            .onFailure { e ->
                crashReporter.recordException(e)
                triggerError(e.message ?: "Error searching media")
            }
    }

    fun logMediaSelected(media: Media) {
        val isSearch = activeSearchQuery.value?.isNotEmpty() == true
        analyticsTracker.logSelectContent(
            contentType = media.mediaType.toAnalyticsContentType(),
            itemId = media.id.toString(),
            source = if (isSearch) "home_search" else "home_popular",
        )
    }

    fun triggerError(errorMessage: String) {
        _homeUiState.update { it.copy(errorMessage = CustomException.GenericException(errorMessage)) }
    }

    fun clearError() {
        _homeUiState.update { it.copy(errorMessage = CustomException.GenericException(null)) }
    }
}

internal fun MediaType.toAnalyticsContentType(): String = when (this) {
    MediaType.TV_SHOW -> AnalyticsEvents.ContentType.TV_SHOW
    else -> AnalyticsEvents.ContentType.MOVIE
}
