package com.apptolast.familyfilmapp.ui.screens.groups.components

import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
            MovieItem(
                movie = movie,
                modifier = Modifier.clip(MaterialTheme.shapes.medium),
                onClick = onMovieClick,
            )
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
                Movie().copy(id = 1, title = "Title 1", overview = "Description 1"),
                Movie().copy(id = 2, title = "Title 2", overview = "Description 2"),
                Movie().copy(id = 3, title = "Title 3", overview = "Description 3"),
            ),
        )
    }
}
