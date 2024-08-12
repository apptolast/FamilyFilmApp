package com.apptolast.familyfilmapp.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.apptolast.familyfilmapp.model.local.MovieCatalogue
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme

const val BASE_URL = "https://image.tmdb.org/t/p/original/"

@Composable
fun MovieItem(movie: MovieCatalogue, modifier: Modifier = Modifier, onClick: (MovieCatalogue) -> Unit) {
    Box(
        modifier = modifier
            .width(IntrinsicSize.Max)
            .padding(4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(4.dp)
                .clickable { onClick(movie) },
        ) {
            AsyncImage(
                model = if (movie.image.isEmpty()) {
                    "https://picsum.photos/133/200"
                } else {
                    "${BASE_URL}${movie.image}"
                },
                contentDescription = null,
                modifier = Modifier
                    .size(width = 133.dp, height = 200.dp)
                    .clip(
                        shape = MaterialTheme.shapes.medium,
                    ),
                contentScale = ContentScale.FillHeight,
            )

            Text(
                text = movie.title,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp),
                textAlign = TextAlign.Center,
                minLines = 2,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeItemPreview() {
    FamilyFilmAppTheme {
        MovieItem(
            MovieCatalogue().copy(
                title = "title",
                image = "https:///600x400/000/fff",
            ),
            onClick = {},
        )
    }
}
