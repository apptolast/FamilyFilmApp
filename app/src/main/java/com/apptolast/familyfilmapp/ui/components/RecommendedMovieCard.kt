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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
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
import com.apptolast.familyfilmapp.model.local.Movie
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import java.util.Calendar

@Composable
fun RecommendedMovieCard(movie: Movie, modifier: Modifier = Modifier, navigateToDetailsScreen: (Movie) -> Unit) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    navigateToDetailsScreen(movie)
                },
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            AsyncImage(
                model = movie.posterPath,
                contentDescription = movie.title,
                contentScale = ContentScale.Inside,
            )
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Top,
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Left,
                )
//                Row(
//                    modifier = Modifier.padding(vertical = 10.dp),
//                    horizontalArrangement = Arrangement.Start,
//                    verticalAlignment = Alignment.CenterVertically,
//                ) {
//                    Text(
//                        text = "${
//                            Calendar.getInstance().apply { time = movie.releaseDate }.get(Calendar.YEAR).plus(1900)
//                        }",
//                        style = MaterialTheme.typography.labelSmall,
//                        textAlign = TextAlign.Left,
//                    )
//                    if (movie.voteAverage != 0f) {
//                        Text(
//                            text = "|",
//                            style = MaterialTheme.typography.labelSmall,
//                            textAlign = TextAlign.Left,
//                        )
//                        Icon(
//                            imageVector = Icons.Default.Star,
//                            contentDescription = "Start",
//                            modifier = Modifier.height(12.dp),
//                        )
//                        Text(
//                            text = "${movie.voteAverage}",
//                            style = MaterialTheme.typography.labelSmall,
//                            textAlign = TextAlign.Left,
//                        )
//                    }
//                }
                Text(
                    text = movie.overview,
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
                        onClick = { navigateToDetailsScreen(movie) },
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
private fun RecommendedMovieCardPreview() {
    FamilyFilmAppTheme {
        RecommendedMovieCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            movie = Movie(
                title = "Movie Title",
                posterPath = "https://image.tmdb.org/t/p/original/ar2h87jlTfMlrDZefR3VFz1SfgH.jpg",
            ),
            navigateToDetailsScreen = {},
        )
    }
}
