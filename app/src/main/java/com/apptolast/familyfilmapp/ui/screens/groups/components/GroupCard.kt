package com.apptolast.familyfilmapp.ui.screens.groups.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.apptolast.familyfilmapp.R
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupCard(
    userOwner: User,
    group: Group,
    onChangeGroupName: () -> Unit,
    onAddMember: () -> Unit,
    onDeleteGroup: () -> Unit,
    modifier: Modifier = Modifier,
    onDeleteUser: (User) -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = group.name,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .weight(1f) // Assign weight to text
                        .padding(3.dp),
                )

                if (group.ownerId.equals(userOwner.id)) {
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
                if (group.ownerId.equals(userOwner.id)) {
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
            LazyColumn {
                items(group.users) { user ->
                    val swipeState = rememberSwipeToDismissBoxState()

                    when (swipeState.currentValue) {
                        SwipeToDismissBoxValue.EndToStart -> {
                            onDeleteUser(user)
                        }

                        else -> {
                            /* no-op */
                        }
                    }

                    SwipeToDismissBox(
                        state = swipeState,
                        enableDismissFromEndToStart = true,
                        enableDismissFromStartToEnd = false,
                        backgroundContent = {
                            Box(
                                contentAlignment = Alignment.CenterEnd,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.error,
                                        shape = MaterialTheme.shapes.medium,
                                    ),
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
            userOwner = User(),
            group = Group().copy(
                id = 1,
                ownerId = 1,
                name = "Name",
                users = listOf(
                    User().copy(email = "Email 1", language = "es"),
                    User().copy(email = "Email 2", language = "es"),
                    User().copy(email = "Email 3", language = "es"),
                ),
                watchedList = listOf(1),
                toWatchList = listOf(1),
            ),
            onDeleteUser = {},
            onAddMember = {},
            onDeleteGroup = {},
            onChangeGroupName = {},
        )
    }
}

@Preview
@Composable
private fun GroupCardNotOwnerPreview() {
    FamilyFilmAppTheme {
        GroupCard(
            userOwner = User(),
            group = Group().copy(
                id = 1,
                ownerId = 2,
                name = "Name",
                users = listOf(
                    User().copy(email = "Email 1", language = "es"),
                    User().copy(email = "Email 2", language = "es"),
                    User().copy(email = "Email 3", language = "es"),
                ),
                watchedList = listOf(1),
                toWatchList = listOf(1),
            ),
            onDeleteUser = {},
            onAddMember = {},
            onDeleteGroup = {},
            onChangeGroupName = {},
        )
    }
}
