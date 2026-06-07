package com.apptolast.familyfilmapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme

/**
 * Horizontal, multi-select row of the user's groups. Each chip shows the group avatar so the
 * user can pick which groups an action (watched / to-watch) applies to — replaces the old
 * "select groups" bottom sheet. Material 3 chips already expose a 48dp touch target.
 */
@Composable
fun GroupFilterChips(
    groups: List<Group>,
    selectedGroupIds: Set<String>,
    onToggleGroup: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(items = groups, key = { it.id }) { group ->
            FilterChip(
                selected = group.id in selectedGroupIds,
                onClick = { onToggleGroup(group.id) },
                leadingIcon = {
                    GroupAvatar(
                        group = group,
                        size = 35.dp,
                        textStyle = MaterialTheme.typography.labelMedium,
                    )
                },
                label = {
//                    Text(
//                        text = group.name,
//                        maxLines = 1,
//                        overflow = TextOverflow.Ellipsis,
//                    )
                },
                modifier = Modifier.height(40.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewGroupFilterChips() {
    FamilyFilmAppTheme {
        GroupFilterChips(
            groups = listOf(
                Group(id = "1", ownerId = "1", name = "Familia", users = emptyList(), lastUpdated = null),
                Group(id = "2", ownerId = "1", name = "Amigos", users = emptyList(), lastUpdated = null),
                Group(id = "3", ownerId = "1", name = "Curro", users = emptyList(), lastUpdated = null),
            ),
            selectedGroupIds = setOf("1", "3"),
            onToggleGroup = {},
        )
    }
}
