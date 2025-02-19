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
    onCheck: (Group, Boolean) -> Unit = { _, _ -> },
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
                        style = MaterialTheme.typography.titleSmall,
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

                                        // Para que sea check, la pelicula tiene que estar en la lista "toWatch"
                                        // del usuario (user.toWatch) del usuario y que el grupo coincida con el del grupo
                                        // seleccionado (group.id)
                                        user.toWatch.any { selectedMovie ->
                                            selectedMovie.movieId == movieId &&
                                               selectedMovie.groupsIds.contains(group.id)
                                        }



//                                        user.toWatch.any { selectedMovie ->
//                                            selectedMovie.movieId == movieId &&
//                                                selectedMovie.groupsIds.contains(group.id)
//                                        }


                                        // To know when to perform the check:
                                        // The user must have the movie in the "toWatch" list
                                        // and the selected group must be in "toWatch.groups"
//                                        user.toWatch
//                                            .find { it.movieId == movieId }
//                                            ?.groupsIds
//                                            ?.any { it == group.id } == true
                                    }

                                    DialogType.Watched -> {
                                        user.watched.any { selectedMovie ->
                                            selectedMovie.movieId == movieId && selectedMovie.groupsIds.contains(group.id)
                                        }

//                                        user.watched
//                                            .find { it.movieId == movieId }
//                                            ?.groupsIds
//                                            ?.any { it == group.id } == true
                                    }

                                    else -> false
                                },

//                                checked = when (dialogType) {
//                                    DialogType.ToWatch -> {
//                                        user.toWatch.any { selectedMovie ->
//                                            selectedMovie.movieId == movieId && selectedMovie.groupsIds.contains(group.id)
//                                        }
//                                    }
//
//                                    DialogType.Watched -> {
//                                        user.watched.any { selectedMovie ->
//                                            selectedMovie.movieId == movieId && selectedMovie.groupsIds.contains(group.id)
//                                        }
//                                    }
//
//                                    else -> false
//                                },

                                onCheckedChange = { isChecked ->
                                    onCheck(group, isChecked)
                                },
                                modifier = Modifier.padding(end = 8.dp),
                            )
                            Text(
                                text = group.name,
                                style = MaterialTheme.typography.titleSmall,
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
