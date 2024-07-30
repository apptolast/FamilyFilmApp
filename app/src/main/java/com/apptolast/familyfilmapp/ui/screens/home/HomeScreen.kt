package com.apptolast.familyfilmapp.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.apptolast.familyfilmapp.model.local.Movie
import com.apptolast.familyfilmapp.navigation.navtypes.DetailNavTypeDestination
import com.apptolast.familyfilmapp.ui.components.BottomBar
import com.apptolast.familyfilmapp.ui.components.tabgroups.TabGroupsViewModel
import com.apptolast.familyfilmapp.ui.theme.FamilyFilmAppTheme

@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = hiltViewModel(),
    tabViewModel: TabGroupsViewModel = hiltViewModel(),
) {

    Scaffold(
        bottomBar = { BottomBar(navController = navController) },
    ) { paddingValues ->
        HomeContent(
            navigateToDetailsScreen = { movie ->
                val groupId = 0 ?: -1
                navController.navigate(DetailNavTypeDestination.getDestination(movie, groupId))
            },
            modifier = Modifier.padding(paddingValues),
        )
    }
}

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    navigateToDetailsScreen: (Movie) -> Unit,
) {
    Column(modifier = modifier.fillMaxSize()) {
        RowMovie(
            movies = emptyList(),
            navigateToDetailsScreen = {
                navigateToDetailsScreen(it)
            },
            modifier = Modifier.weight(1f),
        )
        RowMovie(
            movies = emptyList(),
            navigateToDetailsScreen = navigateToDetailsScreen,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun RowMovie(
    movies: List<Movie>,
    navigateToDetailsScreen: (Movie) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        LazyRow(modifier = Modifier.padding(bottom = 15.dp)) {
            items(movies) { movie ->
                HomeItem(
                    movie = movie,
                    navigateToDetailsScreen = {
                        navigateToDetailsScreen(movie)
                    },
                )
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun HomeContentPreview() {
    FamilyFilmAppTheme {
        HomeContent(
            navigateToDetailsScreen = { },
        )
    }
}
