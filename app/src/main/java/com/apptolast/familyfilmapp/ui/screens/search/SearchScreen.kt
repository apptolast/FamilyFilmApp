package com.apptolast.familyfilmapp.ui.screens.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.apptolast.familyfilmapp.R
import com.apptolast.familyfilmapp.model.local.Movie
import com.apptolast.familyfilmapp.navigation.navtypes.DetailNavTypeDestination
import com.apptolast.familyfilmapp.ui.components.RecommendedMovieCard
import com.apptolast.familyfilmapp.ui.components.tabgroups.TabGroupsViewModel
import com.apptolast.familyfilmapp.ui.screens.search.states.SearchScreenUI
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme

@Composable
fun SearchScreen(
    navController: NavController,
    groupId: Int,
    viewModel: SearchViewModel = hiltViewModel(),
    tabGroupsViewModel: TabGroupsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val searchUiState by viewModel.uiState.collectAsStateWithLifecycle()

    val tabUiState by tabGroupsViewModel.uiState.collectAsStateWithLifecycle()
    val tabBackendState by tabGroupsViewModel.backendState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = searchUiState.searchQuery.value) {
        viewModel.getMovieQuery()
    }

    Scaffold { paddingValues ->
        SearchContent(
            movies = searchUiState.searchResults.value.ifEmpty { uiState.movies },
            searchUiState,
            modifier = Modifier.padding(paddingValues),
            onNavigateDetailScreen = { movie ->
                navController.navigate(DetailNavTypeDestination.getDestination(movie, groupId))
            },
            onChangeSearchQuery = {
                viewModel.onSearchQueryChanged(it)
            },
        )
    }
}

@Composable
fun SearchContent(
    movies: List<Movie>,
    searchUiState: SearchScreenUI,
    onNavigateDetailScreen: (Movie) -> Unit,
    onChangeSearchQuery: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 5.dp)
            .padding(12.dp),
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            value = searchUiState.searchQuery.value,
            onValueChange = {
                onChangeSearchQuery(it)
            },
            shape = RoundedCornerShape(12.dp),
            leadingIcon = {
                Icon(imageVector = Icons.Filled.Search, contentDescription = "")
            },
            label = {
                Text(text = stringResource(R.string.search_film_or_series))
            },
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 10.dp),
        ) {
            items(movies) { movie ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(500)) + slideInVertically(animationSpec = tween(500)),
                    exit = fadeOut(animationSpec = tween(500)),
                ) {
                    RecommendedMovieCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        movie = movie,
                        navigateToDetailsScreen = { onNavigateDetailScreen(it) },
                    )
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun SearchScreenPreview() {
    FamilyFilmAppTheme {
        SearchScreen(
            NavController(LocalContext.current),
            -1,
        )
    }
}
