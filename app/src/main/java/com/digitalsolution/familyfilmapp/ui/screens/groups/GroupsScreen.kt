package com.digitalsolution.familyfilmapp.ui.screens.groups

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.digitalsolution.familyfilmapp.R
import com.digitalsolution.familyfilmapp.model.local.Group
import com.digitalsolution.familyfilmapp.ui.components.BottomBar
import com.digitalsolution.familyfilmapp.ui.components.dialogs.BasicDialog
import com.digitalsolution.familyfilmapp.ui.components.dialogs.TextFieldDialog
import com.digitalsolution.familyfilmapp.ui.components.tabgroups.TabGroupsViewModel
import com.digitalsolution.familyfilmapp.ui.components.tabgroups.TopBar
import com.digitalsolution.familyfilmapp.ui.screens.groups.components.GroupCard
import com.digitalsolution.familyfilmapp.ui.screens.groups.states.GroupUiState
import com.digitalsolution.familyfilmapp.ui.screens.login.components.SupportingErrorText
import com.digitalsolution.familyfilmapp.ui.theme.FamilyFilmAppTheme
import com.digitalsolution.familyfilmapp.utils.Constants

@Composable
fun GroupsScreen(
    navController: NavController,
    viewModel: GroupViewModel = hiltViewModel(),
    tabViewmodel: TabGroupsViewModel = hiltViewModel(),
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val groupBackendState by viewModel.state.collectAsStateWithLifecycle()
    val groupUiState by viewModel.groupUIState.collectAsStateWithLifecycle()
    var selectedGroup by remember { mutableStateOf<Group?>(null) }

    var showGroupNameDialog by rememberSaveable {
        mutableStateOf(false)
    }

    if (!groupBackendState.errorMessage?.error.isNullOrBlank()) {
        LaunchedEffect(groupBackendState.errorMessage) {
            snackBarHostState.showSnackbar(
                groupBackendState.errorMessage!!.error,
                null,
                true,
            )
            // Puede ser necesario actualizar también el TabGroupsViewModel
            tabViewmodel.refreshGroups()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            TopBar(tabViewmodel)
        },
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
            selectedGroup = selectedGroup,
            groupUiState = groupUiState,
            onCLickSwipeCard = {},
            onClickRemoveMember = {},
            onAddMemberClick = {},
            onDeleteGroupClick = {
                viewModel.deleteGroup(selectedGroup!!.id)
            },
            onChangeGroupName = {},
            modifier = Modifier.padding(paddingValues),
        )

        // Dialog to change group name
        if (showGroupNameDialog) {
            TextFieldDialog(
                title = "Set group name",
                description = "Group name",
                onConfirm = { groupName ->
                    viewModel.addGroup(groupName)
                },
                onDismiss = {
                    showGroupNameDialog = false
                },
            )
        }

        if (groupBackendState.isLoading) {
            Column(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .testTag(Constants.CIRCULAR_PROGRESS_INDICATOR),
                )
            }
        }
    }
}

@Composable
fun GroupContent(
    selectedGroup: Group?,
    groupUiState: GroupUiState,
    onClickRemoveMember: (Group) -> Unit,
    onAddMemberClick: () -> Unit,
    onDeleteGroupClick: () -> Unit,
    onCLickSwipeCard: (Group) -> Unit,
    onChangeGroupName: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDeleteGroupDialog by rememberSaveable {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        GroupCard(
            groupTitle = selectedGroup?.name ?: "No name",
            groupUiState = groupUiState,
            members = emptyList(), // FIXME: Members not serialized
            onRemoveMemberClick = onClickRemoveMember,
            onSwipeDelete = onCLickSwipeCard,
            onAddMemberClick = onAddMemberClick,
            onDeleteGroupClick = {
                showDeleteGroupDialog = true
            },
            onChangeGroupName = onChangeGroupName,
        )
    }

    // Dialog to delete the group
    if (showDeleteGroupDialog) {
        BasicDialog(
            title = stringResource(R.string.dialog_delete_group_title),
            description = stringResource(R.string.dialog_delete_group_description),
            confirmButtonText = stringResource(id = android.R.string.ok),
            cancelButtonText = stringResource(id = android.R.string.cancel),
            onConfirm = onDeleteGroupClick,
            onDismiss = { showDeleteGroupDialog = false },
        )
    }
}

@Composable
fun AddGroupDialog(dismissDialog: () -> Unit, addGroup: (String) -> Unit) {
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
private fun GroupContentPreview() {
    FamilyFilmAppTheme {
        GroupContent(
            selectedGroup = Group(),
            groupUiState = GroupUiState(),
            onClickRemoveMember = { _ -> },
            onAddMemberClick = {},
            onDeleteGroupClick = {},
            onCLickSwipeCard = { _ -> },
            onChangeGroupName = {},
        )
    }
}
