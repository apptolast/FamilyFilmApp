package com.apptolast.familyfilmapp.ui.screens.discover

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.apptolast.familyfilmapp.model.local.types.MediaType
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DiscoverScreen(
    onMediaSelected: (mediaId: Int, mediaType: MediaType) -> Unit,
    viewModel: DiscoverViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    DiscoverContent(
        state = state,
        onSkip = viewModel::skipMedia,
        onWantToWatch = viewModel::markAsWantToWatch,
        onWatched = viewModel::markAsWatched,
        onOpenDetails = { onMediaSelected(it.id, it.mediaType) },
        onFilterSelect = viewModel::setMediaFilter,
        onToggleGroup = viewModel::toggleGroupSelection,
        onDismissSkipped = viewModel::hideSkippedSheet,
        onRestoreSkipped = viewModel::restoreSkippedMedia,
        onClearError = viewModel::clearError,
    )
}
