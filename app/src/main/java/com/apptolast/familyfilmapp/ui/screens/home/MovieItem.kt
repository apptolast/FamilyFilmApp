package com.apptolast.familyfilmapp.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.apptolast.familyfilmapp.model.local.Movie
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme

const val BASE_URL = "https://image.tmdb.org/t/p/original/"

@Composable
fun MovieItem(movie: Movie, onClick: (Movie) -> Unit = {}) {
    AsyncImage(
        model = if (movie.posterPath.isEmpty()) {
            "https://picsum.photos/133/200"
        } else {
            "${BASE_URL}${movie.posterPath}"
        },
        contentDescription = movie.title,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .aspectRatio(2 / 3.6f)
            .clip(shape = MaterialTheme.shapes.small)
            .clickable { onClick(movie) },
        contentScale = ContentScale.FillHeight,
    )
}

@Preview(showBackground = true)
@Composable
private fun HomeItemPreview() {
    FamilyFilmAppTheme {
        MovieItem(
            Movie().copy(
                title = "title",
                posterPath = "https:///600x400/000/fff",
            ),
        )
    }
}
