package com.digitalsolution.familyfilmapp.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.digitalsolution.familyfilmapp.ui.screens.home.HomeViewModel

@Composable
fun TabGroups(viewModel: HomeViewModel) {
    var stateRow by rememberSaveable { mutableIntStateOf(0) }
    val titles = viewModel.getGroupsList()

    // Define colores y estilos específicos
    val selectedTabColor = MaterialTheme.colorScheme.primary
    val unselectedTabColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    val tabPadding = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)

    ScrollableTabRow(
        modifier = Modifier.background(
            color = Color.Transparent,
            shape = RoundedCornerShape(10.dp)
        ),
        selectedTabIndex = stateRow,
        edgePadding = 0.dp,
        divider = {}
    ) {
        titles.forEachIndexed { index, groupData ->
            Tab(
                selected = stateRow == index,
                onClick = { stateRow = index },
                modifier = tabPadding,
                selectedContentColor = selectedTabColor,
                unselectedContentColor = unselectedTabColor,
                text = {
                    Text(
                        text = groupData.name,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = if (stateRow == index) MaterialTheme.typography.titleSmall
                        else MaterialTheme.typography.titleMedium
                    )
                }
            )
        }
    }
}