package com.digitalsolution.familyfilmapp.ui.screens.groups

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.digitalsolution.familyfilmapp.model.local.GroupInfo
import com.digitalsolution.familyfilmapp.ui.screens.groups.components.BottomSheetGroupScreen
import com.digitalsolution.familyfilmapp.ui.screens.groups.components.GroupCard
import com.digitalsolution.familyfilmapp.ui.screens.groups.states.GroupBackendState
import com.digitalsolution.familyfilmapp.ui.screens.groups.states.GroupUiState
import com.digitalsolution.familyfilmapp.ui.theme.FamilyFilmAppTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsScreen(
    navController: NavController,
    viewModel: GroupViewModel = hiltViewModel(),
) {
    val coroutineModalBottomSheetScope = rememberCoroutineScope()

    val sheetState = rememberModalBottomSheetState()

    val groupBackendState by viewModel.state.collectAsStateWithLifecycle()

    val groupUiState by viewModel.groupUIState.observeAsState()

    val addMemberUiState by viewModel.addMemberUIState.observeAsState()

    if (addMemberUiState?.isBottomSheetVisible?.value == true) {
        ModalBottomSheet(
            onDismissRequest = { /*TODO*/ },
            sheetState = sheetState,
            shape = RoundedCornerShape(40.dp, 40.dp, 0.dp, 0.dp),
            tonalElevation = 10.dp,
        ) {
            BottomSheetGroupScreen {
                coroutineModalBottomSheetScope.launch {
                    sheetState.hide()
                }
            }
        }
    }
    groupUiState?.let {
        GroupContent(
            groupBackendState,
            groupUiState = it,
            onClickRemoveMember = {},
            onCLickSwipeCard = {},
            onAddMemberClick = {
                addMemberUiState?.isBottomSheetVisible!!.value = true
            },
            onDeleteGroupClick = {},
            onChangeGroupName = {},
        )
    }
}

@Composable
fun GroupContent(
    groupBackendState: GroupBackendState,
    groupUiState: GroupUiState,
    onClickRemoveMember: (GroupInfo) -> Unit,
    onAddMemberClick: () -> Unit,
    onDeleteGroupClick: () -> Unit,
    onCLickSwipeCard: (GroupInfo) -> Unit,
    onChangeGroupName: (String) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        GroupCard(
            groupTitle = "Worker Dudes",
            groupUiState = groupUiState,
            members = groupBackendState.groupsInfo,
            onRemoveMemberClick = onClickRemoveMember,
            onSwipeDelete = onCLickSwipeCard,
            onAddMemberClick = onAddMemberClick,
            onDeleteGroupClick = onDeleteGroupClick,
            onChangeGroupName = onChangeGroupName,
        )
    }
}

@Preview
@Composable
fun GroupContentPreview() {
    FamilyFilmAppTheme {
        GroupContent(
            groupBackendState = GroupBackendState(),
            groupUiState = GroupUiState(),
            onClickRemoveMember = { _ -> },
            onAddMemberClick = {},
            onDeleteGroupClick = {},
            onCLickSwipeCard = { _ -> },
            onChangeGroupName = {},
        )
    }
}
