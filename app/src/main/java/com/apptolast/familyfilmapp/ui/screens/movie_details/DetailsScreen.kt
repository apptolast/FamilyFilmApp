package com.apptolast.familyfilmapp.ui.screens.movie_details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.apptolast.familyfilmapp.model.local.Movie
import com.apptolast.familyfilmapp.ui.screens.home.BASE_URL
import com.apptolast.familyfilmapp.ui.screens.movie_details.DetailScreenViewModel.DialogType
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme

@Composable
fun DetailsScreenRoot(
    navController: NavController,
    movie: Movie,
    viewModel: DetailScreenViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    DetailsScreen(
        state = state,
        movie = movie,
        displayDialog = viewModel::displayDialog,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DetailsScreen(
    state: DetailScreenViewModel.State,
    movie: Movie,
    displayDialog: (DialogType) -> Unit = { _ -> },
) {

    val lazyListState = rememberLazyListState()
    val snackBarHostState = remember { SnackbarHostState() }
    var scrolledY = 0f
    var previousOffset = 0

    LaunchedEffect(key1 = state.errorMessage?.error) {
        state.errorMessage?.let {
            if (it.error.isNotBlank()) {
                snackBarHostState.showSnackbar(
                    it.error,
                    "Close",
                    true,
                    SnackbarDuration.Long,
                )
            }
        }
    }


    if (state.dialogType != DialogType.NONE) {
        Dialog(
            onDismissRequest = { displayDialog(DialogType.NONE) },
            content = {
                Column(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.large)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp),
                ) {
                    Text(text = "Select groups")

                    FlowColumn {
                        state.dialogGroupList.forEach { group ->
                            Row {
                                Checkbox(
                                    checked = false,
                                    onCheckedChange = {
                                    },
                                    modifier = Modifier.padding(end = 8.dp),
                                )
                                Text(text = group.second)
                            }
                        }
                    }
//                    LazyColumn {
//                        items(state.dialogGroupList) { groups ->
//                            ListItem(
//                                modifier = Modifier
//                                    .padding(vertical = 8.dp),
////                                    .clickable {
////                                        alarmClicked(alarm)
////                                    },
//                                overlineContent = {
//                                    Text(
//                                        text = groups.second,
//                                        style = MaterialTheme.typography.titleMedium.copy(
//                                            fontWeight = FontWeight.SemiBold,
//                                        ),
//                                    )
//                                },
//                                headlineContent = { },
//                                supportingContent = { },
//
//                                leadingContent = {
//                                    Checkbox(
//                                        checked = false,
//                                        onCheckedChange = {
//                                        },
//                                        modifier = Modifier.padding(end = 8.dp),
//                                    )
//                                },
//                            )
//                        }
//                    }
                }
            },
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            state = lazyListState,
        ) {
            item {
                AsyncImage(
                    model = "${BASE_URL}${movie.posterPath}",
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(bottomStartPercent = 5, bottomEndPercent = 5))
                        .height(420.dp)
                        .graphicsLayer {
                            scrolledY += lazyListState.firstVisibleItemScrollOffset - previousOffset
                            translationY = scrolledY * 0.5f
                            previousOffset = lazyListState.firstVisibleItemScrollOffset
                        },
                    contentScale = ContentScale.Crop,
                )
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(text = movie.releaseDate)
                        Text(text = if (movie.adult) "+18" else "")
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Button(
                            onClick = { displayDialog(DialogType.TO_SEE) },
                            modifier = Modifier.weight(1f),
                        ) {
                            DetailsButtonContent(icon = Icons.Default.Add, text = "To see")
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        OutlinedButton(
                            onClick = { displayDialog(DialogType.SEEN) },
                            modifier = Modifier.weight(1f),
                        ) {
                            DetailsButtonContent(icon = Icons.Default.Visibility, text = "Seen")
                        }
                    }
                    Text(
                        text = "Description",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(top = 15.dp),
                    )
                    Text(
                        text = movie.overview,
                        modifier = Modifier.padding(vertical = 15.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailsButtonContent(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 3.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.padding(end = 6.dp),
        )
        Text(text = text)
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true,
)
@Composable
private fun DetailsScreenPreview() {
    FamilyFilmAppTheme {
        DetailsScreen(
            state = DetailScreenViewModel.State(),
            movie = Movie(),
        )
    }
}
