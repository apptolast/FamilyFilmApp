package com.digitalsolution.familyfilmapp.ui.screens.groups

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.digitalsolution.familyfilmapp.SharedViewModel
import com.digitalsolution.familyfilmapp.model.local.MemeberData
import com.digitalsolution.familyfilmapp.ui.screens.groups.components.GroupMembersCard

@Composable
fun GroupsScreen(
    navController: NavController,
    viewModel: GroupViewModel = hiltViewModel(),
    sharedViewModel: SharedViewModel = hiltViewModel()
) {
    val list = sharedViewModel.getMembers()

    GroupContent(
        list,
        onClickRemoveMember = {},
        onCLickSwipeCard = {}
    )
}

@Composable
fun GroupContent(
    members: List<MemeberData>,
    onClickRemoveMember: (MemeberData) -> Unit,
    onCLickSwipeCard: (MemeberData) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GroupMembersCard(
            groupTitle = "Group 0",
            members = members,
            onRemoveMemberClick = onClickRemoveMember,
            onSwipeDelete = onCLickSwipeCard
        )
    }
}



