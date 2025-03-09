package com.apptolast.familyfilmapp.ui.screens.home

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.apptolast.familyfilmapp.R
import com.apptolast.familyfilmapp.model.local.Movie
import com.apptolast.familyfilmapp.navigation.Routes
import com.apptolast.familyfilmapp.navigation.navtypes.DetailNavTypeDestination
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import kotlinx.coroutines.flow.flowOf

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel(), onClickNav: (String) -> Unit) {
    val stateUI by viewModel.homeUiState.collectAsStateWithLifecycle()
    val movies: LazyPagingItems<Movie> = viewModel.movies.collectAsLazyPagingItems()

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val snackBarHostState = remember { SnackbarHostState() }
    val errorMessage = viewModel.homeUiState.value.errorMessage?.error

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackBarHostState.showSnackbar(it)
            viewModel.clearError() // Limpiar error después de mostrar el Snackbar
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(stringResource(R.string.movies))
                        Row {
                            IconButton(onClick = { onClickNav(Routes.Groups.routes) }) {
                                Icon(
                                    imageVector = Icons.Outlined.Groups,
                                    contentDescription = Icons.Outlined.Groups.toString(),
                                )
                            }
                            IconButton(onClick = { onClickNav(Routes.Profile.routes) }) {
                                Icon(
                                    imageVector = Icons.Outlined.Settings,
                                    contentDescription = Icons.Outlined.Settings.toString(),
                                )
                            }
                        }
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { paddingValues ->
        Box {
            HomeContent(
                movies = movies,
                filterMovies = stateUI.filterMovies,
                modifier = Modifier.padding(top = paddingValues.calculateTopPadding().value.dp),
                onMovieClick = { movie ->
                    onClickNav(DetailNavTypeDestination.getDestination(movie))
                },
                searchMovieByNameBody = viewModel::searchMovieByName,
            )

            LoadStateContent(
                movies = movies,
                triggerError = viewModel::triggerError,
            )
        }
    }
}

@Composable
fun HomeContent(
    movies: LazyPagingItems<Movie>,
    filterMovies: List<Movie>,
    onMovieClick: (Movie) -> Unit,
    modifier: Modifier = Modifier,
    searchMovieByNameBody: (String) -> Unit,
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
    ) {

        MovieGridList(
            movies = movies,
            filterMovies = filterMovies,
            onMovieClick = onMovieClick,
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                searchMovieByNameBody(it)
            },
            shape = MaterialTheme.shapes.small,
            leadingIcon = {
                Icon(imageVector = Icons.Filled.Search, contentDescription = "")
            },
            trailingIcon = {
                AnimatedVisibility(searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(
                            imageVector = Icons.Default.Close, // Ícono de "X"
                            contentDescription = "Borrar texto"
                        )
                    }
                }
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
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                disabledContainerColor = MaterialTheme.colorScheme.background,
                errorContainerColor = MaterialTheme.colorScheme.background,
            ),
        )
    }
}

@Composable
private fun MovieGridList(
    movies: LazyPagingItems<Movie>,
    filterMovies: List<Movie>,
    onMovieClick: (Movie) -> Unit = {},
) {

    AnimatedVisibility(filterMovies.isNotEmpty()) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(100.dp),
            horizontalArrangement = Arrangement.spacedBy(17.dp),
            verticalArrangement = Arrangement.spacedBy(17.dp),
            contentPadding = PaddingValues(top = 76.dp),
        ) {
            items(filterMovies) { movie ->
                MovieItem(
                    movie = movie,
                    onClick = onMovieClick,
                )
            }
        }
    }
    AnimatedVisibility(filterMovies.isEmpty()) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(100.dp),
            horizontalArrangement = Arrangement.spacedBy(17.dp),
            verticalArrangement = Arrangement.spacedBy(17.dp),
            contentPadding = PaddingValues(top = 76.dp),
        ) {
            items(movies.itemCount) { index ->
                MovieItem(
                    movie = movies[index]!!,
                    onClick = onMovieClick,
                )
            }
        }
    }
}

@Composable
private fun LoadStateContent(movies: LazyPagingItems<Movie>, triggerError: (String) -> Unit) {

    movies.apply {
        when {
            loadState.refresh is LoadState.Loading -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            loadState.refresh is LoadState.Error -> {
                val error = movies.loadState.refresh as LoadState.Error
                triggerError(error.error.localizedMessage!!)
            }

            loadState.append is LoadState.Loading -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            loadState.append is LoadState.Error -> {
                val error = movies.loadState.append as LoadState.Error
                triggerError(error.error.localizedMessage!!)
            }
        }
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
                    sourceLoadStates = LoadStates(LoadState.Loading, LoadState.Loading, LoadState.Loading),
                ),
            ).collectAsLazyPagingItems(),
            filterMovies = emptyList(),
            onMovieClick = {},
            searchMovieByNameBody = {},
        )
    }
}
