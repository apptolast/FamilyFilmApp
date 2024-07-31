package com.apptolast.familyfilmapp.ui.screens.home

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.apptolast.familyfilmapp.R
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
    val context = LocalContext.current
    var searchQuery by rememberSaveable { mutableStateOf("") }
    // val filteredMovies = movies.filter { it.title.contains(searchQuery, ignoreCase = true) }

    Column(modifier = modifier.fillMaxSize()) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            value = searchQuery,
            onValueChange = {
                searchQuery = it
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
                    // TODO: Implement search
                    Toast.makeText(context, "Search: $searchQuery", Toast.LENGTH_SHORT).show()
                },
            ),
        )

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
