package com.digitalsolution.familyfilmapp.ui.screens.groups

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.digitalsolution.familyfilmapp.R
import com.digitalsolution.familyfilmapp.model.local.Group
import com.digitalsolution.familyfilmapp.ui.components.BottomBar
import com.digitalsolution.familyfilmapp.ui.components.tabgroups.TabGroupsViewModel
import com.digitalsolution.familyfilmapp.ui.components.tabgroups.TopBar
import com.digitalsolution.familyfilmapp.ui.screens.groups.components.GroupCard
import com.digitalsolution.familyfilmapp.ui.screens.groups.states.GroupBackendState
import com.digitalsolution.familyfilmapp.ui.screens.groups.states.GroupUiState
import com.digitalsolution.familyfilmapp.ui.screens.login.components.SupportingErrorText
import com.digitalsolution.familyfilmapp.ui.theme.FamilyFilmAppTheme

@Composable
fun GroupsScreen(
    navController: NavController,
    viewModel: GroupViewModel = hiltViewModel(),
    tabViewmodel: TabGroupsViewModel = hiltViewModel(),
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val groupBackendState by viewModel.state.collectAsStateWithLifecycle()
    val groupUiState by viewModel.groupUIState.observeAsState()

    var showGroupNameDialog by rememberSaveable {
        mutableStateOf(false)
    }

    if (!groupBackendState.errorMessage?.error.isNullOrBlank()) {
        LaunchedEffect(groupBackendState.errorMessage) {
            snackBarHostState.showSnackbar(
                groupBackendState.errorMessage!!.error,
                null,
                false,
                SnackbarDuration.Short,
            )
            tabViewmodel.init()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = { TopBar(tabViewmodel) },
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
                    showGroupNameDialog = true
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

        if (showGroupNameDialog) {
            AddGroupDialog(
                dismissDialog = {
                    showGroupNameDialog = false
                },
                addGroup = { groupName ->
                    viewModel.addGroup(groupName)
                },
            )
        }
    }
}

@Composable
fun GroupContent(
    groupBackendState: GroupBackendState,
    groupUiState: GroupUiState,
    onClickRemoveMember: (Group) -> Unit,
    onAddMemberClick: () -> Unit,
    onDeleteGroupClick: () -> Unit,
    onCLickSwipeCard: (Group) -> Unit,
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

@Composable
fun AddGroupDialog(
    dismissDialog: () -> Unit,
    addGroup: (String) -> Unit,
) {
    val errorMessage = stringResource(id = R.string.group_dialog_name_empty_error_message)
    var groupName by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = {
            dismissDialog()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (groupName.isNotBlank()) {
                        addGroup(groupName)
                        dismissDialog()
                    }
                },
            ) {
                Text("Confirm")
            }
        },
        title = {
            Text("Set group name")
        },
        text = {
            OutlinedTextField(
                value = groupName,
                onValueChange = {
                    groupName = it
                },
                label = { Text("Group name") },
                singleLine = true,
                isError = groupName.isBlank(),
                supportingText = {
                    if (groupName.isBlank()) {
                        SupportingErrorText(errorMessage)
                    }
                },
            )
        },
    )
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
