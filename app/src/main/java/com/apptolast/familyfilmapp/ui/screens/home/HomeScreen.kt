package com.apptolast.familyfilmapp.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.apptolast.familyfilmapp.R
import com.apptolast.familyfilmapp.model.local.Movie
import com.apptolast.familyfilmapp.navigation.navtypes.DetailNavTypeDestination
import com.apptolast.familyfilmapp.ui.components.BottomBar
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import kotlinx.coroutines.flow.flowOf

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val movieItems: LazyPagingItems<Movie> = viewModel.movies.collectAsLazyPagingItems()

    Scaffold(
        bottomBar = { BottomBar(navController = navController) },
    ) { paddingValues ->
        HomeContent(
            movies = movieItems,
            filterMovies = state.filterMovies,
            modifier = Modifier.padding(paddingValues),
            onMovieClick = { movie ->
                navController.navigate(DetailNavTypeDestination.getDestination(movie))
            },
            searchMovieByNameBody = { filter ->
                viewModel.searchMovieByName(filter)
            },
        )
    }
}

@Composable
fun HomeContent(
    movies: LazyPagingItems<Movie>,
    filterMovies: List<Movie>,
    modifier: Modifier = Modifier,
    onMovieClick: (Movie) -> Unit = {},
    searchMovieByNameBody: (String) -> Unit = {},
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }

    Column(modifier = modifier.fillMaxSize()) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                searchMovieByNameBody(it)
            },
            shape = RoundedCornerShape(12.dp),
            leadingIcon = {
                Icon(imageVector = Icons.Filled.Search, contentDescription = "")
            },
            label = {
                Text(text = stringResource(R.string.search_film_or_series))
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search,
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    searchMovieByNameBody(searchQuery)
                },
            ),
        )

        RowMovie(
            movies = movies,
            filterMovies = filterMovies,
            modifier = Modifier.weight(1f),
            onMovieClick = onMovieClick,
        )
    }
}

@Composable
private fun RowMovie(
    movies: LazyPagingItems<Movie>,
    filterMovies: List<Movie>,
    modifier: Modifier = Modifier,
    onMovieClick: (Movie) -> Unit = {},
) {
    if (filterMovies.isNotEmpty()) {
        Column(modifier = modifier) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.padding(bottom = 15.dp),
            ) {
                items(filterMovies) { movie ->
                    MovieItem(
                        movie = movie,
                        onClick = onMovieClick,
                    )
                }
            }
        }
    } else {
        Column(modifier = modifier) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.padding(bottom = 15.dp),
            ) {
                items(movies.itemCount) { index ->
                    MovieItem(
                        movie = movies[index]!!,
                        onClick = onMovieClick,
                    )
                }
            }

            movies.apply {
                when {
                    loadState.refresh is LoadState.Loading -> {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = modifier.fillMaxSize(),
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }

                    loadState.refresh is LoadState.Error -> {
                        val error = movies.loadState.refresh as LoadState.Error

                        ErrorMessage(
                            modifier = Modifier,
                            message = error.error.localizedMessage!!,
                            onClickRetry = { retry() },
                        )

                    }

                    loadState.append is LoadState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .size(20.dp),
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }

                    loadState.append is LoadState.Error -> {
                        val error = movies.loadState.append as LoadState.Error
                        ErrorMessage(
                            modifier = Modifier,
                            message = error.error.localizedMessage!!,
                            onClickRetry = { retry() },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ErrorMessage(message: String, modifier: Modifier = Modifier, onClickRetry: () -> Unit) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )

        OutlinedButton(
            onClick = onClickRetry,
            modifier = Modifier
                .padding(16.dp),
            content = {
                Text(text = "Retry")
            },
        )
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun HomeContentPreview() {
    FamilyFilmAppTheme {
        HomeContent(
            movies = flowOf(
                PagingData.from(
                    listOf(
                        Movie().copy(
                            title = "Matrix",
                            overview = """
                        "Trata sobre un programador que descubre que la realidad en la que vive es
                         una simulación creada por máquinas."
                    """.trimIndent(),
                            posterPath = "https://image.tmdb.org/t/p/w500/ar2h87jlTfMlrDZefR3VFz1SfgH.jpg",
                        ),
                    ),
                ),
            ).collectAsLazyPagingItems(),
            filterMovies = emptyList(),
            onMovieClick = {},
            searchMovieByNameBody = {},
        )
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun HomeContentLoadingPreview() {
    FamilyFilmAppTheme {
        HomeContent(
            movies = flowOf(
                PagingData.from(
                    listOf(
                        Movie().copy(
                            title = "Matrix",
                            overview = """
                        "Trata sobre un programador que descubre que la realidad en la que vive es
                         una simulación creada por máquinas."
                    """.trimIndent(),
                            posterPath = "https://image.tmdb.org/t/p/w500/ar2h87jlTfMlrDZefR3VFz1SfgH.jpg",
                        ),
                    ),
                    sourceLoadStates = LoadStates(LoadState.Loading, LoadState.Loading, LoadState.Loading),
                ),
            ).collectAsLazyPagingItems(),
            filterMovies = emptyList(),
            onMovieClick = {},
            searchMovieByNameBody = {},
        )
    }
}


@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun HomeContentErrorPreview() {
    FamilyFilmAppTheme {
        HomeContent(
            movies = flowOf(
                PagingData.from(
                    listOf(
                        Movie().copy(
                            title = "Matrix",
                            overview = """
                        "Trata sobre un programador que descubre que la realidad en la que vive es
                         una simulación creada por máquinas."
                    """.trimIndent(),
                            posterPath = "https://image.tmdb.org/t/p/w500/ar2h87jlTfMlrDZefR3VFz1SfgH.jpg",
                        ),
                    ),
                    sourceLoadStates = LoadStates(
                        LoadState.Error(Throwable("Error")),
                        LoadState.Error(Throwable("Error")),
                        LoadState.Error(Throwable("Error")),
                    ),
                ),
            ).collectAsLazyPagingItems(),
            filterMovies = emptyList(),
            onMovieClick = {},
            searchMovieByNameBody = {},
        )
    }
}
