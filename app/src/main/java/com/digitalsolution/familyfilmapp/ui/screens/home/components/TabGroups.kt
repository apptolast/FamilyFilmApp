@file:Suppress("IMPLICIT_CAST_TO_ANY")

package com.digitalsolution.familyfilmapp.ui.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.digitalsolution.familyfilmapp.R
import com.digitalsolution.familyfilmapp.model.local.GroupData
import com.digitalsolution.familyfilmapp.ui.screens.home.HomeViewModel

@Composable
fun TabGroups(viewModel: HomeViewModel?, groups: List<GroupData>, groupScreen: Boolean) {
    var stateRow by rememberSaveable { mutableIntStateOf(0) }
    val titles = if (!groupScreen) {
        viewModel?.getGroupsList() ?: groups
    } else {
        groups.toMutableList().apply {
            this.add(GroupData(image = "", name = "Add Groups"))
        }
    }

    // Define colores y estilos específicos
    val selectedTabColor = MaterialTheme.colorScheme.primary
    val unselectedTabColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    val tabPadding = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)

    ScrollableTabRow(
        selectedTabIndex = stateRow,
        containerColor = MaterialTheme.colorScheme.outlineVariant,
        edgePadding = 0.dp,
        divider = {}
    ) {
        titles.forEachIndexed { index, groupData ->
            Tab(
                selected = stateRow == index,
                onClick = {
                    if (groupData.name.equals("Add Groups", true)) {
                        // TODO: Navegar a la pantalla de agregar grupo o mostrar el diálogo de agregar grupo
                    } else {
                        stateRow = index
                    }
                },
                modifier = tabPadding,
                selectedContentColor = selectedTabColor,
                unselectedContentColor = unselectedTabColor,
                text = {
                    if (groupData.name.equals("Add Groups", true)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)  // Ajusta este valor según lo necesites
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Add"
                            )
                            Text(
                                text = stringResource(id = R.string.groups_text_add)
                            )
                        }
                    } else {
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
