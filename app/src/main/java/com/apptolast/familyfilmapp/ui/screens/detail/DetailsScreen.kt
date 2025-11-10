package com.apptolast.familyfilmapp.ui.screens.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.PlaylistAddCheckCircle
import androidx.compose.material.icons.filled.PlaylistAddCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.apptolast.familyfilmapp.model.local.Movie
import com.apptolast.familyfilmapp.model.local.types.MovieStatus
import com.apptolast.familyfilmapp.ui.screens.home.BASE_URL
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme
import com.apptolast.familyfilmapp.ui.theme.redAgeMovie

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    movieId: Int,
    modifier: Modifier = Modifier,
    factoryProvider: DetailsViewModelFactoryProvider = hiltViewModel(),
    viewModel: DetailsViewModel = viewModel(
        factory = DetailsViewModel.provideFactory(
            assistedFactory = factoryProvider.detailsViewModelFactory,
            movieId = movieId,
        ),
    ),
    onBack: () -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.movie.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
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
        bottomBar = {
            val isToWatch = state.user.statusMovies[state.movie.id.toString()] == MovieStatus.ToWatch

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                CustomStatusButton(
                    text = "To Watch",
                    icon = if (isToWatch) Icons.Default.PlaylistAddCheckCircle else Icons.Default.PlaylistAddCircle,
                    isSelected = isToWatch,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.updateMovieStatus(state.movie, MovieStatus.ToWatch) },
                )

                Spacer(modifier = Modifier.width(14.dp))

                CustomStatusButton(
                    text = "Watched",
                    icon = Icons.Default.Visibility,
                    isSelected = state.user.statusMovies[state.movie.id.toString()] == MovieStatus.Watched,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.updateMovieStatus(state.movie, MovieStatus.Watched) },
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        modifier = modifier.consumeWindowInsets(PaddingValues(50.dp)),
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .consumeWindowInsets(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AsyncImage(
                model = "${BASE_URL}${state.movie.posterPath}",
                contentDescription = "Movie Poster",
                modifier = Modifier
                    .height(380.dp)
                    .clip(MaterialTheme.shapes.small),
                contentScale = ContentScale.Fit,
            )
            MovieInfo(movie = state.movie)
        }
    }
}

@Composable
fun MovieInfo(movie: Movie) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = movie.title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 2.dp),
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(end = 8.dp),
                ) {
                    Text(text = movie.releaseDate.take(4), fontWeight = FontWeight.Bold)
                    AnimatedVisibility(movie.adult) {
                        AgeRestrictionBadge(
                            age = 18,
                            color = redAgeMovie,
                        )
                    }
                }
            }

            Text(
                text = movie.overview,
                modifier = Modifier.fillMaxWidth(),
                fontSize = 16.sp,
                textAlign = TextAlign.Justify,
            )

            ProvidersContent(
                streamProviders = movie.streamProviders,
                buyProviders = movie.buyProviders,
                rentProviders = movie.rentProviders,
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
            .padding(5.dp),
        textAlign = TextAlign.Center,
    )
}

@Composable
fun CustomStatusButton(
    text: String,
    icon: ImageVector,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    val content: @Composable RowScope.() -> Unit = {
        DetailsButtonContent(icon = icon, text = text)
    }

    if (isSelected) {
        Button(
            onClick = onClick,
            modifier = modifier,
            content = content,
        )
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier,
            content = content,
        )
    }
}

@Composable
private fun RowScope.DetailsButtonContent(icon: ImageVector, text: String) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        modifier = Modifier.padding(end = 6.dp),
    )
    Text(text = text)
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun DetailsScreenPreview() {
    FamilyFilmAppTheme {
        MovieInfo(
            Movie().copy(
                title = "Esto es un titulo muy largo que no cabe en el cuadro",
                posterPath = "",
                releaseDate = "2022",
                adult = true,
            ),
        )
    }
}
