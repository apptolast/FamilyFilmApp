package com.apptolast.familyfilmapp.ui.components.dialogs

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
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.ui.screens.detail.DialogType
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme

@SuppressLint("MutableCollectionMutableState")
@Composable
fun SelectGroupsDialog(
    movieId: Int,
    title: String,
    user: User,
    groups: List<Group>,
    dialogType: DialogType,
    modifier: Modifier = Modifier,
    onCancel: () -> Unit = {},
    onCheck: (Group, Boolean,) -> Unit = { _, _ -> },
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
                                checked = when (dialogType) {
                                    DialogType.ToWatch -> {
                                        val movieInUserList = user.toWatch.any { it.movieId == movieId }
                                        val groupInMovie =
                                            user.toWatch.find { it.movieId == movieId }?.groups?.any { it.id == group.id } == true
                                        movieInUserList && groupInMovie
                                    }

                                    DialogType.Watched -> {
                                        val movieInUserList = user.watched.any { it.movieId == movieId }
                                        val groupInMovie =
                                            user.watched.find { it.movieId == movieId }?.groups?.any { it.id == group.id } == true
                                        movieInUserList && groupInMovie
                                    }

                                    else -> false
                                },
                                onCheckedChange = { isChecked ->
                                    onCheck(group, isChecked)
                                },
                                modifier = Modifier.padding(end = 8.dp),
                            )
                            Text(
                                text = group.name,
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
            movieId = 1,
            title = "Select groups",
            user = User(),
            groups = listOf(
                Group().copy(name = "group 1"),
                Group().copy(name = "group 2"),
            ),
            dialogType = DialogType.ToWatch,
        )
    }
}
