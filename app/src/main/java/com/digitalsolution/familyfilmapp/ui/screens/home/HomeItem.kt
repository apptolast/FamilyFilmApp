package com.digitalsolution.familyfilmapp.ui.screens.home

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.digitalsolution.familyfilmapp.model.local.Movie
import com.digitalsolution.familyfilmapp.ui.theme.FamilyFilmAppTheme
import kotlin.random.Random

@Composable
fun CustomCard(
    modifier: Modifier = Modifier,
    block: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.padding(horizontal = 17.dp),
        shape = RoundedCornerShape(4.dp),
        elevation = CardDefaults.cardElevation(5.dp),
        content = block
    )
}

@Composable
fun HomeItem(
    movie: Movie,
    navigateToDetailsScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    CustomCard(modifier = modifier.clickable { navigateToDetailsScreen() }) {
        Column {
//            FIXME: This is for test
            AsyncImage(
                model = "https://loremflickr.com/400/400/cat?lock=${Random.nextInt(20 + 1)}",
//                model = movie.image,
                contentDescription = null,
                modifier = Modifier.fillMaxHeight(0.75f),
                contentScale = ContentScale.Crop
            )
            Text(
                text = movie.title,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp, horizontal = 8.dp),
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                minLines = 2
            )
        }
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    showBackground = true,
    backgroundColor = 0xFF1F1B16,
)
@Composable
fun HomeItemPreview() {
    FamilyFilmAppTheme {
        HomeItem(movie = Movie(
            title = "Title Title Title Title Title Title Title Title ",
            image = "https://loremflickr.com/400/400/cat?lock=1"
        ), navigateToDetailsScreen = {})
    }
}
