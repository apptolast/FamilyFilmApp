package com.apptolast.familyfilmapp.ui.screens.groups.components

import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.apptolast.familyfilmapp.model.local.Movie
import com.apptolast.familyfilmapp.ui.screens.home.MovieItem
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme


@Composable
fun HorizontalScrollableMovies(
    movies: List<Movie>,
    modifier: Modifier = Modifier,
    onMovieClick: (Movie) -> Unit = {},
) {
    LazyRow(modifier = modifier) {
        items(movies) { movie ->
            MovieItem(movie = movie, onClick = onMovieClick)
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun HorizontalScrollableMoviesPreview() {
    FamilyFilmAppTheme {
        HorizontalScrollableMovies(
            movies =
            listOf(
                Movie().copy(1, "Title 1", "Description 1"),
                Movie().copy(2, "Title 2", "Description 2"),
                Movie().copy(3, "Title 3", "Description 3"),
            ),
        )
    }
}
