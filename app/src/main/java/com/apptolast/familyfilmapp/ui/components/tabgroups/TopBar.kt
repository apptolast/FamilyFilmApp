package com.apptolast.familyfilmapp.ui.components.tabgroups

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import timber.log.Timber

@Composable
fun TopBar(viewmodel: TabGroupsViewModel = hiltViewModel()) {
    val unselectedTabColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)

    val backendState by viewmodel.backendState.collectAsStateWithLifecycle()
    val uiState by viewmodel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = true) { viewmodel.refreshGroups() }

    if (backendState.groups?.isNotEmpty() == true) {
        ScrollableTabRow(
            selectedTabIndex = uiState.selectedGroupPos,
            containerColor = MaterialTheme.colorScheme.outlineVariant,
            edgePadding = 0.dp,
            divider = {},
        ) {
            backendState.groups!!.forEachIndexed { index, group ->
                Tab(
                    selected = uiState.selectedGroupPos == index,
                    onClick = {
                        viewmodel.selectGroupByPos(index)
                    },
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = unselectedTabColor,
                    text = {
                        Text(
                            text = group.name,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            style = if (uiState.selectedGroupPos == index) {
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
        Timber.d("PEro este es el TopBar pas apro aqui antes ")
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun TopBarPreview() {
    FamilyFilmAppTheme {
        TopBar(viewmodel = hiltViewModel())
    }
}
