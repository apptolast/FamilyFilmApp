package com.apptolast.familyfilmapp.ui.screens.groups

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun GroupsScreen(onGroupSelected: (String) -> Unit, viewModel: GroupsViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.createdGroupIdToOpen) {
        state.createdGroupIdToOpen?.let { groupId ->
            onGroupSelected(groupId)
            viewModel.onCreatedGroupNavigationHandled()
        }
    }

    GroupsContent(
        state = state,
        onOpenGroup = onGroupSelected,
        onShowDialog = viewModel::showDialog,
        onCreateGroup = viewModel::createGroup,
        onClearError = viewModel::clearError,
        onRemovedFromGroupHandled = viewModel::onRemovedFromGroupHandled,
    )
}
