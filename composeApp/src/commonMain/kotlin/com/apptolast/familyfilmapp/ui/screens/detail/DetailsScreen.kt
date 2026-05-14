package com.apptolast.familyfilmapp.ui.screens.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.apptolast.familyfilmapp.model.local.types.MediaType
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun DetailsScreen(
    mediaId: Int,
    mediaType: MediaType,
    onBack: () -> Unit,
    viewModel: DetailsViewModel = koinViewModel(parameters = { parametersOf(mediaId, mediaType) }),
) {
    val state by viewModel.state.collectAsState()

    DetailsContent(
        state = state,
        onBack = onBack,
        onStatusClick = viewModel::onStatusButtonClick,
        onGroupToggle = viewModel::onGroupSelectionChanged,
        onConfirm = viewModel::confirmMediaStatus,
        onDismissSheet = viewModel::onBottomSheetDismiss,
    )
}
