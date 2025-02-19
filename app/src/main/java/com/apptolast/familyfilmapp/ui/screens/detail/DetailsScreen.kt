package com.apptolast.familyfilmapp.ui.screens.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.apptolast.familyfilmapp.model.local.Group
import com.apptolast.familyfilmapp.model.local.Movie
import com.apptolast.familyfilmapp.ui.components.dialogs.SelectGroupsDialog
import com.apptolast.familyfilmapp.ui.screens.home.BASE_URL
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme

@Composable
fun DetailsScreenRoot(movie: Movie, viewModel: DetailScreenViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    DetailsScreen(
        movie = movie,
        state = state,
        displayDialog = viewModel::displayDialog,
        updateGroup = { group, isChecked ->
            viewModel.updateMovieGroup(
                movieId = movie.id,
                group = group,
                isChecked = isChecked,
            )
        },
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DetailsScreen(
    movie: Movie,
    state: DetailScreenStateState,
    displayDialog: (DialogType) -> Unit = { _ -> },
    updateGroup: (Group, Boolean) -> Unit = { _, _ -> },
) {
    val snackBarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
    ) { paddingValues ->

        DetailsContent(
            movie = movie,
            modifier = Modifier.padding(paddingValues),
            displayDialog = displayDialog,
        )
    }

    if (state.dialogType != DialogType.NONE) {
        SelectGroupsDialog(
            movieId = movie.id,
            title = "Select groups - ${state.dialogType.name}",
            user = state.user,
            groups = state.groups,
            dialogType = state.dialogType,
            onCancel = {
                displayDialog(DialogType.NONE)
            },
            onCheck = { group, isChecked ->
                updateGroup(group, isChecked)
            },
        )
    }
}

@Composable
fun DetailsContent(
    movie: Movie,
    modifier: Modifier = Modifier,
    displayDialog: (DialogType) -> Unit = { _ -> },
) {
    val lazyListState = rememberLazyListState()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        state = lazyListState,
    ) {
        item {
            AsyncImage(
                model = "${BASE_URL}${movie.posterPath}",
                contentDescription = null,
                clipToBounds = true,
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(16.dp)
                    .height(430.dp)
                    .clip(MaterialTheme.shapes.large),
                contentScale = ContentScale.Fit,
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
                    Text(text = "")
                    Text(text = if (movie.adult) "+18" else "")
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Button(
                        onClick = { displayDialog(DialogType.ToWatch) },
                        modifier = Modifier.weight(1f),
                    ) {
                        DetailsButtonContent(icon = Icons.Default.Add, text = "To Watch")
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    OutlinedButton(
                        onClick = { displayDialog(DialogType.Watched) },
                        modifier = Modifier.weight(1f),
                    ) {
                        DetailsButtonContent(icon = Icons.Default.Visibility, text = "Watched")
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
        DetailsContent(
            movie = Movie().copy(
                title = "Movie title",
                posterPath = "/poster.jpg",
                adult = true,
                releaseDate = "2023-01-01",
            ),
        )
    }
}
