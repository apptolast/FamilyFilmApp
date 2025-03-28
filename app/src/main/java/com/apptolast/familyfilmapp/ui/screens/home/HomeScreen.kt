package com.apptolast.familyfilmapp.ui.screens.home

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
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
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    onClickNav: (String) -> Unit = {},
) {
    val stateUI by viewModel.homeUiState.collectAsStateWithLifecycle()
    val movies: LazyPagingItems<Movie> = viewModel.movies.collectAsLazyPagingItems()

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val snackBarHostState = remember { SnackbarHostState() }
    val errorMessage = viewModel.homeUiState.value.errorMessage?.error

    val animatedColor by animateColorAsState(
        targetValue = lerp(
            MaterialTheme.colorScheme.surface.copy(alpha = 1f),
            Color.Transparent,
            scrollBehavior.state.collapsedFraction,
        ),
        label = "Color transition",
    )

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackBarHostState.showSnackbar(it)
            viewModel.clearError() // Clear error after showing
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.movies),
                        style = MaterialTheme.typography.headlineLarge,
                    )
                },
                actions = {
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
                },
                colors = TopAppBarDefaults.topAppBarColors().copy(
                    scrolledContainerColor = animatedColor,
                    containerColor = animatedColor,
                ),
                scrollBehavior = scrollBehavior,
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
        ) {
            HomeContent(
                modifier = Modifier.padding(paddingValues),
                movies = movies,
                onMovieClick = { movie ->
                    onClickNav(DetailNavTypeDestination.getDestination(movie))
                },
                searchMovieByNameBody = viewModel::searchMovieByName,
                stateUI = stateUI,
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
    stateUI: HomeUiState,
    movies: LazyPagingItems<Movie>,
    modifier: Modifier = Modifier,
    onMovieClick: (Movie) -> Unit = {},
    searchMovieByNameBody: (String) -> Unit = {},
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }

    MovieGridList(
        movies = movies,
        stateUi = stateUI,
        onMovieClick = onMovieClick,
    )

    OutlinedTextField(
        modifier = modifier
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
                        contentDescription = "Borrar texto",
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

@Composable
private fun MovieGridList(movies: LazyPagingItems<Movie>, stateUi: HomeUiState, onMovieClick: (Movie) -> Unit = {}) {
    val filterMovies = stateUi.filterMovies

    AnimatedVisibility(filterMovies.isNotEmpty()) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(100.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(top = 180.dp, bottom = 8.dp),
        ) {
            items(filterMovies) { movie ->
                val status = stateUi.user.statusMovies[movie.id.toString()]
                MovieItem(
                    movie = movie,
                    onClick = onMovieClick,
                    status = status,
                )
            }
        }
    }
    AnimatedVisibility(filterMovies.isEmpty()) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(100.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(top = 180.dp, bottom = 8.dp),
        ) {
            items(movies.itemCount) { index ->
                val status = stateUi.user.statusMovies[movies[index]?.id.toString()]
                MovieItem(
                    movie = movies[index]!!,
                    onClick = onMovieClick,
                    status = status,
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
            stateUI = HomeUiState(),
            onMovieClick = {},
            searchMovieByNameBody = {},
        )
    }
}
