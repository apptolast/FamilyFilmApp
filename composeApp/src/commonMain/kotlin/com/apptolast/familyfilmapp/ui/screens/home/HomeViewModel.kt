@file:OptIn(ExperimentalCoroutinesApi::class)

package com.apptolast.familyfilmapp.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingData
import app.cash.paging.cachedIn
import com.apptolast.familyfilmapp.ads.NativeAdHandle
import com.apptolast.familyfilmapp.ads.NativeAdManager
import com.apptolast.familyfilmapp.analytics.AnalyticsEvents
import com.apptolast.familyfilmapp.analytics.AnalyticsTracker
import com.apptolast.familyfilmapp.exceptions.CustomException
import com.apptolast.familyfilmapp.firebase.CrashReporter
import com.apptolast.familyfilmapp.firebase.CurrentUserIdProvider
import com.apptolast.familyfilmapp.model.local.Media
import com.apptolast.familyfilmapp.model.local.types.MediaFilter
import com.apptolast.familyfilmapp.model.local.types.MediaType
import com.apptolast.familyfilmapp.network.TmdbLocaleManager
import com.apptolast.familyfilmapp.repositories.Repository
import com.apptolast.familyfilmapp.repositories.datasources.TmdbDatasource
import com.apptolast.familyfilmapp.utils.DispatcherProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: Repository,
    private val tmdbDatasource: TmdbDatasource,
    private val dispatcherProvider: DispatcherProvider,
    private val tmdbLocaleManager: TmdbLocaleManager,
    private val nativeAdManager: NativeAdManager,
    private val analyticsTracker: AnalyticsTracker,
    private val crashReporter: CrashReporter,
    private val currentUserIdProvider: CurrentUserIdProvider,
) : ViewModel() {

    val nativeAds: StateFlow<List<NativeAdHandle>> = nativeAdManager.nativeAds

    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState: StateFlow<HomeUiState> = _homeUiState.asStateFlow()

    private val selectedFilter = MutableStateFlow(MediaFilter.ALL)
    private val activeSearchQuery = MutableStateFlow<String?>(null)

    private val currentUserId: String? get() = currentUserIdProvider.currentUserId()

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

    val media: Flow<PagingData<Media>> = combine(
        selectedFilter,
        tmdbLocaleManager.includeAdult,
    ) { filter, includeAdult -> filter to includeAdult }
        .distinctUntilChanged()
        .flatMapLatest { (filter, _) ->
            val countryCode = tmdbLocaleManager.countryCode
            val pagingSourceFactory = when (filter) {
                MediaFilter.ALL, MediaFilter.MOVIES -> {
                    { MediaPagingSource(tmdbDatasource, countryCode) }
                }

                MediaFilter.TV_SHOWS -> {
                    { TvShowPagingSource(tmdbDatasource, countryCode) }
                }
            }
            Pager(
                config = PagingConfig(pageSize = 20, enablePlaceholders = false),
                pagingSourceFactory = pagingSourceFactory,
            ).flow
        }
        .catch { error ->
            crashReporter.recordException(error)
            triggerError(error.message ?: "Error getting media")
        }
        .cachedIn(viewModelScope)

    fun setMediaFilter(filter: MediaFilter) {
        selectedFilter.value = filter
        _homeUiState.update { it.copy(selectedFilter = filter, filterMedia = emptyList()) }
        analyticsTracker.logEvent(
            AnalyticsEvents.FILTER_CHANGED,
            mapOf(AnalyticsEvents.Param.FILTER to filter.name),
        )
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
