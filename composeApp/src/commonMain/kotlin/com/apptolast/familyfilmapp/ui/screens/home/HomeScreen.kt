package com.apptolast.familyfilmapp.ui.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import app.cash.paging.compose.collectAsLazyPagingItems
import com.apptolast.familyfilmapp.model.local.types.MediaType
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    onMediaSelected: (mediaId: Int, mediaType: MediaType) -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val stateUI by viewModel.homeUiState.collectAsState()
    val mediaItems = viewModel.media.collectAsLazyPagingItems()
    val nativeAds by viewModel.nativeAds.collectAsState()

    HomeContent(
        stateUI = stateUI,
        mediaItems = mediaItems,
        nativeAds = nativeAds,
        onMediaClick = { media ->
            viewModel.logMediaSelected(media)
            onMediaSelected(media.id, media.mediaType)
        },
        searchMediaByName = viewModel::searchMediaByName,
        onFilterSelect = viewModel::setMediaFilter,
        triggerError = viewModel::triggerError,
    )
}
