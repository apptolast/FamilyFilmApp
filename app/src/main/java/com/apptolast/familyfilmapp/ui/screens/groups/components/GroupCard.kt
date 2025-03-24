package com.apptolast.familyfilmapp.ui.screens.groups.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ModeEditOutline
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.apptolast.familyfilmapp.R
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import kotlin.collections.listOf

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun GroupCard(
    userOwner: User,
    group: Group,
    groupUsers: List<User>,
    modifier: Modifier = Modifier,
    onChangeGroupName: () -> Unit = {},
    onAddMember: () -> Unit = {},
    onDeleteGroup: () -> Unit = {},
    onDeleteUser: (User) -> Unit = {},
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = group.name,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .weight(1f) // Assign weight to text
                        .padding(top = if (group.ownerId == userOwner.uid) 0.dp else 10.dp),
                )

                if (group.ownerId == userOwner.uid) {
                    IconButton(onClick = onChangeGroupName) {
                        Icon(
                            imageVector = Icons.Filled.ModeEditOutline,
                            contentDescription = stringResource(R.string.edit_text),
                        )
                    }
                }
            }

            // Buttons to "Add Member" and "Delete Group"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (group.ownerId.equals(userOwner.uid)) {
                    OutlinedButton(onClick = onAddMember) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = "Add")
                        Text(text = stringResource(id = R.string.groups_text_add_member))
                    }

                    OutlinedButton(onClick = onDeleteGroup) {
                        Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete")
                        Text(text = stringResource(id = R.string.groups_text_delete_group))
                    }
                }
            }

            // List of users
            FlowColumn {
                groupUsers.forEach { user ->

                    val swipeState = rememberSwipeToDismissBoxState(
                        confirmValueChange = {
                            when (it) {
                                SwipeToDismissBoxValue.EndToStart -> {
                                    if (group.ownerId != user.uid) {
                                        onDeleteUser(user)
                                        true
                                    } else {
                                        Toast.makeText(context, "Delete the group", Toast.LENGTH_SHORT).show()
                                        false
                                    }
                                }

                                else -> false
                            }
                        },
                    )

                    SwipeToDismissBox(
                        state = swipeState,
                        enableDismissFromEndToStart = userOwner.uid == group.ownerId,
                        enableDismissFromStartToEnd = false,
                        backgroundContent = {
//                            Box(
//                                contentAlignment = Alignment.CenterEnd,
//                                modifier = Modifier
//                                    .fillMaxSize()
//                                    .padding(8.dp)
//                                    .background(
//                                        color = MaterialTheme.colorScheme.error,
//                                        shape = MaterialTheme.shapes.medium,
//                                    ),
//                            ) {
//                                Icon(
//                                    modifier = Modifier
//                                        .padding(8.dp)
//                                        .minimumInteractiveComponentSize(),
//                                    imageVector = Icons.Outlined.Delete,
//                                    tint = MaterialTheme.colorScheme.onError,
//                                    contentDescription = null,
//                                )
//                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.error,
                                        shape = MaterialTheme.shapes.medium,
                                    ),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                // Delete action on swipe from end to start
                                AnimatedVisibility(
                                    visible = swipeState.targetValue == SwipeToDismissBoxValue.EndToStart,
                                    enter = fadeIn(),
                                ) {
                                    Icon(
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .minimumInteractiveComponentSize(),
                                        imageVector = Icons.Outlined.Delete,
                                        tint = MaterialTheme.colorScheme.onError,
                                        contentDescription = null,
                                    )
                                }
                            }
                        },
                        content = {
                            GroupMemberCard(
                                user = user,
                                modifier = Modifier.padding(8.dp),
                            )
                        },
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun GroupCardOwnerPreview() {
    FamilyFilmAppTheme {
        GroupCard(
            userOwner = User().copy(uid = "1"),
            group = Group().copy(
                id = "1",
                ownerId = "1",
                name = "Name",
                users = listOf(
                    "user 1",
                    "user 2",
                    "user 3",
                ),
            ),
            groupUsers = listOf(
                User().copy(uid = "1", email = "a@a.com"),
                User().copy(uid = "2", email = "b@b.com"),
                User().copy(uid = "3", email = "c@c.com"),
            ),
        )
    }
}

@Preview
@Composable
private fun GroupCardNotOwnerPreview() {
    FamilyFilmAppTheme {
        GroupCard(
            userOwner = User().copy(uid = "2"),
            group = Group().copy(
                id = "1",
                ownerId = "2",
                name = "Name",
                users = listOf(
                    "1",
                    "2",
                    "3",
                ),
            ),
            groupUsers = listOf(
                User().copy(uid = "1", email = "a@a.com"),
                User().copy(uid = "2", email = "b@b.com"),
                User().copy(uid = "3", email = "c@c.com"),
            ),
        )
    }
}
