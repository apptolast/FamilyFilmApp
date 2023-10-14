package com.digitalsolution.familyfilmapp.ui.components.tabgroups

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.digitalsolution.familyfilmapp.ui.theme.FamilyFilmAppTheme

@Composable
fun TabGroups(
    viewmodel: TabGroupsViewModel = hiltViewModel(),
) {
    var stateRow by rememberSaveable { mutableIntStateOf(0) }

    val groups by viewmodel.groups.observeAsState()

    val selectedTabColor = MaterialTheme.colorScheme.primary
    val unselectedTabColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    val tabPadding = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)

    if (!groups.isNullOrEmpty()) {
        ScrollableTabRow(
            selectedTabIndex = stateRow,
            containerColor = MaterialTheme.colorScheme.outlineVariant,
            edgePadding = 0.dp,
            divider = {}
        ) {
            groups?.forEachIndexed { index, groupInfo ->
                Tab(
                    selected = stateRow == index,
                    onClick = { stateRow = index },
                    modifier = tabPadding,
                    selectedContentColor = selectedTabColor,
                    unselectedContentColor = unselectedTabColor,
                    text = {
                        Text(
                            text = groupInfo.name,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            style = if (stateRow == index) {
                                MaterialTheme.typography.titleSmall
                            } else {
                                MaterialTheme.typography.titleMedium
                            }
                        )
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TabGroupsPreview() {
    FamilyFilmAppTheme {
        TabGroups(viewmodel = hiltViewModel())
    }
}
