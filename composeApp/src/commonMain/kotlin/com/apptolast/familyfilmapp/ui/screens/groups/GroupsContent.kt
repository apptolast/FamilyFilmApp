package com.apptolast.familyfilmapp.ui.screens.groups

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.SyncState
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.ui.components.dialogs.TextFieldDialog
import com.apptolast.familyfilmapp.ui.screens.groups.components.GroupListCard
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import com.apptolast.familyfilmapp.utils.TT_GROUPS_EMPTY_TEXT
import com.apptolast.familyfilmapp.utils.TT_GROUPS_FAB
import familyfilmkmp.composeapp.generated.resources.Res
import familyfilmkmp.composeapp.generated.resources.dialog_create_group_description
import familyfilmkmp.composeapp.generated.resources.dialog_create_group_title
import familyfilmkmp.composeapp.generated.resources.groups_empty_title
import familyfilmkmp.composeapp.generated.resources.groups_removed_from_group
import familyfilmkmp.composeapp.generated.resources.groups_text_create_group
import familyfilmkmp.composeapp.generated.resources.sync_state_error
import familyfilmkmp.composeapp.generated.resources.sync_state_offline
import familyfilmkmp.composeapp.generated.resources.sync_state_syncing
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsContent(
    state: GroupsViewModel.GroupsState,
    modifier: Modifier = Modifier,
    onOpenGroup: (String) -> Unit = {},
    onShowDialog: (GroupsScreenDialog) -> Unit = {},
    onCreateGroup: (String) -> Unit = {},
    onClearError: () -> Unit = {},
    onRemovedFromGroupHandled: () -> Unit = {},
) {
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.error) {
        if (!state.error.isNullOrBlank()) {
            snackBarHostState.showSnackbar(state.error)
            onClearError()
        }
    }

    val removedFromGroupMessage = stringResource(Res.string.groups_removed_from_group)
    LaunchedEffect(state.removedFromGroup) {
        if (state.removedFromGroup) {
            snackBarHostState.showSnackbar(removedFromGroupMessage)
            onRemovedFromGroupHandled()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        floatingActionButton = {
            ExpandableFAB(
                isExtended = state.summaries.isEmpty(),
                onClick = { onShowDialog(GroupsScreenDialog.CreateGroup) },
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        modifier = modifier,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                state.summaries.isEmpty() -> {
                    Text(
                        text = stringResource(Res.string.groups_empty_title),
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(y = (-70).dp)
                            .testTag(TT_GROUPS_EMPTY_TEXT),
                    )
                }

                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 320.dp),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            SyncStateIndicator(syncState = state.syncState)
                        }
                        items(
                            items = state.summaries,
                            key = { summary -> summary.group.id },
                        ) { summary ->
                            GroupListCard(
                                summary = summary,
                                onClick = { onOpenGroup(summary.group.id) },
                            )
                        }
                    }
                }
            }
        }

        when (state.showDialog) {
            GroupsScreenDialog.CreateGroup -> {
                TextFieldDialog(
                    title = stringResource(Res.string.dialog_create_group_title),
                    description = stringResource(Res.string.dialog_create_group_description),
                    onConfirm = { groupName -> onCreateGroup(groupName) },
                    onDismiss = { onShowDialog(GroupsScreenDialog.None) },
                )
            }

            GroupsScreenDialog.None -> Unit
        }
    }
}

@Composable
fun ExpandableFAB(isExtended: Boolean, onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier.testTag(TT_GROUPS_FAB),
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
    ) {
        AnimatedContent(
            targetState = isExtended,
            label = "Fab content",
            transitionSpec = {
                (slideInHorizontally { height -> height } + fadeIn()).togetherWith(
                    slideOutHorizontally { height -> -height } + fadeOut(),
                ).using(SizeTransform(clip = false))
            },
        ) { targetState ->
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(Res.string.groups_text_create_group),
                )
                if (targetState) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(Res.string.groups_text_create_group),
                        modifier = Modifier.padding(end = 8.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun SyncStateIndicator(syncState: SyncState, modifier: Modifier = Modifier) {
    AnimatedVisibility(
        visible = syncState !is SyncState.Synced,
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            when (syncState) {
                is SyncState.Syncing -> {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(end = 8.dp).width(16.dp).padding(2.dp),
                        strokeWidth = 2.dp,
                    )
                    Text(
                        text = stringResource(Res.string.sync_state_syncing),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                is SyncState.Error -> {
                    Text(
                        text = "${stringResource(Res.string.sync_state_error)}: ${syncState.message}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }

                is SyncState.Offline -> {
                    Text(
                        text = stringResource(Res.string.sync_state_offline),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                is SyncState.Synced -> Unit
            }
        }
    }
}

@Preview(name = "Empty", device = Devices.PHONE, showBackground = true)
@Composable
private fun PreviewGroupsContentEmpty() {
    FamilyFilmAppTheme {
        GroupsContent(
            state = GroupsViewModel.GroupsState(isLoading = false),
        )
    }
}

@Preview(name = "Phone", device = Devices.PHONE, showBackground = true)
@Preview(name = "Tablet", device = Devices.TABLET, showBackground = true)
@Composable
private fun PreviewGroupsContent() {
    FamilyFilmAppTheme {
        GroupsContent(
            state = GroupsViewModel.GroupsState(
                isLoading = false,
                summaries = listOf(
                    previewGroupSummary("g1", "Friday Night", 3),
                    previewGroupSummary("g2", "Date Night", 2),
                    previewGroupSummary("g3", "Family Time", 5),
                ),
            ),
        )
    }
}

private fun previewGroupSummary(groupId: String, name: String, count: Int): GroupSummary {
    val users = (1..count).map { index ->
        User(
            id = "$groupId-u$index",
            email = "user$index@example.com",
            language = "en",
            photoUrl = "",
            username = listOf("Alex", "Sara", "Mario", "Laura", "Nora").getOrElse(index - 1) { "User $index" },
        )
    }
    return GroupSummary(
        group = Group(
            id = groupId,
            ownerId = users.firstOrNull()?.id.orEmpty(),
            name = name,
            users = users.map { it.id },
            lastUpdated = null,
        ),
        members = users,
    )
}
