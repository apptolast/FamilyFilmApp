package com.apptolast.familyfilmapp.ui.screens.groups.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.apptolast.familyfilmapp.model.local.Movie
import com.apptolast.familyfilmapp.ui.screens.home.MovieItem
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme

@Composable
fun HorizontalScrollableMovies(movies: List<Movie>, onMovieClick: (Movie) -> Unit = {}) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(movies) { movie ->
            MovieItem(
                modifier = Modifier.width(130.dp),
                movie = movie,
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
            movies = listOf(
                Movie().copy(id = 1, title = "Title 1", overview = "Description 1"),
                Movie().copy(id = 2, title = "Title 2", overview = "Description 2"),
                Movie().copy(id = 3, title = "Title 3", overview = "Description 3"),
            ),
        )
    }
}
