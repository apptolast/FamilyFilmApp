package com.digitalsolution.familyfilmapp.ui.components.tabgroups

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.digitalsolution.familyfilmapp.ui.theme.FamilyFilmAppTheme

@Composable
fun TopBar(viewmodel: TabGroupsViewModel = hiltViewModel()) {
    val tabState by viewmodel.state.collectAsStateWithLifecycle()

    var selectedGroupId by rememberSaveable { mutableIntStateOf(tabState.groups.firstOrNull()?.id ?: 2) }

    val selectedTabColor = MaterialTheme.colorScheme.primary
    val unselectedTabColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    val tabPadding = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)

    if (tabState.groups.isNotEmpty()) {
        // Execute the callback with the first selected tab by default.
        ScrollableTabRow(
            selectedTabIndex = tabState.groups.indexOfFirst { it.id == selectedGroupId },
            containerColor = MaterialTheme.colorScheme.outlineVariant,
            edgePadding = 0.dp,
            divider = {},
        ) {
            tabState.groups.forEach { groupInfo ->
                Tab(
                    selected = selectedGroupId == groupInfo.id,
                    onClick = {
                        selectedGroupId = groupInfo.id
                        viewmodel.selectGroupById(groupInfo.id)
                    },
                    modifier = tabPadding,
                    selectedContentColor = selectedTabColor,
                    unselectedContentColor = unselectedTabColor,
                    text = {
                        Text(
                            text = groupInfo.name,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            style = if (selectedGroupId == groupInfo.id) {
                                MaterialTheme.typography.titleSmall
                            } else {
                                MaterialTheme.typography.titleMedium
                            },
                        )
                    },
                )
            }
        }
    } else {
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun TopBarPreview() {
    FamilyFilmAppTheme {
        TopBar(viewmodel = hiltViewModel())
    }
}
