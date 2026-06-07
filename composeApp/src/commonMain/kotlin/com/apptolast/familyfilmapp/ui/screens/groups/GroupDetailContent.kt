package com.apptolast.familyfilmapp.ui.screens.groups

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ModeEditOutline
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.Media
import com.apptolast.familyfilmapp.model.local.SyncState
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.ui.components.GroupAvatar
import com.apptolast.familyfilmapp.ui.components.dialogs.BasicDialog
import com.apptolast.familyfilmapp.ui.components.dialogs.EmailFieldDialog
import com.apptolast.familyfilmapp.ui.components.dialogs.TextFieldDialog
import com.apptolast.familyfilmapp.ui.screens.groups.components.GroupMemberRow
import com.apptolast.familyfilmapp.ui.screens.groups.components.HorizontalScrollableMedia
import com.apptolast.familyfilmapp.ui.screens.groups.components.RecommendedMediaFlipCard
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import com.apptolast.familyfilmapp.utils.TT_GROUP_DETAIL_ADD_MEMBER
import com.apptolast.familyfilmapp.utils.TT_GROUP_DETAIL_BACK
import com.apptolast.familyfilmapp.utils.TT_GROUP_DETAIL_CONTENT
import com.apptolast.familyfilmapp.utils.TT_GROUP_DETAIL_DELETE
import com.apptolast.familyfilmapp.utils.TT_GROUP_DETAIL_EDIT
import com.apptolast.familyfilmapp.utils.TT_GROUP_DETAIL_PROGRESS_DIALOG
import familyfilmkmp.composeapp.generated.resources.Res
import familyfilmkmp.composeapp.generated.resources.back
import familyfilmkmp.composeapp.generated.resources.dialog_add_group_member_title
import familyfilmkmp.composeapp.generated.resources.dialog_cancel
import familyfilmkmp.composeapp.generated.resources.dialog_change_group_description
import familyfilmkmp.composeapp.generated.resources.dialog_change_group_title
import familyfilmkmp.composeapp.generated.resources.dialog_delete_group_description
import familyfilmkmp.composeapp.generated.resources.dialog_delete_group_title
import familyfilmkmp.composeapp.generated.resources.dialog_ok
import familyfilmkmp.composeapp.generated.resources.group_change_image
import familyfilmkmp.composeapp.generated.resources.group_recommended_label
import familyfilmkmp.composeapp.generated.resources.groups_detail_not_found
import familyfilmkmp.composeapp.generated.resources.groups_members_title
import familyfilmkmp.composeapp.generated.resources.groups_text_to_watch
import familyfilmkmp.composeapp.generated.resources.groups_text_watched
import familyfilmkmp.composeapp.generated.resources.groups_tooltip_add_member
import familyfilmkmp.composeapp.generated.resources.groups_tooltip_delete_group
import familyfilmkmp.composeapp.generated.resources.groups_tooltip_edit_name
import familyfilmkmp.composeapp.generated.resources.groups_update_loading
import familyfilmkmp.composeapp.generated.resources.screen_title_groups
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailContent(
    state: GroupDetailViewModel.GroupDetailState,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onShowDialog: (GroupDetailDialog) -> Unit = {},
    onChangeGroupName: (Group) -> Unit = {},
    onPickGroupImage: (Group, ByteArray) -> Unit = { _, _ -> },
    onAddMember: (groupId: String, identifier: String) -> Unit = { _, _ -> },
    onDeleteGroup: (String) -> Unit = {},
    onRemoveMember: (groupId: String, userId: String) -> Unit = { _, _ -> },
    onMediaClick: (Media) -> Unit = {},
    onRevealRecommended: () -> Unit = {},
    onClearError: () -> Unit = {},
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val groupData = state.groupData

    LaunchedEffect(state.error) {
        if (!state.error.isNullOrBlank()) {
            snackBarHostState.showSnackbar(state.error)
            onClearError()
        }
    }

    Scaffold(
        modifier = modifier.testTag(TT_GROUP_DETAIL_CONTENT),
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            GroupDetailTopBar(
                title = groupData?.group?.name ?: stringResource(Res.string.screen_title_groups),
                canManageGroup = groupData?.isCurrentUserOwner == true,
                onBack = onBack,
                onChangeGroupName = {
                    groupData?.group?.let { group -> onShowDialog(GroupDetailDialog.ChangeGroupName(group)) }
                },
                onAddMember = {
                    groupData?.group?.let { group -> onShowDialog(GroupDetailDialog.AddMember(group)) }
                },
                onDeleteGroup = {
                    groupData?.group?.let { group -> onShowDialog(GroupDetailDialog.DeleteGroup(group)) }
                },
            )
        },
    ) { paddingValues ->
        when {
            state.isLoading && groupData == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            groupData == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(Res.string.groups_detail_not_found),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            else -> {
                GroupDetailBody(
                    groupData = groupData,
                    syncState = state.syncState,
                    isUploadingImage = state.isUploadingImage,
                    modifier = Modifier.padding(paddingValues),
                    onPickGroupImage = onPickGroupImage,
                    onDeleteUser = { user -> onRemoveMember(groupData.group.id, user.id) },
                    onMediaClick = onMediaClick,
                    onRevealRecommended = onRevealRecommended,
                )
            }
        }

        when (val dialog = state.showDialog) {
            is GroupDetailDialog.AddMember -> {
                EmailFieldDialog(
                    title = stringResource(Res.string.dialog_add_group_member_title),
                    onConfirm = { identifier -> onAddMember(dialog.group.id, identifier) },
                    onDismiss = { onShowDialog(GroupDetailDialog.None) },
                )
            }

            is GroupDetailDialog.ChangeGroupName -> {
                TextFieldDialog(
                    title = stringResource(Res.string.dialog_change_group_title),
                    description = stringResource(Res.string.dialog_change_group_description),
                    onConfirm = { newGroupName ->
                        onChangeGroupName(dialog.group.copy(name = newGroupName))
                    },
                    onDismiss = { onShowDialog(GroupDetailDialog.None) },
                )
            }

            is GroupDetailDialog.DeleteGroup -> {
                BasicDialog(
                    title = stringResource(Res.string.dialog_delete_group_title),
                    description = stringResource(Res.string.dialog_delete_group_description),
                    confirmButtonText = stringResource(Res.string.dialog_ok),
                    cancelButtonText = stringResource(Res.string.dialog_cancel),
                    onConfirm = { onDeleteGroup(dialog.group.id) },
                    onDismiss = { onShowDialog(GroupDetailDialog.None) },
                )
            }

            GroupDetailDialog.None -> Unit
        }

        if (state.isLoading && groupData != null) {
            GroupUpdateLoadingDialog()
        }
    }
}

