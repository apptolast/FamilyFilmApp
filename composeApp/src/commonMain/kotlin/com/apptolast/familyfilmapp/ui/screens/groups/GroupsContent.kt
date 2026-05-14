package com.apptolast.familyfilmapp.ui.screens.groups

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import com.apptolast.familyfilmapp.utils.TT_GROUPS_EMPTY_TEXT
import com.apptolast.familyfilmapp.utils.TT_GROUPS_FAB
import com.apptolast.familyfilmapp.utils.TT_GROUPS_GROUP_CARD
import com.apptolast.familyfilmapp.utils.TT_GROUPS_TAB

@Composable
fun GroupsContent(
    state: GroupViewModel.GroupsState,
    onSelectGroup: (String) -> Unit,
    onCreateGroupRequested: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateGroupRequested,
                modifier = Modifier.testTag(TT_GROUPS_FAB),
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create group")
            }
        },
    ) { padding ->
        if (state.isLoading && state.groups.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        if (state.groups.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(
                    text = "No groups yet — tap + to create one",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.testTag(TT_GROUPS_EMPTY_TEXT),
                )
            }
            return@Scaffold
        }

        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            GroupTabs(
                groups = state.groups,
                selectedGroupId = state.selectedGroupId,
                onSelectGroup = onSelectGroup,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            )

            Spacer(Modifier.height(8.dp))

            state.error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }

            val data = state.selectedGroupData
            if (data == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item {
                        GroupSummaryCard(group = data.group, memberCount = data.members.size)
                    }
                    if (data.mediaToWatch.isNotEmpty()) {
                        item {
                            Text("To watch (${data.mediaToWatch.size})", style = MaterialTheme.typography.titleSmall)
                        }
                        items(data.mediaToWatch, key = { it.id }) { media ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = media.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(12.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GroupTabs(
    groups: List<Group>,
    selectedGroupId: String?,
    onSelectGroup: (String) -> Unit,
    contentPadding: PaddingValues,
) {
    LazyRow(
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(groups, key = { it.id }) { group ->
            FilterChip(
                selected = group.id == selectedGroupId,
                onClick = { onSelectGroup(group.id) },
                label = { Text(group.name) },
                modifier = Modifier.testTag(TT_GROUPS_TAB),
            )
        }
    }
}

@Composable
private fun GroupSummaryCard(group: Group, memberCount: Int) {
    Card(modifier = androidx.compose.ui.Modifier.fillMaxWidth().testTag(TT_GROUPS_GROUP_CARD)) {
        Column(modifier = androidx.compose.ui.Modifier.padding(16.dp)) {
            Text(group.name, style = MaterialTheme.typography.titleMedium)
            Text("$memberCount members", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
@Preview
private fun PreviewGroupsContentEmpty() {
    FamilyFilmAppTheme {
        GroupsContent(
            state = GroupViewModel.GroupsState(isLoading = false),
            onSelectGroup = {},
            onCreateGroupRequested = {},
        )
    }
}

@Composable
@Preview
private fun PreviewGroupsContentWithGroups() {
    FamilyFilmAppTheme {
        val group = Group(
            id = "g1",
            ownerId = "u1",
            name = "Family",
            users = listOf("u1", "u2"),
            lastUpdated = null,
        )
        GroupsContent(
            state = GroupViewModel.GroupsState(
                groups = listOf(group),
                selectedGroupId = "g1",
                selectedGroupData = GroupViewModel.GroupData(
                    group = group,
                    members = emptyList(),
                    mediaToWatch = emptyList(),
                    mediaWatched = emptyList(),
                    recommendedMedia = null,
                    currentUserId = "u1",
                ),
                isLoading = false,
            ),
            onSelectGroup = {},
            onCreateGroupRequested = {},
        )
    }
}
