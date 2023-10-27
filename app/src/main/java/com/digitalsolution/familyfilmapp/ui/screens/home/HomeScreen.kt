package com.digitalsolution.familyfilmapp.ui.screens.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.digitalsolution.familyfilmapp.R
import com.digitalsolution.familyfilmapp.model.local.Movie
import com.digitalsolution.familyfilmapp.navigation.Routes
import com.digitalsolution.familyfilmapp.ui.theme.FamilyFilmAppTheme
import kotlin.system.exitProcess

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val loginState by viewModel.isUserLoggedIn.collectAsStateWithLifecycle()
    val homeUiState by viewModel.homeUiState.collectAsStateWithLifecycle()

    BackHandler(true) {
        exitProcess(0)
    }

    LaunchedEffect(key1 = loginState) {
        if (!loginState) {
            navController.navigateUp()
        }
    }

    HomeContent(
        homeUiState = homeUiState,
        navigateToDetailsScreen = { navController.navigate(Routes.Details.routes) },
    )
}

@Composable
fun HomeContent(
    homeUiState: HomeUiState,
    navigateToDetailsScreen: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        RowMovie(
            title = stringResource(R.string.home_text_to_see),
            icon = Icons.Default.ListAlt,
            movies = homeUiState.seen,
            navigateToDetailsScreen = navigateToDetailsScreen,
            modifier = Modifier.weight(1f),
        )
        RowMovie(
            title = stringResource(R.string.home_text_seen),
            icon = Icons.Default.Visibility,
            movies = homeUiState.forSeen,
            navigateToDetailsScreen = navigateToDetailsScreen,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun RowMovie(
    title: String,
    icon: ImageVector,
    movies: List<Movie>,
    navigateToDetailsScreen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
            )
            Icon(imageVector = icon, contentDescription = icon.toString())
        }
        LazyRow(modifier = Modifier.padding(bottom = 15.dp)) {
            items(movies) { movie ->
                HomeItem(
                    movie = movie,
                    navigateToDetailsScreen = navigateToDetailsScreen,
                )
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun HomeScreenPreview() {
    FamilyFilmAppTheme {
        HomeContent(
            HomeUiState(
                seen = listOf(Movie(image = "", "Movie title")),
                forSeen = listOf(Movie(image = "", "Movie title")),
                groups = listOf("Group 1", "Group 2"),
                isLoading = true,
                errorMessage = null,
            ),
        ) {}
    }
}
