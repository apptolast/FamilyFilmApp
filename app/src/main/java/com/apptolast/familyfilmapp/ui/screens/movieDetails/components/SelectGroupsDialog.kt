package com.apptolast.familyfilmapp.ui.screens.movieDetails.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.apptolast.familyfilmapp.model.local.GroupStatus
import com.apptolast.familyfilmapp.model.local.MovieStatus
import com.apptolast.familyfilmapp.ui.screens.movieDetails.DialogType
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme

@SuppressLint("MutableCollectionMutableState")
@Composable
fun SelectGroupsDialog(
    title: String,
    groups: List<GroupStatus>,
    dialogType: DialogType,
    modifier: Modifier = Modifier,
    onCancel: () -> Unit = {},
    onCheck: (Int, Boolean) -> Unit = { _, _ -> },
) {
    Dialog(
        onDismissRequest = onCancel,
        content = {
            LazyColumn(
                modifier = modifier
                    .clip(MaterialTheme.shapes.large)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(12.dp)
                    .defaultMinSize(minWidth = 220.dp),
            ) {
                item {
                    Text(
                        text = title,
                        modifier = Modifier.padding(vertical = 8.dp),
                        style = MaterialTheme.typography.headlineSmall,
                    )
                }

                groups.forEach { group ->
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Checkbox(
                                checked = when (group.status) {
                                    MovieStatus.NOT_IN_GROUP,
                                    MovieStatus.WATCHED_BY_OTHER,
                                    MovieStatus.TO_WATCH_BY_OTHER,
                                    -> false

                                    MovieStatus.TO_WATCH_BY_USER -> dialogType == DialogType.TO_SEE
                                    MovieStatus.WATCHED_BY_USER -> dialogType == DialogType.SEEN
                                },
                                onCheckedChange = { isChecked ->
                                    onCheck(group.groupId, isChecked)
                                },
                                modifier = Modifier.padding(end = 8.dp),
                            )
                            Text(
                                text = group.groupName,
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .wrapContentHeight(Alignment.CenterVertically),
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 2,
                            )
                        }
                    }
                }
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun SelectGroupsDialogPreview() {
    FamilyFilmAppTheme {
        SelectGroupsDialog(
            title = "Select groups",
            groups = listOf(
                GroupStatus(1, "Group 1", MovieStatus.TO_WATCH_BY_USER),
                GroupStatus(2, "Group 2", MovieStatus.WATCHED_BY_USER),
            ),
            dialogType = DialogType.TO_SEE,
        )
    }
}