@Composable
private fun GroupUpdateLoadingDialog() {
    Dialog(onDismissRequest = {}) {
        Card(
            modifier = Modifier.testTag(TT_GROUP_DETAIL_PROGRESS_DIALOG),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                Text(
                    text = stringResource(Res.string.groups_update_loading),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GroupDetailTopBar(
    title: String,
    canManageGroup: Boolean,
    onBack: () -> Unit,
    onChangeGroupName: () -> Unit,
    onAddMember: () -> Unit,
    onDeleteGroup: () -> Unit,
) {
    TopAppBar(
        navigationIcon = {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag(TT_GROUP_DETAIL_BACK),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(Res.string.back),
                )
            }
        },
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleLarge,
            )
        },
        actions = {
            if (canManageGroup) {
                IconButton(
                    onClick = onChangeGroupName,
                    modifier = Modifier.testTag(TT_GROUP_DETAIL_EDIT),
                ) {
                    Icon(
                        imageVector = Icons.Filled.ModeEditOutline,
                        contentDescription = stringResource(Res.string.groups_tooltip_edit_name),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
                IconButton(
                    onClick = onAddMember,
                    modifier = Modifier.testTag(TT_GROUP_DETAIL_ADD_MEMBER),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PersonAdd,
                        contentDescription = stringResource(Res.string.groups_tooltip_add_member),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
                IconButton(
                    onClick = onDeleteGroup,
                    modifier = Modifier.testTag(TT_GROUP_DETAIL_DELETE),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DeleteOutline,
                        contentDescription = stringResource(Res.string.groups_tooltip_delete_group),
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
        ),
    )
}

@Composable
private fun GroupDetailBody(
    groupData: GroupDetailViewModel.GroupData,
    syncState: SyncState,
    isUploadingImage: Boolean,
    modifier: Modifier = Modifier,
    onPickGroupImage: (Group, ByteArray) -> Unit,
    onDeleteUser: (User) -> Unit,
    onMediaClick: (Media) -> Unit,
    onRevealRecommended: () -> Unit,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            SyncStateIndicator(
                syncState = syncState,
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        item {
            AnimatedVisibility(visible = groupData.recommendedMedia != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 12.dp, end = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(Res.string.group_recommended_label),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 8.dp),
                        textAlign = TextAlign.Center,
                    )
                    groupData.recommendedMedia?.let { media ->
                        key(groupData.group.id, media.id) {
                            RecommendedMediaFlipCard(
                                media = media,
                                isPersistedRevealed = groupData.isRecommendedRevealed,
                                onReveal = onRevealRecommended,
                                onMediaClick = onMediaClick,
                                modifier = Modifier.fillMaxWidth(0.68f),
                            )
                        }
                    }
                }
            }
        }

        item {
            GroupImageHeader(
                group = groupData.group,
                canEdit = groupData.isCurrentUserOwner,
                isUploading = isUploadingImage,
                onPickImage = onPickGroupImage,
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        item {
            GroupMembersSection(
                groupData = groupData,
                onDeleteUser = onDeleteUser,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }

        if (groupData.mediaToWatch.isNotEmpty()) {
            item {
                Text(
                    text = stringResource(Res.string.groups_text_to_watch),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 16.dp),
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
                    modifier = Modifier.padding(horizontal = 16.dp),
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

@Composable
private fun GroupImageHeader(
    group: Group,
    canEdit: Boolean,
    isUploading: Boolean,
    onPickImage: (Group, ByteArray) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val launcher = rememberFilePickerLauncher(type = FileKitType.Image) { file ->
        file?.let { picked -> scope.launch { onPickImage(group, picked.readBytes()) } }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .then(
                    if (canEdit && !isUploading) {
                        Modifier.clickable { launcher.launch() }
                    } else {
                        Modifier
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            GroupAvatar(group = group, size = 96.dp, textStyle = MaterialTheme.typography.headlineMedium)

            if (isUploading) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.45f)),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }

            if (canEdit && !isUploading) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.ModeEditOutline,
                        contentDescription = stringResource(Res.string.group_change_image),
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GroupMembersSection(
    groupData: GroupDetailViewModel.GroupData,
    modifier: Modifier = Modifier,
    onDeleteUser: (User) -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = stringResource(Res.string.groups_members_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            groupData.members.forEach { user ->
                key(user.id) {
                    val canSwipe = groupData.isCurrentUserOwner && groupData.group.ownerId != user.id
                    val swipeState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { dismissValue ->
                            when (dismissValue) {
                                SwipeToDismissBoxValue.EndToStart -> {
                                    if (canSwipe) {
                                        onDeleteUser(user)
                                        true
                                    } else {
                                        false
                                    }
                                }

                                else -> false
                            }
                        },
                    )

                    SwipeToDismissBox(
                        state = swipeState,
                        enableDismissFromEndToStart = canSwipe,
                        enableDismissFromStartToEnd = false,
                        backgroundContent = {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 4.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.errorContainer,
                                        shape = MaterialTheme.shapes.medium,
                                    ),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                AnimatedVisibility(
                                    visible = swipeState.dismissDirection == SwipeToDismissBoxValue.EndToStart,
                                    enter = fadeIn(),
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Delete,
                                        tint = MaterialTheme.colorScheme.onErrorContainer,
                                        contentDescription = null,
                                        modifier = Modifier.padding(12.dp),
                                    )
                                }
                            }
                        },
                        content = {
                            GroupMemberRow(
                                user = user,
                                stats = groupData.memberStats[user.id] ?: MemberMediaStats(),
                                isOwner = groupData.group.ownerId == user.id,
                            )
                        },
                    )
                }
            }
        }
    }
}

@Preview(name = "Owner Phone", device = Devices.PHONE, showBackground = true)
@Preview(name = "Owner Tablet", device = Devices.TABLET, showBackground = true)
@Composable
private fun PreviewGroupDetailContentOwner() {
    FamilyFilmAppTheme {
        GroupDetailContent(
            state = GroupDetailViewModel.GroupDetailState(
                isLoading = false,
                groupData = previewGroupData(currentUserId = "u1"),
            ),
        )
    }
}

@Preview(name = "Member", device = Devices.PHONE, showBackground = true)
@Composable
private fun PreviewGroupDetailContentMember() {
    FamilyFilmAppTheme {
        GroupDetailContent(
            state = GroupDetailViewModel.GroupDetailState(
                isLoading = false,
                groupData = previewGroupData(currentUserId = "u2"),
            ),
        )
    }
}

@Preview(name = "Updating", device = Devices.PHONE, showBackground = true)
@Composable
private fun PreviewGroupDetailContentUpdating() {
    FamilyFilmAppTheme {
        GroupDetailContent(
            state = GroupDetailViewModel.GroupDetailState(
                isLoading = true,
                groupData = previewGroupData(currentUserId = "u1"),
            ),
        )
    }
}

private fun previewGroupData(currentUserId: String): GroupDetailViewModel.GroupData {
    val users = listOf(
        User("u1", "alex@example.com", "en", "", "Alex"),
        User("u2", "sara@example.com", "en", "", "Sara"),
        User("u3", "mario@example.com", "en", "", "Mario"),
        User("u4", "laura@example.com", "en", "", "Laura"),
    )
    return GroupDetailViewModel.GroupData(
        group = Group(
            id = "g1",
            ownerId = "u1",
            name = "Friday Night",
            users = users.map { it.id },
            lastUpdated = null,
        ),
        members = users,
        memberStats = mapOf(
            "u1" to MemberMediaStats(8, 2),
            "u2" to MemberMediaStats(10, 1),
            "u3" to MemberMediaStats(7, 2),
            "u4" to MemberMediaStats(9, 1),
        ),
        mediaToWatch = emptyList(),
        mediaWatched = emptyList(),
        recommendedMedia = Media(),
        currentUserId = currentUserId,
    )
}
