package com.digitalsolution.familyfilmapp.ui.screens.recommend

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.digitalsolution.familyfilmapp.model.local.Movie
import com.digitalsolution.familyfilmapp.navigation.navtypes.DetailNavTypeDestination
import com.digitalsolution.familyfilmapp.ui.components.BottomBar
import com.digitalsolution.familyfilmapp.ui.components.RecommendedMovieCard
import com.digitalsolution.familyfilmapp.ui.screens.recommend.states.GenresBackendState
import com.digitalsolution.familyfilmapp.ui.screens.recommend.states.MovieUiState
import com.digitalsolution.familyfilmapp.ui.theme.FamilyFilmAppTheme

@Composable
fun RecommendScreen(navController: NavController, viewModel: RecommendViewModel = hiltViewModel()) {
    val recommendUiState by viewModel.state.collectAsStateWithLifecycle()
    val backendState by viewModel.recommendUIBackendState.observeAsState()

    Scaffold(
        bottomBar = { BottomBar(navController = navController) },
    ) { paddingValues ->
        RecommendContent(
            recommendUiState,
            backendState,
            modifier = Modifier.padding(paddingValues),
            navigationToDetail = { movie ->
                navController.navigate(DetailNavTypeDestination.getDestination(movie))
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecommendContent(
    movieState: MovieUiState,
    backendState: GenresBackendState?,
    modifier: Modifier = Modifier,
    navigationToDetail: (Movie) -> Unit,
) {
    Column(modifier = modifier.fillMaxSize()) {
        LazyVerticalGrid(columns = GridCells.Fixed(3)) {
            items(backendState!!.genreInfo) { item ->

                var isSelected by remember { mutableStateOf(false) }
                FilterChip(
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        selectedContainerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        selectedLabelColor = Color.White,
                    ),
                    onClick = {
                        isSelected = !isSelected
                    },
                    selected = isSelected,
                    label = {
                        Text(
                            item.name,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    modifier = Modifier.padding(horizontal = 4.dp),
                )
            }
        }

        Text(
            text = "Comedy",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
            modifier = Modifier
                .padding(10.dp)
                .padding(bottom = 4.dp),
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(horizontal = 10.dp)) {
            items(movieState.movies) { film ->
                RecommendedMovieCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    film,
                    navigateToDetailsScreen = {
                        navigationToDetail(
                            it,
                        )
                    },
                )
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun RecommendScreenPreview() {
    FamilyFilmAppTheme {
        RecommendScreen(navController = rememberNavController())
    }
}
