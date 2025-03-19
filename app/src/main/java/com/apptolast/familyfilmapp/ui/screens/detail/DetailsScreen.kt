package com.apptolast.familyfilmapp.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.PlaylistAddCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.apptolast.familyfilmapp.R
import com.apptolast.familyfilmapp.model.local.Movie
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.model.local.types.MovieStatus
import com.apptolast.familyfilmapp.navigation.Routes
import com.apptolast.familyfilmapp.ui.screens.home.BASE_URL
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    viewModel: DetailScreenViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    movie: Movie, // Datos de la película
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(movie.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Row {
                            CustomStatusButton(
                                icon = Icons.Default.PlaylistAddCircle,
                                isSelected = state.user.statusMovies[movie.id.toString()] == MovieStatus.ToWatch,
                                onClick = { viewModel.updateMovieStatus(movie, MovieStatus.ToWatch) },
                            )
                            CustomStatusButton(
                                icon = Icons.Default.Visibility,
                                isSelected = state.user.statusMovies[movie.id.toString()] == MovieStatus.Watched,
                                onClick = { viewModel.updateMovieStatus(movie, MovieStatus.Watched) },
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = Icons.AutoMirrored.Outlined.ArrowBack.toString(),
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            MoviePoster(movie = movie)
            MovieInfo(movie = movie)
        }
    }
}

@Composable
fun MoviePoster(movie: Movie) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp),
    ) {
        AsyncImage(
            model = "${BASE_URL}${movie.posterPath}",
            contentDescription = "Movie Poster",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
//            clipToBounds = true,
        )
    }
}

@Composable
fun MovieInfo(movie: Movie) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = movie.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = movie.releaseDate.take(4), fontWeight = FontWeight.Bold)
                AgeRestrictionBadge(
                    age = if (movie.adult) 18 else 0,
                    color = if (movie.adult) Color.Red else Color.Green,
                )
            }
        }

        Text(
            text = movie.overview,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            fontSize = 16.sp,
            textAlign = TextAlign.Justify,
        )
    }
}

@Composable
fun CustomStatusButton(
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit = {},
) {

    if (isSelected) {
        OutlinedIconButton(onClick = onClick) {
            Icon(
                imageVector = icon,
                contentDescription = null,
            )
        }
    } else {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = icon,
                contentDescription = null,
            )
        }
    }
}

@Composable
fun AgeRestrictionBadge(age: Int, color: Color) {
    Text(
        text = "+$age",
        color = Color.White,
        modifier = Modifier
            .background(
                color = color,
                shape = CircleShape,
            )
            .defaultMinSize(minWidth = 40.dp)
            .padding(6.dp),
        textAlign = TextAlign.Center,
    )
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun DetailsScreenPreview() {
    FamilyFilmAppTheme {
        MovieDetailScreen(
            movie = Movie().copy(
                id = 1,
                title = "Movie title",
                posterPath = "/poster.jpg",
                adult = true,
                releaseDate = "2023-01-01",
            ),
        )
    }
}
