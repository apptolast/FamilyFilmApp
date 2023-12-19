package com.apptolast.familyfilmapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.apptolast.familyfilmapp.model.local.Movie
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme

@Composable
fun DetailsScreen(
    navController: NavController,
    movie: Movie,
    groupId: Int,
    viewModel: DetailScreenViewModel = hiltViewModel(),
) {
    val detailScreenUIState by viewModel.uiState.collectAsStateWithLifecycle()

    val lazyListState = rememberLazyListState()
    val snackBarHostState = remember { SnackbarHostState() }
    var scrolledY = 0f
    var previousOffset = 0

    if (detailScreenUIState.successMovieToWatchList.isNotBlank() ||
        detailScreenUIState.successMovieToViewList.isNotBlank() ||
        (detailScreenUIState.errorMessage?.error?.isNotBlank() ?: "") as Boolean
    ) {
        LaunchedEffect(
            key1 = detailScreenUIState.successMovieToViewList,
        ) {
            snackBarHostState.showSnackbar(
                detailScreenUIState.successMovieToViewList,
                "Close",
                true,
                SnackbarDuration.Long,
            )
        }

        LaunchedEffect(key1 = detailScreenUIState.successMovieToWatchList) {
            snackBarHostState.showSnackbar(
                detailScreenUIState.successMovieToWatchList,
                "Close",
                true,
                SnackbarDuration.Long,
            )
        }

        LaunchedEffect(key1 = detailScreenUIState.errorMessage?.error) {
            detailScreenUIState.errorMessage?.let {
                snackBarHostState.showSnackbar(
                    it.error,
                    "Close",
                    true,
                    SnackbarDuration.Long,
                )
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            state = lazyListState,
        ) {
            item {
                AsyncImage(
                    model = movie.image,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(bottomStartPercent = 5, bottomEndPercent = 5))
                        .height(250.dp)
                        .graphicsLayer {
                            scrolledY += lazyListState.firstVisibleItemScrollOffset - previousOffset
                            translationY = scrolledY * 0.5f
                            previousOffset = lazyListState.firstVisibleItemScrollOffset
                        },
                    contentScale = ContentScale.Crop,
                )
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(text = "2023")
                        Text(text = "1h 34min")
                        Text(text = "+18")
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Button(
                            onClick = { viewModel.addMovieToSeenList(groupId, movie.id) },
                            modifier = Modifier.weight(1f),
                        ) {
                            DetailsButtonContent(icon = Icons.Default.Add, text = "Add to see")
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        OutlinedButton(
                            onClick = { viewModel.addMovieToWatchList(groupId, movie.id) },
                            modifier = Modifier.weight(1f),
                        ) {
                            DetailsButtonContent(icon = Icons.Default.Visibility, text = "Don't seen")
                        }
                    }
                    Text(
                        text = "Description",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(top = 15.dp),
                    )
                    Text(
                        text = movie.synopsis,
                        modifier = Modifier.padding(vertical = 15.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailsButtonContent(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 3.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.padding(end = 6.dp),
        )
        Text(text = text)
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true,
)
@Composable
private fun DetailsScreenPreview() {
    FamilyFilmAppTheme {
        DetailsScreen(
            navController = rememberNavController(),
            movie = Movie(),
            groupId = -1,
            viewModel = hiltViewModel(),
        )
    }
}
