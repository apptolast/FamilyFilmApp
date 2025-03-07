package com.apptolast.familyfilmapp.ui.screens.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import com.apptolast.familyfilmapp.model.local.Movie
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.model.local.types.MovieStatus
import com.apptolast.familyfilmapp.ui.screens.home.BASE_URL
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme

@Composable
fun DetailsScreenRoot(movie: Movie, viewModel: DetailScreenViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    DetailsScreen(
        movie = movie,
        state = state,
        onStatusChange = { status ->
            viewModel.updateMovieStatus(movie, status)
        },
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DetailsScreen(movie: Movie, state: DetailScreenStateState, onStatusChange: (MovieStatus) -> Unit = { }) {
    val snackBarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
    ) { paddingValues ->

        DetailsContent(
            movie = movie,
            user = state.user,
            modifier = Modifier.padding(paddingValues),
            onStatusChange = onStatusChange,
        )
    }
}

@Composable
fun DetailsContent(
    movie: Movie,
    user: User,
    modifier: Modifier = Modifier,
    onStatusChange: (MovieStatus) -> Unit = { },
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
                    CustomStatusButton(
                        text = "To Watch",
                        icon = Icons.Default.Add,
                        isSelected = user.statusMovies[movie.id.toString()] == MovieStatus.ToWatch,
                        modifier = Modifier.weight(1f),
                        onClick = { onStatusChange(MovieStatus.ToWatch) },
                    )

                    Spacer(modifier = Modifier.width(14.dp))

                    CustomStatusButton(
                        text = "Watched",
                        icon = Icons.Default.Visibility,
                        isSelected = user.statusMovies[movie.id.toString()] == MovieStatus.Watched,
                        modifier = Modifier.weight(1f),
                        onClick = { onStatusChange(MovieStatus.Watched) },
                    )
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
fun CustomStatusButton(
    text: String,
    icon: ImageVector,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    val content: @Composable RowScope.() -> Unit = {
        DetailsButtonContent(icon = icon, text = text)
    }

    if (isSelected) {
        Button(
            onClick = onClick,
            modifier = modifier,
            content = content,
        )
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier,
            content = content,
        )
    }
}

@Composable
private fun RowScope.DetailsButtonContent(icon: ImageVector, text: String, selected: Boolean = false) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        modifier = Modifier.padding(end = 6.dp),
    )
    Text(text = text)
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun DetailsScreenPreview() {
    FamilyFilmAppTheme {
        DetailsContent(
            movie = Movie().copy(
                id = 1,
                title = "Movie title",
                posterPath = "/poster.jpg",
                adult = true,
                releaseDate = "2023-01-01",
            ),
            user = User().copy(
                id = "1",
                email = "a@a.com",
                statusMovies = mapOf(),
            ),
        )
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun DetailsScreenToWatchPreview() {
    FamilyFilmAppTheme {
        DetailsContent(
            movie = Movie().copy(
                id = 1,
                title = "Movie title",
                posterPath = "/poster.jpg",
                adult = true,
                releaseDate = "2023-01-01",
            ),
            user = User().copy(
                id = "1",
                email = "a@a.com",
                statusMovies = mapOf("1" to MovieStatus.ToWatch),
            ),
        )
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun DetailsScreenWatchedPreview() {
    FamilyFilmAppTheme {
        DetailsContent(
            movie = Movie().copy(
                id = 1,
                title = "Movie title",
                posterPath = "/poster.jpg",
                adult = true,
                releaseDate = "2023-01-01",
            ),
            user = User().copy(
                id = "1",
                email = "a@a.com",
                statusMovies = mapOf("1" to MovieStatus.Watched),
            ),
        )
    }
}
