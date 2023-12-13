package com.apptolast.familyfilmapp.ui.screens.groups.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ModeEditOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconToggleButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.apptolast.familyfilmapp.R
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.ui.screens.groups.states.GroupUiState
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import com.apptolast.familyfilmapp.ui.theme.bold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupCard(
    group: Group,
    groupUiState: GroupUiState,
    onRemoveMemberClick: (Int, Int) -> Unit,
    onSwipeDelete: (Group) -> Unit,
    onAddMemberClick: () -> Unit,
    onDeleteGroupClick: () -> Unit,
    onChangeGroupName: (String) -> Unit,
    isFakeList: Boolean,
) {
    var groupName by rememberSaveable { mutableStateOf(groupUiState.groupTitleChange) }
    var checkedEditGroupName by rememberSaveable { mutableStateOf(groupUiState.checkedEditGroupName) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp,
        ),
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (!checkedEditGroupName) {
                    // Show the group name (not editing)
                    Text(
                        text = group.name,
                        style = MaterialTheme.typography.titleLarge.bold(),
                        modifier = Modifier
                            .weight(1f) // Assign weight to text
                            .padding(3.dp),
                    )
                } else {
                    // Show the group name (during the editing)
                    OutlinedTextField(
                        modifier = Modifier
                            .weight(1f)
                            .padding(6.dp),
                        value = groupName,
                        onValueChange = { groupName = it },
                        enabled = !isFakeList,
                        shape = RoundedCornerShape(10.dp),
                        label = {
                            Text(
                                modifier = Modifier
                                    .padding(bottom = 4.dp),
                                text = stringResource(id = R.string.groups_text_edit_group_name),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                    )
                }
                if (groupUiState.updateNameGroupVisibility) {
                    OutlinedIconToggleButton(
                        modifier = Modifier
                            .wrapContentWidth()
                            .padding(end = 8.dp),
                        checked = checkedEditGroupName,
                        onCheckedChange = {
                            // If I press check (edit mode) and it's not empty, I change the name
                            checkedEditGroupName = it

                            if (!checkedEditGroupName && groupName.isNotBlank()) {
                                onChangeGroupName(groupName)
                                groupName = ""
                            }
                        },
                        enabled = !isFakeList,
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        when (checkedEditGroupName) {
                            true -> Icons.Filled.Check
                            false -> Icons.Filled.ModeEditOutline
                        }.let { icon ->
                            Icon(
                                imageVector = icon,
                                contentDescription = stringResource(R.string.edit_text),
                            )
                        }
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (groupUiState.addMemberButtonVisibility) {
                    OutlinedButton(onClick = { onAddMemberClick() }, enabled = !isFakeList) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = "Add")
                        Text(text = stringResource(id = R.string.groups_text_add_member))
                    }
                }
                if (groupUiState.deleteGroupButtonVisibility) {
                    OutlinedButton(onClick = { onDeleteGroupClick() }, enabled = !isFakeList) {
                        Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete")
                        Text(text = stringResource(id = R.string.groups_text_delete_group))
                    }
                }
            }
            LazyColumn {
                items(group.users) { item ->
                    val state = rememberDismissState(
                        confirmValueChange = {
                            if (it == DismissValue.DismissedToStart) {
                                onSwipeDelete(group)
                            }
                            true
                        },
                    )
                    AnimatedVisibility(
                        visible = state.currentValue != DismissValue.DismissedToEnd,
                        exit = fadeOut(animationSpec = tween(durationMillis = 300)),
                    ) {
                        SwipeToDismiss(
                            state = state,
                            background = {
                                val color = when (state.dismissDirection) {
                                    DismissDirection.StartToEnd -> {
                                        Color.Transparent
                                    }

                                    DismissDirection.EndToStart -> {
                                        onRemoveMemberClick(item.groupID, item.userID)
                                        Color(0xFFFF1744)
                                    }

                                    null -> {
                                        Color.Transparent
                                    }
                                }
                                Card(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp),
                                    shape = MaterialTheme.shapes.small,
                                    colors = CardDefaults.cardColors(
                                        containerColor = color,
                                    ),
                                    elevation = CardDefaults.cardElevation(
                                        defaultElevation = 3.dp,
                                    ),
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(color)
                                            .padding(12.dp, 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.End,
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "",
                                        )
                                    }
                                }
                            },
                            dismissContent = {
                                GroupMemberCard(
                                    group = item,
                                    onRemoveMemberClick = onRemoveMemberClick,
                                )
                            },
                            directions = setOf(DismissDirection.EndToStart),
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun GroupCardPreview() {
    FamilyFilmAppTheme {
        GroupCard(
            group = Group().copy(name = "Name"),
            groupUiState = GroupUiState().copy(deleteGroupButtonVisibility = true),
            onRemoveMemberClick = { _, _ -> },
            onSwipeDelete = {},
            onAddMemberClick = {},
            onDeleteGroupClick = {},
            onChangeGroupName = {},
            isFakeList = true,
        )
    }
}
