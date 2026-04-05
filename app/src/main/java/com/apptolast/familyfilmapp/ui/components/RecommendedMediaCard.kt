package com.apptolast.familyfilmapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.apptolast.familyfilmapp.R
import com.apptolast.familyfilmapp.model.local.Media
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme

@Composable
fun RecommendedMediaCard(media: Media, modifier: Modifier = Modifier, navigateToDetailsScreen: (Media) -> Unit) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    navigateToDetailsScreen(media)
                },
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            AsyncImage(
                model = media.posterPath,
                contentDescription = media.title,
                contentScale = ContentScale.Inside,
            )
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Top,
            ) {
                Text(
                    text = media.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Left,
                )
                Text(
                    text = media.overview,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(12.dp),
                    verticalAlignment = Alignment.Bottom,
                ) {
                    Button(
                        onClick = { navigateToDetailsScreen(media) },
                        modifier = Modifier
                            .width(200.dp)
                            .height(35.dp),
                    ) {
                        Text(text = stringResource(id = R.string.text_read_more))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewRecommendedMediaCard() {
    FamilyFilmAppTheme {
        RecommendedMediaCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            media = Media(
                title = "Media Title",
                posterPath = "https://image.tmdb.org/t/p/original/ar2h87jlTfMlrDZefR3VFz1SfgH.jpg",
            ),
            navigateToDetailsScreen = {},
        )
    }
}
