@file:Suppress("IMPLICIT_CAST_TO_ANY")

package com.digitalsolution.familyfilmapp.ui.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.digitalsolution.familyfilmapp.R
import com.digitalsolution.familyfilmapp.model.local.Group

@Composable
fun TabGroups(
    addGroupFlag: Boolean = false,
    viewmodel: TabGroupsViewModel = hiltViewModel(),
) {
    var stateRow by rememberSaveable { mutableIntStateOf(0) }

    val groups by viewmodel.groups.observeAsState()

    val groupNames: ArrayList<Group>? = if (addGroupFlag) {
        // FIXME: @Pablo fix this shit!
        // If the element is not being added yet, add the add group at the end
        if (groups?.find { it.name == stringResource(id = R.string.groups_text_add) } == null)
            groups?.apply { add(Group(image = "", name = "Add Groups")) }
        else
            groups
    } else {
        groups
    }

    val selectedTabColor = MaterialTheme.colorScheme.primary
    val unselectedTabColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    val tabPadding = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)

    ScrollableTabRow(
        selectedTabIndex = stateRow,
        containerColor = MaterialTheme.colorScheme.outlineVariant,
        edgePadding = 0.dp,
        divider = {}
    ) {
        groupNames?.forEachIndexed { index, groupData ->
            Tab(
                selected = stateRow == index,
                onClick = {
                    if (addGroupFlag) {
                        // TODO: Navegar a la pantalla
                    } else {
                        stateRow = index
                    }
                },
                modifier = tabPadding,
                selectedContentColor = selectedTabColor,
                unselectedContentColor = unselectedTabColor,
                text = {

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .clip(CircleShape)
                                .padding(5.dp),
                            shape = CircleShape,
                        ) {
                            if (addGroupFlag && index == groupNames.size - 1) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = "Add"
                                )
                            }
                        }
                        Text(
                            text = groupData.name,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            style = if (stateRow == index) {
                                MaterialTheme.typography.titleSmall
                            } else {
                                MaterialTheme.typography.titleMedium
                            }
                        )
                    }

                }
            )
        }
    }
}
