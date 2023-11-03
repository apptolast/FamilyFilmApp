package com.digitalsolution.familyfilmapp.ui.screens.groups

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.digitalsolution.familyfilmapp.R
import com.digitalsolution.familyfilmapp.model.local.GroupInfo
import com.digitalsolution.familyfilmapp.ui.components.BottomBar
import com.digitalsolution.familyfilmapp.ui.components.TopBar
import com.digitalsolution.familyfilmapp.ui.screens.groups.components.GroupCard
import com.digitalsolution.familyfilmapp.ui.screens.groups.states.GroupBackendState
import com.digitalsolution.familyfilmapp.ui.screens.groups.states.GroupUiState
import com.digitalsolution.familyfilmapp.ui.theme.FamilyFilmAppTheme

@Composable
fun GroupsScreen(
    navController: NavController,
    viewModel: GroupViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val groupBackendState by viewModel.state.collectAsStateWithLifecycle()
    val groupUiState by viewModel.groupUIState.observeAsState()

    Scaffold(
        topBar = { TopBar() },
        bottomBar = { BottomBar(navController = navController) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(text = stringResource(id = R.string.groups_text_add)) },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(id = R.string.groups_text_add),
                    )
                },
                onClick = {
                    Toast.makeText(context, "TODO: FFA-94", Toast.LENGTH_SHORT).show()
                    viewModel.addGroup()
                },
            )
        },
        floatingActionButtonPosition = FabPosition.End,
    ) { paddingValues ->
        GroupContent(
            groupBackendState,
            groupUiState = groupUiState!!,
            onClickRemoveMember = {},
            onCLickSwipeCard = {},
            onAddMemberClick = {},
            onDeleteGroupClick = {},
            onChangeGroupName = {},
            modifier = Modifier.padding(paddingValues),
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
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        GroupCard(
            groupTitle = "Worker Dudes", // FIXME: harcoded name
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

@Preview(showSystemUi = true)
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
