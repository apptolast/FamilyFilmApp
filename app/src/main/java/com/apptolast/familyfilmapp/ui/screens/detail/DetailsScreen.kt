package com.apptolast.familyfilmapp.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.PlaylistAddCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.apptolast.familyfilmapp.model.local.Movie
import com.apptolast.familyfilmapp.model.local.User
import com.apptolast.familyfilmapp.model.local.types.MovieStatus
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
                    Text(movie.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {

            MoviePoster(
                movie = movie,
                user = state.user,
                onStatusChange = { status ->
                    viewModel.updateMovieStatus(movie, status)
                },
            )
            MovieInfo(movie)
        }
    }
}

@Composable
fun MoviePoster(movie: Movie, user: User, onStatusChange: (MovieStatus) -> Unit = { }) {
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
        ButtonsContent(
            modifier = Modifier.align(Alignment.BottomEnd),
            movie = movie,
            user = user,
            onStatusChange = onStatusChange,
        )
    }
}

@Composable
fun MovieInfo(movie: Movie) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)) {
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
fun ButtonsContent(
    modifier: Modifier = Modifier,
    movie: Movie,
    user: User,
    onStatusChange: (MovieStatus) -> Unit = { },
) {
    Row(
        modifier = modifier.padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        CustomStatusButton(
            icon = Icons.Default.PlaylistAddCircle,
            isSelected = user.statusMovies[movie.id.toString()] == MovieStatus.ToWatch,
            onClick = { onStatusChange(MovieStatus.ToWatch) },
        )
        CustomStatusButton(
            icon = Icons.Default.Visibility,
            isSelected = user.statusMovies[movie.id.toString()] == MovieStatus.Watched,
            onClick = { onStatusChange(MovieStatus.Watched) },
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
    Box(
        modifier = Modifier
            .size(25.dp)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "+$age",
            color = Color.White,
        )
    }
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
