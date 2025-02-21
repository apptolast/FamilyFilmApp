package com.apptolast.familyfilmapp.ui.screens.search.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.apptolast.familyfilmapp.model.local.Movie
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme

@Composable
fun MovieItem(movie: Movie, modifier: Modifier = Modifier, onNavigateDetailScreen: (Movie) -> Unit = {}) {
    Row(
        modifier = modifier
            .padding(12.dp)
            .clickable {
                onNavigateDetailScreen(movie)
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = movie.posterPath,
            contentDescription = null,
            modifier = Modifier
                .size(width = 180.dp, height = 118.dp)
                .clip(shape = RoundedCornerShape(10.dp)),
            contentScale = ContentScale.Crop,
        )
        Text(
            text = movie.title,
            modifier = Modifier
                .padding(10.dp)
                .padding(bottom = 4.dp),
            style = MaterialTheme.typography.titleSmall,
        )
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun MovieItemPreview() {
    FamilyFilmAppTheme {
        MovieItem(Movie()) {}
    }
}
