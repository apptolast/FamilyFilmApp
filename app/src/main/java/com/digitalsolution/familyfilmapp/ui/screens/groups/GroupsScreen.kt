package com.digitalsolution.familyfilmapp.ui.screens.groups

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.digitalsolution.familyfilmapp.model.local.GroupData
import com.digitalsolution.familyfilmapp.ui.screens.groups.components.GroupCard

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

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(groups) { groupData ->
            GroupCard(groupData = groupData)
        }
    }

}



