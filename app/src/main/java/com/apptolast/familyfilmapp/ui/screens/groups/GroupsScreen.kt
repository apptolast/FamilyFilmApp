package com.apptolast.familyfilmapp.ui.screens.groups

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.apptolast.familyfilmapp.R
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.ui.components.BottomBar
import com.apptolast.familyfilmapp.ui.components.dialogs.BasicDialog
import com.apptolast.familyfilmapp.ui.components.dialogs.EmailFieldDialog
import com.apptolast.familyfilmapp.ui.components.dialogs.TextFieldDialog
import com.apptolast.familyfilmapp.ui.screens.groups.GroupViewModel.GroupScreenDialogs
import com.apptolast.familyfilmapp.ui.screens.groups.components.GroupCard
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import kotlinx.coroutines.DelicateCoroutinesApi

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun GroupsScreen(navController: NavController, viewModel: GroupViewModel = hiltViewModel()) {
    val snackBarHostState = remember { SnackbarHostState() }

    val backendState by viewModel.backendState.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (!backendState.errorMessage.isNullOrBlank()) {
        Toast.makeText(
            LocalContext.current,
            backendState.errorMessage,
            Toast.LENGTH_SHORT,
        ).show()

        viewModel.clearErrorMessage()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        bottomBar = { BottomBar(navController = navController) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(text = stringResource(id = R.string.groups_text_create_group)) },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(id = R.string.groups_text_create_group),
                    )
                },
                onClick = {
                    viewModel.showDialog(GroupScreenDialogs.CreateGroup)
                },
            )
        },
        floatingActionButtonPosition = FabPosition.End,
    ) { paddingValues ->

        if (backendState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else {
            GroupContent(
                userOwner = backendState.currentUser,
                groups = backendState.groups,
                selectedGroupIndex = uiState.selectedGroupIndex,
                modifier = Modifier.padding(paddingValues),
                onChangeGroupName = { group ->
                    viewModel.showDialog(GroupScreenDialogs.ChangeGroupName(group))
                },
                onAddMemberClick = { group ->
                    viewModel.showDialog(GroupScreenDialogs.AddMember(group))
                },
                onDeleteGroup = { group ->
                    viewModel.showDialog(GroupScreenDialogs.DeleteGroup(group))
                },
                onDeleteUser = { group, user ->
                    viewModel.deleteMember(group, user)
                },
                onGroupSelect = { index ->
                    viewModel.selectGroup(index)
                },
            )

            // Show dialog
            when (uiState.showDialog) {
                GroupScreenDialogs.CreateGroup -> {
                    TextFieldDialog(
                        title = stringResource(id = R.string.dialog_create_group_title),
                        description = stringResource(id = R.string.dialog_create_group_description),
                        onConfirm = { groupName ->
                            viewModel.createGroup(groupName)
                        },
                        onDismiss = {
                            viewModel.showDialog(GroupScreenDialogs.None)
                        },
                    )
                }

                is GroupScreenDialogs.AddMember -> {
                    val group = (uiState.showDialog as GroupScreenDialogs.AddMember).group

                    EmailFieldDialog(
                        title = stringResource(id = R.string.dialog_add_group_member_title),
                        onConfirm = { email ->
                            viewModel.addMember(group, email)
                        },
                        onDismiss = {
                            viewModel.showDialog(GroupScreenDialogs.None)
                        },
                    )
                }

                is GroupScreenDialogs.ChangeGroupName -> {
                    val group = (uiState.showDialog as GroupScreenDialogs.ChangeGroupName).group

                    TextFieldDialog(
                        title = stringResource(id = R.string.dialog_change_group_title),
                        description = stringResource(id = R.string.dialog_change_group_description),
                        onConfirm = { newGroupName ->
                            viewModel.changeGroupName(group.copy(name = newGroupName))
                        },
                        onDismiss = {
                            viewModel.showDialog(GroupScreenDialogs.None)
                        },
                    )
                }

                is GroupScreenDialogs.DeleteGroup -> {
                    val group = (uiState.showDialog as GroupScreenDialogs.DeleteGroup).group
                    BasicDialog(
                        title = stringResource(R.string.dialog_delete_group_title),
                        description = stringResource(R.string.dialog_delete_group_description),
                        confirmButtonText = stringResource(id = android.R.string.ok),
                        cancelButtonText = stringResource(id = android.R.string.cancel),
                        onConfirm = { viewModel.deleteGroup(group) },
                        onDismiss = { viewModel.showDialog(GroupScreenDialogs.None) },
                    )
                }

                GroupScreenDialogs.None -> {
                    /* no-op */
                }

                else -> {}
            }
        }
    }
}

@Composable
fun GroupContent(
    userOwner: User,
    groups: List<Group>,
    selectedGroupIndex: Int,
    modifier: Modifier = Modifier,
    onChangeGroupName: (Group) -> Unit = {},
    onAddMemberClick: (Group) -> Unit = {},
    onDeleteGroup: (Group) -> Unit = {},
    onDeleteUser: (Group, User) -> Unit = { _, _ -> },
    onGroupSelect: (Int) -> Unit = {},
) {
    if (groups.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = (-70).dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Create a group",
                style = MaterialTheme.typography.headlineMedium,
            )
        }
    } else {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Group tabs
            ScrollableTabRow(
                selectedTabIndex = selectedGroupIndex,
                divider = {
                    VerticalDivider()
                },
            ) {
                groups.forEachIndexed { index, group ->
                    Tab(
                        selected = selectedGroupIndex == index,
                        onClick = { onGroupSelect(index) },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        selectedContentColor = MaterialTheme.colorScheme.surfaceVariant,
                        unselectedContentColor = MaterialTheme.colorScheme.surfaceDim,
                        text = {
                            Text(
                                text = group.name,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                style = if (selectedGroupIndex == index) {
                                    MaterialTheme.typography.titleSmall
                                } else {
                                    MaterialTheme.typography.titleMedium
                                },
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        },
                    )
                }
            }

            val currentSelectedGroup = groups[selectedGroupIndex]

            GroupCard(
                userOwner = userOwner,
                group = currentSelectedGroup,
                modifier = Modifier.padding(vertical = 12.dp),
                onChangeGroupName = {
                    onChangeGroupName(currentSelectedGroup)
                },
                onAddMember = {
                    onAddMemberClick(currentSelectedGroup)
                },
                onDeleteGroup = {
                    onDeleteGroup(currentSelectedGroup)
                },
                onDeleteUser = { user ->
                    onDeleteUser(currentSelectedGroup, user)
                },
            )
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true)
@Composable
private fun GroupContentEmptyPreview() {
    FamilyFilmAppTheme {
        GroupContent(
            userOwner = User(),
            groups = emptyList(),
            onChangeGroupName = {},
            onAddMemberClick = {},
            onDeleteGroup = {},
            onDeleteUser = { _, _ -> },
            onGroupSelect = { _ -> },
            selectedGroupIndex = 0,
        )
    }
}

@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true)
@Composable
private fun GroupContentPreview() {
    FamilyFilmAppTheme {
        GroupContent(
            userOwner = User(),
            groups = listOf(
                Group().copy(name = "name 1"),
                Group().copy(name = "name 2"),
                Group().copy(name = "name 3"),
            ),
            selectedGroupIndex = 0,
        )
    }
}
