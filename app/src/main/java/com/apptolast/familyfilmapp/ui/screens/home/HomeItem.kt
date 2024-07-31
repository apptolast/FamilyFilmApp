package com.apptolast.familyfilmapp.ui.screens.home

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.apptolast.familyfilmapp.model.local.MovieCatalogue
import com.apptolast.familyfilmapp.ui.components.CustomCard
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme

@Composable
fun HomeItem(movie: MovieCatalogue, modifier: Modifier = Modifier) {
    val baseUrl = "https://image.tmdb.org/t/p/original/"
    CustomCard(
        modifier = modifier.clickable { },
        content = {
            AsyncImage(
                model = "${baseUrl}${movie.image}",
                contentDescription = movie.title,
                contentScale = ContentScale.Inside,
            )
            Text(
                text = movie.title,
            )
        },
    )
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    showBackground = true,
    backgroundColor = 0xFF1F1B16,
)
@Composable
private fun HomeItemPreview() {
    FamilyFilmAppTheme {
    }
}
