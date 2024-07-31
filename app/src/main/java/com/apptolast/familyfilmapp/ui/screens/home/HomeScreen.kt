package com.apptolast.familyfilmapp.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.padding(bottom = 15.dp),
        ) {
            items(movies) { movie ->
                MovieItem(
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
            movies = listOf(
                MovieCatalogue().copy(
                    title = "Matrix",
                    synopsis = """
                        "Trata sobre un programador que descubre que la realidad en la que vive es
                         una simulación creada por máquinas."
                    """.trimIndent(),
                    image = "https://image.tmdb.org/t/p/w500/ar2h87jlTfMlrDZefR3VFz1SfgH.jpg",
                ),
            ),
        )
    }
}
