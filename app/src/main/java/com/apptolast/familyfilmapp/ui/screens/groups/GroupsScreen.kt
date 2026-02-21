package com.apptolast.familyfilmapp.ui.screens.groups

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.apptolast.familyfilmapp.R
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.Movie
import com.apptolast.familyfilmapp.model.local.SyncState
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.navigation.navtypes.DetailNavTypeDestination
import com.apptolast.familyfilmapp.ui.components.dialogs.BasicDialog
import com.apptolast.familyfilmapp.ui.components.dialogs.EmailFieldDialog
import com.apptolast.familyfilmapp.ui.components.dialogs.TextFieldDialog
import com.apptolast.familyfilmapp.ui.screens.groups.GroupViewModel.GroupScreenDialogs
import com.apptolast.familyfilmapp.ui.screens.groups.components.GroupCard
import com.apptolast.familyfilmapp.ui.screens.groups.components.HorizontalScrollableMovies
import com.apptolast.familyfilmapp.ui.screens.home.MovieItem
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import com.apptolast.familyfilmapp.utils.TT_GROUPS_EMPTY_TEXT
import com.apptolast.familyfilmapp.utils.TT_GROUPS_FAB
import com.apptolast.familyfilmapp.utils.TT_GROUPS_TAB
import kotlinx.coroutines.DelicateCoroutinesApi

@OptIn(DelicateCoroutinesApi::class, ExperimentalMaterial3Api::class)
@Composable
fun GroupsScreen(
    modifier: Modifier = Modifier,
    viewModel: GroupViewModel = hiltViewModel(),
    onClickNav: (String) -> Unit = {},
    onBack: () -> Unit = {},
) {
    val snackBarHostState = remember { SnackbarHostState() }

    val state by viewModel.state.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val listState = rememberLazyListState()
    val isFabExtended by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0
        }
    }

    LaunchedEffect(state.error) {
        if (!state.error.isNullOrBlank()) {
            Toast.makeText(context, state.error, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        floatingActionButton = {
            ExpandableFAB(
                isExtended = isFabExtended,
                onClick = {
                    viewModel.showDialog(GroupScreenDialogs.CreateGroup)
                },
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
                        viewModel.showDialog(GroupScreenDialogs.ChangeGroupName(group))
                    },
                    onAddMemberClick = { group ->
                        viewModel.showDialog(GroupScreenDialogs.AddMember(group))
                    },
                    onDeleteGroup = { groupId ->
                        viewModel.showDialog(
                            GroupScreenDialogs.DeleteGroup(
                                state.groups.first { it.id == groupId },
                            ),
                        )
                    },
                    onDeleteUser = { groupId, userId ->
                        viewModel.removeMember(groupId, userId)
                    },
                    onGroupSelect = { groupId ->
                        viewModel.selectGroup(groupId)
                    },
                    onMovieClick = { movie ->
                        onClickNav(DetailNavTypeDestination.getDestination(movie))
                    },
                )
            }
        }

        // Show dialog
        when (state.showDialog) {
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
                val group = (state.showDialog as GroupScreenDialogs.AddMember).group

                EmailFieldDialog(
                    title = stringResource(id = R.string.dialog_add_group_member_title),
                    onConfirm = { email ->
                        viewModel.addMember(group.id, email)
                    },
                    onDismiss = {
                        viewModel.showDialog(GroupScreenDialogs.None)
                    },
                )
            }

            is GroupScreenDialogs.ChangeGroupName -> {
                val group = (state.showDialog as GroupScreenDialogs.ChangeGroupName).group

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
                val group = (state.showDialog as GroupScreenDialogs.DeleteGroup).group
                BasicDialog(
                    title = stringResource(R.string.dialog_delete_group_title),
                    description = stringResource(R.string.dialog_delete_group_description),
                    confirmButtonText = stringResource(id = android.R.string.ok),
                    cancelButtonText = stringResource(id = android.R.string.cancel),
                    onConfirm = { viewModel.deleteGroup(group.id) },
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
    onMovieClick: (Movie) -> Unit = {},
) {
    if (groups.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = (-70).dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.groups_text_create_group),
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
            // Group tabs
            stickyHeader {
                // CRITICAL: Validate against the ACTUAL groups list we have in this composition
                // This prevents crashes when selectedGroupIndex hasn't updated yet
                val safeTabIndex = when {
                    groups.isEmpty() -> 0
                    selectedGroupIndex >= groups.size -> groups.size - 1
                    selectedGroupIndex < 0 -> 0
                    else -> selectedGroupIndex
                }

                ScrollableTabRow(
                    selectedTabIndex = safeTabIndex,
                    divider = {
                        VerticalDivider()
                    },
                ) {
                    groups.forEachIndexed { index, group ->
                        Tab(
                            selected = safeTabIndex == index,
                            onClick = { onGroupSelect(group.id) }, // Pass group ID
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

            // Sync state indicator
            item {
                SyncStateIndicator(syncState = syncState)
            }

            // Recommended movie
            item {
                AnimatedVisibility(visible = groupData.recommendedMovie != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = stringResource(R.string.group_recommended_label),
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 8.dp),
                            textAlign = TextAlign.Center,
                        )
                        groupData.recommendedMovie?.let { movie ->
                            MovieItem(
                                movie = movie,
                                onClick = onMovieClick,
                                status = null,
                                modifier = Modifier.fillMaxWidth(0.6f),
                            )
                        }
                    }
                }
            }

            // Group card
            item {
                // Find authenticated user by ID (not by position)
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

            // Movies to watch
            if (groupData.moviesToWatch.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.groups_text_to_watch),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(12.dp),
                    )
                }

                item {
                    HorizontalScrollableMovies(
                        movies = groupData.moviesToWatch,
                        onMovieClick = onMovieClick,
                    )
                }
            }

            // Movies watched
            if (groupData.moviesWatched.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.groups_text_watched),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(12.dp),
                    )
                }

                item {
                    HorizontalScrollableMovies(
                        movies = groupData.moviesWatched,
                        onMovieClick = onMovieClick,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
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
                ).using(
                    SizeTransform(clip = false),
                )
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
                        text = stringResource(id = R.string.groups_text_create_group),
                        modifier = Modifier.padding(end = 8.dp),
                    )
                }
            }
        }
    }
}

