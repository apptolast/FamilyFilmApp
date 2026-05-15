package com.apptolast.familyfilmapp.ui.screens.groups

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.Media
import com.apptolast.familyfilmapp.model.local.SyncState
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.ui.components.dialogs.BasicDialog
import com.apptolast.familyfilmapp.ui.components.dialogs.EmailFieldDialog
import com.apptolast.familyfilmapp.ui.components.dialogs.TextFieldDialog
import com.apptolast.familyfilmapp.ui.screens.groups.GroupViewModel.GroupScreenDialogs
import com.apptolast.familyfilmapp.ui.screens.groups.components.GroupCard
import com.apptolast.familyfilmapp.ui.screens.groups.components.HorizontalScrollableMedia
import com.apptolast.familyfilmapp.ui.screens.home.MediaItem
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import com.apptolast.familyfilmapp.utils.TT_GROUPS_EMPTY_TEXT
import com.apptolast.familyfilmapp.utils.TT_GROUPS_FAB
import com.apptolast.familyfilmapp.utils.TT_GROUPS_TAB
import familyfilmkmp.composeapp.generated.resources.Res
import familyfilmkmp.composeapp.generated.resources.dialog_add_group_member_title
import familyfilmkmp.composeapp.generated.resources.dialog_cancel
import familyfilmkmp.composeapp.generated.resources.dialog_change_group_description
import familyfilmkmp.composeapp.generated.resources.dialog_change_group_title
import familyfilmkmp.composeapp.generated.resources.dialog_create_group_description
import familyfilmkmp.composeapp.generated.resources.dialog_create_group_title
import familyfilmkmp.composeapp.generated.resources.dialog_delete_group_description
import familyfilmkmp.composeapp.generated.resources.dialog_delete_group_title
import familyfilmkmp.composeapp.generated.resources.dialog_ok
import familyfilmkmp.composeapp.generated.resources.group_recommended_label
import familyfilmkmp.composeapp.generated.resources.groups_text_create_group
import familyfilmkmp.composeapp.generated.resources.groups_text_to_watch
import familyfilmkmp.composeapp.generated.resources.groups_text_watched
import familyfilmkmp.composeapp.generated.resources.sync_state_error
import familyfilmkmp.composeapp.generated.resources.sync_state_offline
import familyfilmkmp.composeapp.generated.resources.sync_state_syncing
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsContent(
    state: GroupViewModel.GroupsState,
    onSelectGroup: (String) -> Unit,
    onShowDialog: (GroupScreenDialogs) -> Unit,
    onCreateGroup: (String) -> Unit,
    onAddMember: (groupId: String, identifier: String) -> Unit,
    onChangeGroupName: (Group) -> Unit,
    onDeleteGroup: (String) -> Unit,
    onRemoveMember: (groupId: String, userId: String) -> Unit,
    onMediaClick: (Media) -> Unit,
    onClearError: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()
    val isFabExtended by remember {
        derivedStateOf { listState.firstVisibleItemIndex == 0 }
    }

    LaunchedEffect(state.error) {
        if (!state.error.isNullOrBlank()) {
            snackBarHostState.showSnackbar(state.error)
            onClearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        floatingActionButton = {
            ExpandableFAB(
                isExtended = isFabExtended,
                onClick = { onShowDialog(GroupScreenDialogs.CreateGroup) },
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        modifier = modifier,
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else {
            val selectedGroupData = state.selectedGroupData
            if (selectedGroupData != null) {
                GroupContent(
                    groupData = selectedGroupData,
                    groups = state.groups,
                    selectedGroupIndex = state.selectedGroupIndex,
                    syncState = state.syncState,
                    scrollState = listState,
                    modifier = Modifier.consumeWindowInsets(paddingValues),
                    onChangeGroupName = { group ->
                        onShowDialog(GroupScreenDialogs.ChangeGroupName(group))
                    },
                    onAddMemberClick = { group ->
                        onShowDialog(GroupScreenDialogs.AddMember(group))
                    },
                    onDeleteGroup = { groupId ->
                        onShowDialog(
                            GroupScreenDialogs.DeleteGroup(
                                state.groups.first { it.id == groupId },
                            ),
                        )
                    },
                    onDeleteUser = { groupId, userId -> onRemoveMember(groupId, userId) },
                    onGroupSelect = onSelectGroup,
                    onMediaClick = onMediaClick,
                )
            } else if (state.groups.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .offset(y = (-70).dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(Res.string.groups_text_create_group),
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.testTag(TT_GROUPS_EMPTY_TEXT),
                    )
                }
            }
        }

        when (val dialog = state.showDialog) {
            GroupScreenDialogs.CreateGroup -> {
                TextFieldDialog(
                    title = stringResource(Res.string.dialog_create_group_title),
                    description = stringResource(Res.string.dialog_create_group_description),
                    onConfirm = { groupName -> onCreateGroup(groupName) },
                    onDismiss = { onShowDialog(GroupScreenDialogs.None) },
                )
            }

            is GroupScreenDialogs.AddMember -> {
                val group = dialog.group
                EmailFieldDialog(
                    title = stringResource(Res.string.dialog_add_group_member_title),
                    onConfirm = { email -> onAddMember(group.id, email) },
                    onDismiss = { onShowDialog(GroupScreenDialogs.None) },
                )
            }

            is GroupScreenDialogs.ChangeGroupName -> {
                val group = dialog.group
                TextFieldDialog(
                    title = stringResource(Res.string.dialog_change_group_title),
                    description = stringResource(Res.string.dialog_change_group_description),
                    onConfirm = { newGroupName ->
                        onChangeGroupName(group.copy(name = newGroupName))
                    },
                    onDismiss = { onShowDialog(GroupScreenDialogs.None) },
                )
            }

            is GroupScreenDialogs.DeleteGroup -> {
                val group = dialog.group
                BasicDialog(
                    title = stringResource(Res.string.dialog_delete_group_title),
                    description = stringResource(Res.string.dialog_delete_group_description),
                    confirmButtonText = stringResource(Res.string.dialog_ok),
                    cancelButtonText = stringResource(Res.string.dialog_cancel),
                    onConfirm = { onDeleteGroup(group.id) },
                    onDismiss = { onShowDialog(GroupScreenDialogs.None) },
                )
            }

            GroupScreenDialogs.None -> Unit
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GroupContent(
    groupData: GroupViewModel.GroupData,
    groups: List<Group>,
    selectedGroupIndex: Int,
    syncState: SyncState,
    scrollState: LazyListState,
    modifier: Modifier = Modifier,
    onChangeGroupName: (Group) -> Unit = {},
    onAddMemberClick: (Group) -> Unit = {},
    onDeleteGroup: (String) -> Unit = {},
    onDeleteUser: (String, String) -> Unit = { _, _ -> },
    onGroupSelect: (String) -> Unit = {},
    onMediaClick: (Media) -> Unit = {},
) {
    if (groups.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = (-70).dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(Res.string.groups_text_create_group),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.testTag(TT_GROUPS_EMPTY_TEXT),
            )
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            state = scrollState,
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            stickyHeader {
                val safeTabIndex = when {
                    groups.isEmpty() -> 0
                    selectedGroupIndex >= groups.size -> groups.size - 1
                    selectedGroupIndex < 0 -> 0
                    else -> selectedGroupIndex
                }

                ScrollableTabRow(
                    selectedTabIndex = safeTabIndex,
                    divider = { VerticalDivider() },
                ) {
                    groups.forEachIndexed { index, group ->
                        Tab(
                            selected = safeTabIndex == index,
                            onClick = { onGroupSelect(group.id) },
                            modifier = Modifier
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("${TT_GROUPS_TAB}_$index"),
                            selectedContentColor = MaterialTheme.colorScheme.primary,
                            unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            text = {
                                Text(
                                    text = group.name,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    style = if (safeTabIndex == index) {
                                        MaterialTheme.typography.titleMedium
                                    } else {
                                        MaterialTheme.typography.titleSmall
                                    },
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                            },
                        )
                    }
                }
            }

            item {
                SyncStateIndicator(syncState = syncState)
            }

            item {
                AnimatedVisibility(visible = groupData.recommendedMedia != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = stringResource(Res.string.group_recommended_label),
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 8.dp),
                            textAlign = TextAlign.Center,
                        )
                        groupData.recommendedMedia?.let { media ->
                            MediaItem(
                                media = media,
                                onClick = onMediaClick,
                                status = null,
                                modifier = Modifier.fillMaxWidth(0.6f),
                            )
                        }
                    }
                }
            }

            item {
                val currentUser = groupData.members.firstOrNull { it.id == groupData.currentUserId }
                if (currentUser != null) {
                    GroupCard(
                        userOwner = currentUser,
                        group = groupData.group,
                        groupUsers = groupData.members,
                        modifier = Modifier.padding(vertical = 12.dp),
                        onChangeGroupName = { onChangeGroupName(groupData.group) },
                        onAddMember = { onAddMemberClick(groupData.group) },
                        onDeleteGroup = { onDeleteGroup(groupData.group.id) },
                        onDeleteUser = { user -> onDeleteUser(groupData.group.id, user.id) },
                    )
                }
            }

            if (groupData.mediaToWatch.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(Res.string.groups_text_to_watch),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(12.dp),
                    )
                }
                item {
                    HorizontalScrollableMedia(
                        mediaList = groupData.mediaToWatch,
                        onMediaClick = onMediaClick,
                    )
                }
            }

            if (groupData.mediaWatched.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(Res.string.groups_text_watched),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(12.dp),
                    )
                }
                item {
                    HorizontalScrollableMedia(
                        mediaList = groupData.mediaWatched,
                        onMediaClick = onMediaClick,
                    )
                }
            }
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
                    contentDescription = "Add",
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
fun SyncStateIndicator(syncState: SyncState) {
    AnimatedVisibility(
        visible = syncState !is SyncState.Synced,
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

@Preview(showBackground = true)
@Composable
private fun PreviewGroupContent() {
    FamilyFilmAppTheme {
        val testGroup = Group().copy(id = "1", name = "Test Group")
        val testGroupData = GroupViewModel.GroupData(
            group = testGroup,
            members = listOf(
                User().copy(id = "1", email = "a@a.com"),
                User().copy(id = "2", email = "b@b.com"),
            ),
            mediaToWatch = listOf(
                Media().copy(id = 1, title = "Title 1", overview = "Description 1"),
                Media().copy(id = 2, title = "Title 2", overview = "Description 2"),
            ),
            mediaWatched = listOf(
                Media().copy(id = 4, title = "Title 4", overview = "Description 4"),
            ),
            recommendedMedia = Media().copy(id = 1, title = "Recommended"),
            currentUserId = "1",
        )

        GroupContent(
            groupData = testGroupData,
            groups = listOf(
                testGroup,
                Group().copy(id = "2", name = "name 2"),
            ),
            selectedGroupIndex = 0,
            syncState = SyncState.Synced,
            scrollState = rememberLazyListState(),
        )
    }
}
