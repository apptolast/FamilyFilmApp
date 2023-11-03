package com.digitalsolution.familyfilmapp.ui.screens.groups

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.digitalsolution.familyfilmapp.model.local.GroupInfo
import com.digitalsolution.familyfilmapp.ui.screens.groups.components.BottomSheetGroupScreenContent
import com.digitalsolution.familyfilmapp.ui.screens.groups.components.GroupCard
import com.digitalsolution.familyfilmapp.ui.screens.groups.states.GroupBackendState
import com.digitalsolution.familyfilmapp.ui.screens.groups.states.GroupUiState
import com.digitalsolution.familyfilmapp.ui.theme.FamilyFilmAppTheme
import kotlinx.coroutines.launch

const val CARD_HEIGHT = 0.75

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

    if (addMemberUiState?.showSnackbar?.value == true) {
        Box(modifier = Modifier.fillMaxSize()) {
            Snackbar(
                action = {
                },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
                    .background(Color.Blue, shape = RoundedCornerShape(4.dp))
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                actionOnNewLine = false,
                containerColor = Color.Transparent,
            ) {
                Text(groupBackendState.addMemberInfoMessage, color = Color.White)
            }
        }
    }

    if (addMemberUiState?.isBottomSheetVisible?.value == true) {
        ModalBottomSheet(
            modifier = Modifier
                .fillMaxSize()
                .height((LocalConfiguration.current.screenHeightDp * CARD_HEIGHT).dp),
            onDismissRequest = { addMemberUiState?.isBottomSheetVisible!!.value = false },
            sheetState = sheetState,
            shape = RoundedCornerShape(40.dp, 40.dp, 0.dp, 0.dp),
            containerColor = Color(255, 224, 126),
            tonalElevation = 10.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 36.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                BottomSheetGroupScreenContent(
                    addMemberUiState = addMemberUiState!!,
                    onCLickAddMember = viewModel::addGroupMember,
                )
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
    BackHandler {
        coroutineModalBottomSheetScope.launch {
            sheetState.hide()
        }
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
