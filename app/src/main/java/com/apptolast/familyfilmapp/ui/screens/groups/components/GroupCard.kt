package com.apptolast.familyfilmapp.ui.screens.groups.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ModeEditOutline
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp, // Más elevación
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
                }
            }

            // Buttons to "Add Member" and "Delete Group"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (group.ownerId == userOwner.id) {
                    Button(
                        onClick = onAddMember,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        ),
                    ) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = "Add")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = stringResource(id = R.string.groups_text_add_member))
                    }

                    OutlinedButton(
                        onClick = onDeleteGroup,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error,
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                    ) {
                        Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = stringResource(id = R.string.groups_text_delete_group))
                    }
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
                                        visible = swipeState.targetValue == SwipeToDismissBoxValue.EndToStart,
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