/**
 * Displays a subtle sync state indicator below the group tabs.
 * Shows syncing progress, errors, or offline state.
 */
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
                        text = stringResource(R.string.sync_state_syncing),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                is SyncState.Error -> {
                    Text(
                        text = "${stringResource(R.string.sync_state_error)}: ${syncState.message}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
                is SyncState.Offline -> {
                    Text(
                        text = stringResource(R.string.sync_state_offline),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                is SyncState.Synced -> {
                    // Hidden when synced
                }
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true)
@Composable
private fun GroupContentPreview() {
    FamilyFilmAppTheme {
        val testGroup = Group().copy(id = "1", name = "Test Group")
        val testGroupData = GroupViewModel.GroupData(
            group = testGroup,
            members = listOf(
                User().copy(id = "1", email = "a@a.com"),
                User().copy(id = "2", email = "b@b.com"),
            ),
            moviesToWatch = listOf(
                Movie().copy(id = 1, title = "Title 1", overview = "Description 1"),
                Movie().copy(id = 2, title = "Title 2", overview = "Description 2"),
                Movie().copy(id = 3, title = "Title 3", overview = "Description 3"),
            ),
            moviesWatched = listOf(
                Movie().copy(id = 4, title = "Title 4", overview = "Description 4"),
            ),
            recommendedMovie = Movie().copy(id = 1, title = "Recommended", overview = "Top pick"),
            currentUserId = "1",
        )

        GroupContent(
            groupData = testGroupData,
            groups = listOf(
                testGroup,
                Group().copy(id = "2", name = "name 2"),
                Group().copy(id = "3", name = "name 3"),
            ),
            selectedGroupIndex = 0,
            syncState = SyncState.Synced,
            scrollState = rememberLazyListState(),
        )
    }
}
