package com.apptolast.familyfilmapp.ui.screens.groups.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ModeEditOutline
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.apptolast.familyfilmapp.R
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import com.apptolast.familyfilmapp.utils.TT_GROUPS_GROUP_CARD

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

    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag(TT_GROUPS_GROUP_CARD),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
        ),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = group.name,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f),
                )

                if (group.ownerId == userOwner.id) {
                    IconButton(onClick = onChangeGroupName) {
                        Icon(
                            imageVector = Icons.Filled.ModeEditOutline,
                            contentDescription = stringResource(R.string.edit_text),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }

                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = stringResource(R.string.groups_text_delete_group),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = stringResource(id = R.string.groups_text_delete_group),
                                        color = MaterialTheme.colorScheme.error,
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Outlined.Delete,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error,
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    onDeleteGroup()
                                },
                            )
                        }
                    }
                }
            }

            // "Add Member" button - full width for easy touch target
            if (group.ownerId == userOwner.id) {
                FilledTonalButton(
                    onClick = onAddMember,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .defaultMinSize(minHeight = 48.dp),
                ) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(id = R.string.groups_text_add_member))
                }
            }

            // Lista de usuarios con mejor espaciado
            FlowColumn(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            ) {
                groupUsers.forEach { user ->
                    // CRITICAL: Use key(user.id) to ensure stable state per user
                    androidx.compose.runtime.key(user.id) {
                        // Only allow swipe if: user is owner AND current user is NOT the group owner
                        val canSwipe = userOwner.id == group.ownerId && group.ownerId != user.id

                        val swipeState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { dismissValue ->
                                when (dismissValue) {
                                    SwipeToDismissBoxValue.EndToStart -> {
                                        if (canSwipe) {
                                            onDeleteUser(user)
                                            true
                                        } else {
                                            // Show toast only once - don't trigger recomposition
                                            if (group.ownerId == user.id) {
                                                Toast.makeText(
                                                    context,
                                                    context.getString(R.string.cannot_delete_owner),
                                                    Toast.LENGTH_SHORT,
                                                ).show()
                                            }
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
                                        .padding(8.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.errorContainer,
                                            shape = MaterialTheme.shapes.medium,
                                        ),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    // Delete action on swipe from end to start
                                    AnimatedVisibility(
                                        visible = swipeState.dismissDirection == SwipeToDismissBoxValue.EndToStart,
                                        enter = fadeIn(),
                                    ) {
                                        Icon(
                                            modifier = Modifier
                                                .padding(8.dp)
                                                .minimumInteractiveComponentSize(),
                                            imageVector = Icons.Outlined.Delete,
                                            tint = MaterialTheme.colorScheme.onErrorContainer,
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
}

@Preview
@Composable
private fun GroupCardNotOwnerPreview() {
    FamilyFilmAppTheme {
        GroupCard(
            userOwner = User().copy(id = "2"),
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
                User().copy(id = "1", email = "a@a.com"),
                User().copy(id = "2", email = "b@b.com"),
                User().copy(id = "3", email = "c@c.com"),
            ),
        )
    }
}
