package com.apptolast.familyfilmapp.ui.screens.groups

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun GroupsScreen(viewModel: GroupViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()

    GroupsContent(
        state = state,
        onSelectGroup = viewModel::selectGroup,
        onCreateGroupRequested = { viewModel.createGroup("New group") },
    )
}
