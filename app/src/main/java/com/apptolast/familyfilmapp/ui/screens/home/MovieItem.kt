package com.apptolast.familyfilmapp.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlaylistAddCheckCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.apptolast.familyfilmapp.model.local.Movie
import com.apptolast.familyfilmapp.model.local.types.MovieStatus
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme

const val BASE_URL = "https://image.tmdb.org/t/p/original/"

@Composable
fun MovieItem(
    movie: Movie,
    modifier: Modifier = Modifier,
    status: MovieStatus? = null,
    onClick: (Movie) -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(2 / 3.2f)
            .clip(shape = MaterialTheme.shapes.small)
            .clickable { onClick(movie) },
    ) {
        AsyncImage(
            model = if (movie.posterPath.isEmpty()) {
                "https://picsum.photos/133/200"
            } else {
                "${BASE_URL}${movie.posterPath}"
            },
            contentDescription = movie.title,
            contentScale = ContentScale.FillHeight,
        )
        AnimatedVisibility(status != null) {
            Icon(
                imageVector = if (status == MovieStatus.Watched) {
                    Icons.Default.Visibility
                } else {
                    Icons.Default.PlaylistAddCheckCircle
                },
                contentDescription = Icons.Default.Favorite.toString(),
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.TopStart),
            )
        }
    }
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
