package com.digitalsolution.familyfilmapp.ui.screens.home

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.digitalsolution.familyfilmapp.model.local.Movie
import com.digitalsolution.familyfilmapp.ui.components.CustomCard
import com.digitalsolution.familyfilmapp.ui.theme.FamilyFilmAppTheme

@Composable
fun HomeItem(
    movie: Movie,
    navigateToDetailsScreen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CustomCard(
        modifier = modifier.clickable { navigateToDetailsScreen() },
        content = {
            AsyncImage(
                model = movie.image,
                contentDescription = movie.title,
                contentScale = ContentScale.Inside,
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
fun HomeItemPreview() {
    FamilyFilmAppTheme {
        HomeItem(
            movie = Movie(
                title = "Title Title Title Title",
                image = "https://loremflickr.com/400/400/cat?lock=1",
            ),
            navigateToDetailsScreen = {},
        )
    }
}
