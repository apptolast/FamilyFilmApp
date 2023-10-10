package com.digitalsolution.familyfilmapp.ui.screens.groups

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.digitalsolution.familyfilmapp.model.local.GroupData

@Composable
fun GroupsScreen(
    navController: NavController,
    viewModel: GroupViewModel = hiltViewModel()
) {
    val groupUiState by viewModel.state.collectAsStateWithLifecycle()

    GroupContent(groupUiState.groups)
}


@Composable
fun GroupContent(groups: List<GroupData>) {


}



