package com.apptolast.familyfilmapp.ui.screens.groups.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ModeEditOutline
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
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

@OptIn(ExperimentalMaterial3Api::class)
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
    val cannotDeleteOwnerMsg = stringResource(R.string.cannot_delete_owner)
    val isOwner = group.ownerId == userOwner.id

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .testTag(TT_GROUPS_GROUP_CARD),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)) {
            // Header: group name + action icons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 4.dp, top = 8.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = group.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                )

                if (isOwner) {
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                        tooltip = {
                            PlainTooltip {
                                Text(stringResource(R.string.groups_tooltip_edit_name))
                            }
                        },
                        state = rememberTooltipState(),
                    ) {
                        IconButton(onClick = onChangeGroupName) {
                            Icon(
                                imageVector = Icons.Filled.ModeEditOutline,
                                contentDescription = stringResource(R.string.groups_tooltip_edit_name),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    }

                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                        tooltip = {
                            PlainTooltip {
                                Text(stringResource(R.string.groups_tooltip_add_member))
                            }
                        },
                        state = rememberTooltipState(),
                    ) {
                        IconButton(onClick = onAddMember) {
                            Icon(
                                imageVector = Icons.Outlined.PersonAdd,
                                contentDescription = stringResource(R.string.groups_tooltip_add_member),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    }

                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                        tooltip = {
                            PlainTooltip {
                                Text(stringResource(R.string.groups_tooltip_delete_group))
                            }
                        },
                        state = rememberTooltipState(),
                    ) {
                        IconButton(onClick = onDeleteGroup) {
                            Icon(
                                imageVector = Icons.Outlined.DeleteOutline,
                                contentDescription = stringResource(R.string.groups_tooltip_delete_group),
                                tint = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                }
            }

            // Member count label
            Text(
                text = stringResource(R.string.groups_member_count, groupUsers.size),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f),
                modifier = Modifier.padding(horizontal = 16.dp),
            )

            // Members list
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
            ) {
                groupUsers.forEachIndexed { index, user ->
                    key(user.id) {
                        val canSwipe = isOwner && group.ownerId != user.id

                        val swipeState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { dismissValue ->
                                when (dismissValue) {
                                    SwipeToDismissBoxValue.EndToStart -> {
                                        if (canSwipe) {
                                            onDeleteUser(user)
                                            true
                                        } else {
                                            if (group.ownerId == user.id) {
                                                Toast.makeText(
                                                    context,
                                                    cannotDeleteOwnerMsg,
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
                                        .padding(horizontal = 4.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.errorContainer,
                                            shape = MaterialTheme.shapes.medium,
                                        ),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    AnimatedVisibility(
                                        visible = swipeState.dismissDirection ==
                                            SwipeToDismissBoxValue.EndToStart,
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
                                    isOwner = group.ownerId == user.id,
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
private fun GroupCardOwnerPreview() {
    FamilyFilmAppTheme {
        GroupCard(
            userOwner = User().copy(id = "1"),
            group = Group().copy(
                id = "1",
                ownerId = "1",
                name = "Family Movie Night",
                users = listOf("1", "2", "3"),
            ),
            groupUsers = listOf(
                User().copy(id = "1", email = "owner@test.com", username = "owner"),
                User().copy(id = "2", email = "alice@test.com", username = "alice"),
                User().copy(id = "3", email = "bob@test.com"),
            ),
        )
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
                ownerId = "1",
                name = "tito",
                users = listOf("1", "2"),
            ),
            groupUsers = listOf(
                User().copy(id = "1", email = "owner@test.com"),
                User().copy(id = "2", email = "member@test.com"),
            ),
        )
    }
}
