package com.apptolast.familyfilmapp.ui.screens.groups

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.apptolast.familyfilmapp.model.local.types.MediaType
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun GroupDetailScreen(
    groupId: String,
    onBack: () -> Unit,
    onMediaSelected: (Int, MediaType) -> Unit,
    viewModel: GroupDetailViewModel = koinViewModel(parameters = { parametersOf(groupId) }),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.navigateBackAfterDelete) {
        if (state.navigateBackAfterDelete) {
            viewModel.onBackNavigationHandled()
            onBack()
        }
    }

    GroupDetailContent(
        state = state,
        onBack = onBack,
        onShowDialog = viewModel::showDialog,
        onChangeGroupName = viewModel::changeGroupName,
        onPickGroupImage = viewModel::updateGroupImage,
        onAddMember = viewModel::addMember,
        onDeleteGroup = viewModel::deleteGroup,
        onRemoveMember = viewModel::removeMember,
        onMediaClick = { media -> onMediaSelected(media.id, media.mediaType) },
        onRevealRecommended = viewModel::revealRecommendedCard,
        onClearError = viewModel::clearError,
    )
}
