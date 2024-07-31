package com.apptolast.familyfilmapp.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.apptolast.familyfilmapp.model.local.MovieCatalogue
import com.apptolast.familyfilmapp.ui.components.BottomBar
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = hiltViewModel()) {
    val stateUI by viewModel.homeUiState.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = { BottomBar(navController = navController) },
    ) { paddingValues ->
        HomeContent(
            movies = stateUI.movies,
            modifier = Modifier.padding(paddingValues),
        )
    }
}

@Composable
fun HomeContent(movies: List<MovieCatalogue>, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        RowMovie(
            movies = movies,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun RowMovie(movies: List<MovieCatalogue>, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        LazyRow(modifier = Modifier.padding(bottom = 15.dp)) {
            items(movies) { movie ->
                HomeItem(
                    movie = movie,
                )
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun HomeContentPreview() {
    FamilyFilmAppTheme {
        HomeContent(
            movies = emptyList(),
        )
    }
}
